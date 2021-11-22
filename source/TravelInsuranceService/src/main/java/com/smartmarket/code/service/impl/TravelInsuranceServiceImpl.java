package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.UpdateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.ResponseError;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.util.*;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TravelInsuranceServiceImpl implements TravelInsuranceService {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    MapperUtils mapperUtils;

    @Autowired
    HostConstants hostConstants;

    @Autowired
    BICTransactionService bicTransactionService;

    @Autowired
    SetResponseUtils setResponseUtils;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    BICTransactionExceptionService bicTransactionExceptionService;


    @Override
    public ResponseEntity<?> createTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {

        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryCreateBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryCreateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryCreateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));

        BaseResponse response = new BaseResponse();
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        String clientIp =  Utils.getClientIp(request) ;
        String typeTransaction = Constant.TYPE_CREATE;

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        //check validate json request
        ValidateRequest.checkValidCreate(createTravelInsuranceBICRequest);

        //get current start time
        ObjectMapper mapper = new ObjectMapper();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();

        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        Gson gson = new Gson();
        responseCreate = gson.toJson(createTravelInsuranceToBIC);
        JSONObject transactionDetail = new JSONObject(responseCreate);


        String timeDuration = DateTimeUtils.getElapsedTimeStr(System.currentTimeMillis());

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        //get token from database
        String token = authorizationService.getToken();
        if (StringUtils.isEmpty(token)) {
            throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long startTime = System.currentTimeMillis();
        //post Data to BIC
        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(environment.getRequiredProperty("api.createTravelBIC"), null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryCreateBIC);
//            ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody("https://app.bic.vn:1111/EbizApiTest/api/v1/TRV/Create", null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryCreateBIC);
        //get duration time
        String timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);

        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        EJson jsonObjectReponseCreate = null;

        if (jsonResultCreateBIC != null && jsonResultCreateBIC.getBody() != null) {
            jsonObjectReponseCreate = new EJson(jsonResultCreateBIC.getBody());
            JSONObject responseBodyFromBIC = new JSONObject(jsonResultCreateBIC.getBody());

            //check valid response
            boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectReponseCreate);

            if (isValidFormatResponse) {

                if (jsonResultCreateBIC.getStatusCode() == HttpStatus.OK
                        && jsonObjectReponseCreate != null
                        && jsonObjectReponseCreate.getBoolean("succeeded") == true) {

                    //set response data to client
                    response = setResponseUtils.setResponse(response, createTravelInsuranceBICRequest,
                            createTravelInsuranceBICResponse, jsonResultCreateBIC);

                    //convert response BIC
                    String responseBody = mapper.writeValueAsString(response);
                    JSONObject transactionDetailResponse = new JSONObject(responseBody);

                    //create BICTransaction
                    bicTransactionService.createBICTransactionFromCreateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultCreateBIC.getStatusCode().toString(),clientIp,typeTransaction);

                    //logResponse vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","response",
                            responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    //calculate time duration
                    String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                    //logResponse vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(), "BIC", "smartMarket", "client",
                            messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                            "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                            response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
                    logService.createSOALog2(soaObject);

                } else {
                    EJson dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));

                    //set Response error
                    ResponseError responseError = setResponseUtils.setResponseError(createTravelInsuranceBICRequest,
                            jsonResultCreateBIC, dataResponse);

                    String responseBody = mapper.writeValueAsString(responseError);
                    JSONObject transactionDetailResponse = new JSONObject(responseBody);

                    //create BICTransaction
                    bicTransactionService.createBICTransactionFromCreateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultCreateBIC.getStatusCode().toString(),clientIp,typeTransaction);

                    //logResponseError vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC", "response",
                            responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    //calculate time duration
                    String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                    //logResponseError vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                            messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                            "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                            responseError.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);


                    return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }
            } else {

                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","response", responseBodyFromBIC,
                        logTimestamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);

//                    throw new CustomException("",jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), responseBodyFromBIC , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
                throw new CustomException("Đơn hàng đã được tạo trên hệ thống!",jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }
        } else {
            //logResponse vs BIC
            TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC" ,"response", null,
                    logTimestamp, messageTimestamp, timeDurationBIC);
            logService.createTargetLog(tarObject);

            throw new CustomException("", jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
        }
        //check format reponse from BIC


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getTravelBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //time start
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetOrderBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryGetOrderBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryGetOrderBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));

        BaseResponse response = new BaseResponse();

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        //check validate json request
        ValidateRequest.checkValidInquire(queryTravelInsuranceBICRequest);
        //delcare used value
        CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;
        ObjectMapper mapper = new ObjectMapper();

        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        //properties log
        String orderID = queryTravelInsuranceBICRequest.getDetail().getOrderId();
        String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
        String responseStatus = Integer.toString(responseSelvet.getStatus());
        org.json.JSONObject transactionDetail = new org.json.JSONObject();
        transactionDetail.put("orderId", orderID);
        transactionDetail.put("orderRef", orderReference);
        String operationNameTarget =  "" ;


        //get token from database
        String token = authorizationService.getToken();

        if (StringUtils.isEmpty(token)) {
            throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, queryTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(System.currentTimeMillis());

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        Map<String, Object> map = new HashMap<>();
        ResponseEntity<String> resultBIC = null;
        long startTime = 0;
        String timeDurationBIC = null;
        if (queryTravelInsuranceBICRequest.getDetail() != null) {
            if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L)) {
                String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderId();
                if (orderId != null) {
                    map.put("id", orderId);
                    startTime = System.currentTimeMillis();
                    resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOrderId"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                    timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                    operationNameTarget =  "getTravelBICByOrderId" ;
                }
            }
            if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L)) {
                orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
                if (orderReference != null) {
                    map.put("id", orderReference);
                    startTime = System.currentTimeMillis();
                    resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOderReference"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                    timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                    operationNameTarget =  "getTravelBICByOderReference" ;
                }

            }
        }

        if (resultBIC != null && resultBIC.getBody() != null) {
            EJson jsonObjectResultBIC = new EJson(resultBIC.getBody());
            JSONObject jsonObjectResultBICLog = new JSONObject(resultBIC.getBody());

            //check valid response
            boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectResultBIC);

            if (!isValidFormatResponse) {
                if (resultBIC.getStatusCode() == HttpStatus.OK && resultBIC.getBody() != null) {
                    createTravelInsuranceBICResponse = mapperUtils.queryCreateObjectToBIC(queryTravelInsuranceBICRequest, resultBIC, token, queryTravelInsuranceBICRequest.getRequestId());

                    String responseCreate = mapper.writeValueAsString(createTravelInsuranceBICResponse);
                    JSONObject responseBodyBIC = new JSONObject(responseCreate);

                    //set response to client
                    response = setResponseUtils.setResponseInquery(response, createTravelInsuranceBICResponse,queryTravelInsuranceBICRequest);
                    String responseBody = mapper.writeValueAsString(response);
                    JSONObject transactionDetailResponse = new JSONObject(responseBody);

                    //logResponse vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance",operationNameTarget,"response",
                            jsonObjectResultBICLog, logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    //calculate time duration
                    String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                    //logResponse vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                            messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                            "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                            response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);

                }
            } else {
                //logResponse vs BIC
                EJson dataResponse = (jsonObjectResultBIC.getJSONObject("data"));

                TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","response", jsonObjectResultBICLog,
                        logTimestamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);
                throw new CustomException("Not found order",resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), jsonObjectResultBICLog , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }
        } else {
            //logResponse vs BIC

            TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","response", null,
                    logTimestamp, messageTimestamp, timeDurationBIC);
            logService.createTargetLog(tarObject);

            throw new CustomException("", resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), null,  ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateTravelBIC(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //start time
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryUpdateBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryUpdateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.updateTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryUpdateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.updateTravelBIC")));

        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();

        String clientIp =  Utils.getClientIp(request) ;
        String typeTransaction = Constant.TYPE_UPDATE;

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());


        //check validate json request
        ValidateRequest.checkValidUpdate(updateTravelInsuranceBICRequest);

        //get log time
        String logtimeStamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logtimeStamp;

        //Update BIC
        UpdateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        Gson gson = new Gson();
        responseCreate = gson.toJson(updateTravelInsuranceToBIC);
        JSONObject transactionDetail = new JSONObject(responseCreate);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(System.currentTimeMillis());
        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","request",
                transactionDetail, logtimeStamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        String token = authorizationService.getToken();
        if (StringUtils.isEmpty(token)) {
            throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //getOrderId to request BIC
        String orderID = null;
        if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null) {
            orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
        }

        long startTime = System.currentTimeMillis();
        //post Data to BIC
        ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,environment.getRequiredProperty("api.updateTravelBIC"), null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId(), clientHttpRequestFactoryUpdateBIC);
//            ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,"https://app.bic.vn:1111/EbizApiTest/api/v1/TRV/Update", null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId(), clientHttpRequestFactoryUpdateBIC);

        //get duration time
        String timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);

        //set response data to client
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);

        if (jsonResultPutBIC != null && jsonResultPutBIC.getBody() != null) {
            EJson jsonObjectReponseUpdate = new EJson(jsonResultPutBIC.getBody());
            JSONObject responseBodyFromBIC = new JSONObject(jsonResultPutBIC.getBody());

            boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectReponseUpdate);

            //check format reponse from BIC
            if (isValidFormatResponse) {
                if (jsonResultPutBIC.getStatusCode() == HttpStatus.OK
                        && jsonObjectReponseUpdate != null
                        && jsonObjectReponseUpdate.getBoolean("succeeded") == true) {

                    //set response client
                    response = setResponseUtils.setResponseUpdate(response, updateTravelInsuranceBICRequest,
                            createTravelInsuranceBICResponse, jsonResultPutBIC);

                    //create BICTransaction
                    bicTransactionService.createBICTransactionFromUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultPutBIC.getStatusCode().toString(),clientIp,typeTransaction);

                    //log properties
                    String responseBody = mapper.writeValueAsString(response);
                    JSONObject transactionDetailResponse = new JSONObject(responseBody);

                    //logResponse vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response",
                            responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    //calculate time duration
                    String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                    //logResponse vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                            messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                            "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                            response.getResultMessage(), logtimeStamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);
                } else {
                    EJson dataResponse = (jsonObjectReponseUpdate.getJSONObject("data"));

                    //set data reponse error
                    ResponseError responseError = setResponseUtils.setResponseUpdateError(updateTravelInsuranceBICRequest,
                            jsonResultPutBIC, dataResponse);

                    //set properties to log
                    String responseBody = mapper.writeValueAsString(responseError);
                    JSONObject transactionDetailResponse = new JSONObject(responseBody);

                    //create BICTransaction
                    bicTransactionService.createBICTransactionFromUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultPutBIC.getStatusCode().toString(),clientIp,typeTransaction);

                    //logResponseError vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response",
                            responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    //calculate time duration
                    String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                    //logResponseError vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                            messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                            "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                            responseError.getResultMessage(), logtimeStamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);


                    return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }
            } else {
                //logResponse vs BIC

                TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance", "updateTravelBIC","response", responseBodyFromBIC,
                        logtimeStamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);
                throw new CustomException("",jsonResultPutBIC.getStatusCode(), updateTravelInsuranceBICRequest.getRequestId(), responseBodyFromBIC , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);

            }
        } else {
            //logResponse vs BIC

            TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response", null,
                    logtimeStamp, messageTimestamp, timeDurationBIC);
            logService.createTargetLog(tarObject);

            throw new CustomException("", jsonResultPutBIC.getStatusCode(), updateTravelInsuranceBICRequest.getRequestId(), null,  ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> createOrderOutbox(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, String clientIp, String clientId,
                                               Long startTime, String hostName) throws JsonProcessingException, APIAccessException, Exception {

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryCreateBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryCreateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryCreateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));

        BaseResponse response = new BaseResponse();
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        String typeTransaction = Constant.TYPE_CREATE;
        try {
            //check validate json request
            ValidateRequest.checkValidCreate(createTravelInsuranceBICRequest);

            //get current start time
            ObjectMapper mapper = new ObjectMapper();
            CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();

            //Create BIC
            CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            Gson gson = new Gson();
            responseCreate = gson.toJson(createTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            //get token from database
            String token = authorizationService.getToken();
            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,HttpStatus.INTERNAL_SERVER_ERROR);
            }

            ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(environment.getRequiredProperty("api.createTravelBIC"), null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryCreateBIC);
//            ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody("https://app.bic.vn:1111/EbizApiTest/api/v1/TRV/Create", null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryCreateBIC);
            EJson jsonObjectReponseCreate = null;
            String timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);


            if (jsonResultCreateBIC != null && jsonResultCreateBIC.getBody() != null) {
                jsonObjectReponseCreate = new EJson(jsonResultCreateBIC.getBody());
                JSONObject responseBodyFromBIC = new JSONObject(jsonResultCreateBIC.getBody());

                //check valid response
                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectReponseCreate);

                if (isValidFormatResponse) {

                    if (jsonResultCreateBIC.getStatusCode() == HttpStatus.OK
                            && jsonObjectReponseCreate != null
                            && jsonObjectReponseCreate.getBoolean("succeeded") == true) {

                        //set response data to client
                        response = setResponseUtils.setResponse(response, createTravelInsuranceBICRequest,
                                createTravelInsuranceBICResponse, jsonResultCreateBIC);

                        //convert response BIC
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromCreateTravelOutbox(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultCreateBIC.getStatusCode().toString(),clientIp,typeTransaction,clientId);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","response",
                                responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(), "BIC", "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, jsonResultCreateBIC.getStatusCode().toString(), response.getResultCode(),
                                response.getResultMessage(), logTimestamp, hostName, clientIp, "createTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);

                    } else {
                        EJson dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));

                        //set Response error
                        ResponseError responseError = setResponseUtils.setResponseError(createTravelInsuranceBICRequest,
                                jsonResultCreateBIC, dataResponse);

                        String responseBody = mapper.writeValueAsString(responseError);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromCreateTravelOutbox(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultCreateBIC.getStatusCode().toString(),clientIp,typeTransaction,clientId);

                        //logResponseError vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC", "response",
                                responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponseError vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, jsonResultCreateBIC.getStatusCode().toString(), responseError.getResultCode(),
                                responseError.getResultMessage(), logTimestamp, hostName, clientIp,"createTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);

                        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    //logResponse vs BIC
                    TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC","response", responseBodyFromBIC,
                            logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

//                    throw new CustomException("",jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), responseBodyFromBIC , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
                    throw new CustomException("Đơn hàng đã được tạo trên hệ thống!",jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
                }
            } else {
                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"BICtravelinsurance","createTravelBIC" ,"response", null,
                        logTimestamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);

                throw new CustomException("", jsonResultCreateBIC.getStatusCode(), createTravelInsuranceBICRequest.getRequestId(), null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }
            //check format reponse from BIC

        } catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString(),clientId);
                        bicTransactionExceptionService.createPendingBICTransactionFromRequestCreate(createTravelInsuranceBICRequest,"createTravelInsuranceBIC");
                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                        bicTransactionExceptionService.createPendingBICTransactionFromRequestCreate(createTravelInsuranceBICRequest,"createTravelInsuranceBIC");
                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new APIResponseException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new InvalidInputException(ex.getMessage(), createTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                }else {
                    bicTransactionExceptionService.createBICTransactionFromRequestCreate(createTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw ex ;
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> updateOrderOutbox(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, String clientIp, String clientId,
                                               Long startTime, String hostName) throws JsonProcessingException, APIAccessException {

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryUpdateBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryUpdateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.updateTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryUpdateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.updateTravelBIC")));

        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
        BaseResponse response = new BaseResponse();
        ObjectMapper mapper = new ObjectMapper();
        String typeTransaction = Constant.TYPE_UPDATE;
        try {
            //check validate json request
            ValidateRequest.checkValidUpdate(updateTravelInsuranceBICRequest);

            //get log time
            String logtimeStamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logtimeStamp;

            //Update BIC
            UpdateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            Gson gson = new Gson();
            responseCreate = gson.toJson(updateTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","request",
                    transactionDetail, logtimeStamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            String token = authorizationService.getToken();
            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //getOrderId to request BIC
            String orderID = null;
            if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null) {
                orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
            }

            //post Data to BIC
            ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,environment.getRequiredProperty("api.updateTravelBIC"), null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId(), clientHttpRequestFactoryUpdateBIC);
//            ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,"https://app.bic.vn:1111/EbizApiTest/api/v1/TRV/Update", null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId(), clientHttpRequestFactoryUpdateBIC);

            //get duration time
            String timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);

            //set response data to client
            if (jsonResultPutBIC != null && jsonResultPutBIC.getBody() != null) {
                EJson jsonObjectReponseUpdate = new EJson(jsonResultPutBIC.getBody());
                JSONObject responseBodyFromBIC = new JSONObject(jsonResultPutBIC.getBody());

                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectReponseUpdate);

                //check format reponse from BIC
                if (isValidFormatResponse) {
                    if (jsonResultPutBIC.getStatusCode() == HttpStatus.OK
                            && jsonObjectReponseUpdate != null
                            && jsonObjectReponseUpdate.getBoolean("succeeded") == true) {

                        //set response client
                        response = setResponseUtils.setResponseUpdate(response, updateTravelInsuranceBICRequest,
                                createTravelInsuranceBICResponse, jsonResultPutBIC);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromUpdateTravelOubox(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultPutBIC.getStatusCode().toString(),clientIp,typeTransaction,clientId);

                        //log properties
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response",
                                responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, jsonResultPutBIC.getStatusCode().toString(), response.getResultCode(),
                                response.getResultMessage(), logtimeStamp, hostName, clientIp,"updateTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);

                    } else {
                        EJson dataResponse = (jsonObjectReponseUpdate.getJSONObject("data"));

                        ResponseError responseError = setResponseUtils.setResponseUpdateError(updateTravelInsuranceBICRequest,
                                jsonResultPutBIC, dataResponse);

                        //set properties to log
                        String responseBody = mapper.writeValueAsString(responseError);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromUpdateTravelOubox(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultPutBIC.getStatusCode().toString(),clientIp,typeTransaction,clientId);

                        //logResponseError vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response",
                                responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponseError vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, jsonResultPutBIC.getStatusCode().toString(), responseError.getResultCode(),
                                responseError.getResultMessage(), logtimeStamp, hostName, clientIp,"updateTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);

                        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance", "updateTravelBIC","response", responseBodyFromBIC,
                            logtimeStamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);
                    throw new CustomException("",jsonResultPutBIC.getStatusCode(), updateTravelInsuranceBICRequest.getRequestId(), responseBodyFromBIC , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);

                }
            } else {
                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","updateTravelBIC","response", null,
                        logtimeStamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);
                throw new CustomException("", jsonResultPutBIC.getStatusCode(), updateTravelInsuranceBICRequest.getRequestId(), null,  ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString(),clientId);
                        bicTransactionExceptionService.createPendingBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest,"updateTravelInsuranceBIC");
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                        bicTransactionExceptionService.createPendingBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest,"updateTravelInsuranceBIC");
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new APIResponseException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new InvalidInputException(ex.getMessage(), updateTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                }
                else {
                    bicTransactionExceptionService.createBICTransactionFromRequestUpdate(updateTravelInsuranceBICRequest, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ex.getMessage(),clientId);
                    throw ex ;
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> getOrderOutbox(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, String clientIp, String clientId,
                                            Long startTime, String hostName)
            throws JsonProcessingException, APIAccessException{

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetOrderBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryGetOrderBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryGetOrderBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));

        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();

        try {

            //check validate json request
            ValidateRequest.checkValidInquire(queryTravelInsuranceBICRequest);
            //delcare used value
            CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;

            // declare value for log
            //get time log
            String logTimestamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logTimestamp;

            //properties log
            String orderID = queryTravelInsuranceBICRequest.getDetail().getOrderId();
            String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
            org.json.JSONObject transactionDetail = new org.json.JSONObject();
            transactionDetail.put("orderId", orderID);
            transactionDetail.put("orderRef", orderReference);
            String operationNameTarget =  "" ;

            //get token from database
            String token = authorizationService.getToken();

            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, queryTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            Map<String, Object> map = new HashMap<>();
            ResponseEntity<String> resultBIC = null;
            String timeDurationBIC = null;
            if (queryTravelInsuranceBICRequest.getDetail() != null) {
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L)) {
                    String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderId();
                    if (orderId != null) {
                        map.put("id", orderId);
                        startTime = System.currentTimeMillis();
                        resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOrderId"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                        timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                        operationNameTarget =  "getTravelBICByOrderId" ;
                    }
                }
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L)) {
                    orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
                    if (orderReference != null) {
                        map.put("id", orderReference);
                        startTime = System.currentTimeMillis();
                        resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOderReference"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                        timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                        operationNameTarget =  "getTravelBICByOderReference" ;
                    }

                }
            }

            if (resultBIC != null && resultBIC.getBody() != null) {
                EJson jsonObjectResultBIC = new EJson(resultBIC.getBody());
                JSONObject jsonObjectResultBICLog = new JSONObject(resultBIC.getBody());

                //check valid response
                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectResultBIC);

                if (!isValidFormatResponse) {
                    if (resultBIC.getStatusCode() == HttpStatus.OK && resultBIC.getBody() != null) {
                        createTravelInsuranceBICResponse = mapperUtils.queryCreateObjectToBIC(queryTravelInsuranceBICRequest, resultBIC, token, queryTravelInsuranceBICRequest.getRequestId());

                        String responseCreate = mapper.writeValueAsString(createTravelInsuranceBICResponse);
                        JSONObject responseBodyBIC = new JSONObject(responseCreate);

                        //set response to client
                        response = setResponseUtils.setResponseInquery(response, createTravelInsuranceBICResponse,queryTravelInsuranceBICRequest);
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance",operationNameTarget,"response",
                                jsonObjectResultBICLog, logTimestamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);
                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, resultBIC.getStatusCode().toString(), response.getResultCode(),
                                response.getResultMessage(), logTimestamp, hostName, clientIp,"getTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);
                    }
                } else {
                    //logResponse vs BIC
                    EJson dataResponse = (jsonObjectResultBIC.getJSONObject("data"));

                    TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","response", jsonObjectResultBICLog,
                            logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    throw new CustomException("Not found order",resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), jsonObjectResultBICLog , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
                }
            } else {
                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "BICtravelinsurance","getTravelBIC","response", null,
                        logTimestamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);

                throw new CustomException("", resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), null,  ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    throw new APIResponseException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    throw new InvalidInputException(ex.getMessage(), queryTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                } else {
                    throw ex ;
                }

            } catch (JDBCConnectionException jdbcConnect) {
                if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> getJobOutbox(BaseJobDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest,Long startTime)
            throws JsonProcessingException, APIAccessException{

        //SET TIMEOUT
        //set Time out get create api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetOrderBIC = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryGetOrderBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));
        //Read timeout
        clientHttpRequestFactoryGetOrderBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.getTravelBIC")));

        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();


        try {
            //check validate json request
            ValidateRequest.checkValidJobInquire(queryTravelInsuranceBICRequest);
            //delcare used value
            CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;

            // declare value for log
            //get time log
            String logTimestamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logTimestamp;

            //properties log
            String orderID = queryTravelInsuranceBICRequest.getDetail().getOrderId();
            String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
//            String responseStatus = Integer.toString(responseSelvet.getStatus());
            org.json.JSONObject transactionDetail = new org.json.JSONObject();
            transactionDetail.put("orderId", orderID);
            transactionDetail.put("orderRef", orderReference);
            String operationNameTarget =  "" ;

            //get token from database
            String token = authorizationService.getToken();

            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, queryTravelInsuranceBICRequest.getRequestId(),null,ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), "", "BICtravelinsurance","getTravelBIC","request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            Map<String, Object> map = new HashMap<>();
            ResponseEntity<String> resultBIC = null;
            String timeDurationBIC = null;
            if (queryTravelInsuranceBICRequest.getDetail() != null) {
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L)) {
                    String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderId();
                    if (orderId != null) {
                        map.put("id", orderId);
                        startTime = System.currentTimeMillis();
                        resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOrderId"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                        timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                        operationNameTarget =  "getTravelBICByOrderId" ;
                    }
                }
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L)) {
                    orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
                    if (orderReference != null) {
                        map.put("id", orderReference);
                        startTime = System.currentTimeMillis();
                        resultBIC = apiUtils.getApiWithParam(environment.getRequiredProperty("api.getTravelBICByOderReference"), null, map, token, queryTravelInsuranceBICRequest.getRequestId(),clientHttpRequestFactoryGetOrderBIC);
                        timeDurationBIC = DateTimeUtils.getElapsedTimeStr(startTime);
                        operationNameTarget =  "getTravelBICByOderReference" ;
                    }

                }
            }

            if (resultBIC != null && resultBIC.getBody() != null) {
                EJson jsonObjectResultBIC = new EJson(resultBIC.getBody());
                JSONObject jsonObjectResultBICLog = new JSONObject(resultBIC.getBody());

                //check valid response
                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectResultBIC);

                if (!isValidFormatResponse) {
                    if (resultBIC.getStatusCode() == HttpStatus.OK && resultBIC.getBody() != null) {
                        createTravelInsuranceBICResponse = mapperUtils.queryCreateJobObjectToBIC(queryTravelInsuranceBICRequest, resultBIC, token, queryTravelInsuranceBICRequest.getRequestId());

                        String responseCreate = mapper.writeValueAsString(createTravelInsuranceBICResponse);
                        JSONObject responseBodyBIC = new JSONObject(responseCreate);

                        //set response to client
                        response = setResponseUtils.setResponseJobInquery(response, createTravelInsuranceBICResponse,queryTravelInsuranceBICRequest);
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), "", "BICtravelinsurance",operationNameTarget,"response",
                                jsonObjectResultBICLog, logTimestamp, messageTimestamp, timeDurationBIC);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);
                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", queryTravelInsuranceBICRequest.getRequestId(), "", null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, resultBIC.getStatusCode().toString(), response.getResultCode(),
                                response.getResultMessage(), logTimestamp, "", "","getTravelInsuranceAsync");
                        logService.createSOALog2(soaObject);
                    }
                } else {
                    //logResponse vs BIC
                    EJson dataResponse = (jsonObjectResultBIC.getJSONObject("data"));

                    TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(),"", "BICtravelinsurance","getTravelBIC","response", jsonObjectResultBICLog,
                            logTimestamp, messageTimestamp, timeDurationBIC);
                    logService.createTargetLog(tarObject);

                    throw new CustomException("Not found order",resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), jsonObjectResultBICLog , ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
                }
            } else {
                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), "", "BICtravelinsurance","getTravelBIC","response", null,
                        logTimestamp, messageTimestamp, timeDurationBIC);
                logService.createTargetLog(tarObject);

                throw new CustomException("", resultBIC.getStatusCode(), queryTravelInsuranceBICRequest.getRequestId(), null,  ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG,HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    throw new APIResponseException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    throw new InvalidInputException(ex.getMessage(), queryTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                } else {
                    throw ex ;
                }

            } catch (JDBCConnectionException jdbcConnect) {
                if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
