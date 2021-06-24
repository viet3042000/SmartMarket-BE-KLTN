package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.util.*;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
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
    BICTransactionExceptionService bicTransactionExceptionService;

    @Override
    public ResponseEntity<?> createTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {

        long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        BaseResponse response = new BaseResponse();
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        try {
            //check validate json request
            ValidateRequest.checkValidCreate(createTravelInsuranceBICRequest);

            //get current start time
            ObjectMapper mapper = new ObjectMapper();
            CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();

            //Create BIC
            CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            responseCreate = mapper.writeValueAsString(createTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null,createTravelInsuranceBICRequest.getRequestId(),createTravelInsuranceBICRequest.getRequestTime(), "CreateTravelInsuranceBIC","request",
                    transactionDetail, logTimestamp, messageTimestamp,timeDuration);
            logService.createTargetLog(tarObjectRequest);

            //get token from database
            String token = authorizationService.getTokenFromDatabase();
            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId());
            }

            //post Data to BIC
            ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(hostConstants.BIC_HOST_CREATE, null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId());


            int status = responseSelvet.getStatus();
            String responseStatus = Integer.toString(status);
            EJson jsonObjectReponseCreate = null;

            if (jsonResultCreateBIC != null && jsonResultCreateBIC.getBody() != null ) {
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
                        bicTransactionService.createBICTransactionFromCreateorUpdateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultCreateBIC.getStatusCode().toString());

                        //get duration time
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null,createTravelInsuranceBICRequest.getRequestId(),createTravelInsuranceBICRequest.getRequestTime(),"CreateTravelInsuranceBIC", "response",
                                responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationResponse);
                        logService.createTargetLog(tarObject);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(), "BIC", "smartMarket","client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);

                    } else {
                        EJson dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));

                        //set Response error
                        ReponseError responseError = new ReponseError();
                        responseError = setResponseUtils.setResponseError(responseError,createTravelInsuranceBICRequest,
                                jsonResultCreateBIC,dataResponse );

                        String responseBody = mapper.writeValueAsString(responseError);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromCreateorUpdateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultCreateBIC.getStatusCode().toString());

                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponseError vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null,createTravelInsuranceBICRequest.getRequestId(),createTravelInsuranceBICRequest.getRequestTime(),"CreateTravelInsuranceBIC", "response",
                                responseBodyFromBIC, logTimestamp, messageTimestamp, timeDurationResponse);
                        logService.createTargetLog(tarObject);

                        //logResponseError vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                                responseError.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);

                        return new ResponseEntity<>(responseError, HttpStatus.OK);
                    }
                } else {
                    throw new CustomException("Format of BIC response is not TRUE", HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId(),responseBodyFromBIC);
                }
            } else {
                throw new CustomException("Not found body response from BIC ", HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId(),null);
            }
            //check format reponse from BIC

        } catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                    throw new APIResponseException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.INVALID_INPUT_DATA, HttpStatus.BAD_REQUEST.toString());
                    throw new InvalidInputException(ex.getMessage(), createTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, customException.getHttpStatus().toString());
                    throw new CustomException(ex.getMessage(), customException.getHttpStatus(), createTravelInsuranceBICRequest.getRequestId(),customException.getResponseBIC());
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getTravelBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //time start
        long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        BaseResponse response = new BaseResponse();

        //check validate json request

        try {

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

            //get token from database
            String token = authorizationService.getTokenFromDatabase();

            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, queryTravelInsuranceBICRequest.getRequestId());
            }

            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog",null, queryTravelInsuranceBICRequest.getRequestId(),queryTravelInsuranceBICRequest.getRequestTime(),"getOrderTravelInsurance", "request",
                    transactionDetail, logTimestamp, messageTimestamp, timeDuration);
            logService.createTargetLog(tarObjectRequest);

            Map<String, Object> map = new HashMap<>();
            ResponseEntity<String> resultBIC = null;
            if (queryTravelInsuranceBICRequest.getDetail() != null) {
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L)) {
                    String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderId();
                    if (orderId != null) {
                        map.put("id", orderId);
                        resultBIC = apiUtils.getApiWithParam(hostConstants.BIC_HOST_GET_BY_ORDER_ID, null, map, token, queryTravelInsuranceBICRequest.getRequestId());
                    }
                }
                if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L)) {
                    orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
                    if (orderReference != null) {
                        map.put("id", orderReference);
                        resultBIC = apiUtils.getApiWithParam(hostConstants.BIC_HOST_GET_BY_ORDER_REFERANCE, null, map, token, queryTravelInsuranceBICRequest.getRequestId());
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
                        JSONObject responseBodyBIC= new JSONObject(responseCreate);

                        //set response to client
                        response = setResponseUtils.setResponseInquery(response, createTravelInsuranceBICResponse);
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(),queryTravelInsuranceBICRequest.getRequestTime(), "getOrderTravelInsurance", "response",
                                responseBodyBIC, logTimestamp, messageTimestamp, timeDurationResponse);
                        logService.createTargetLog(tarObject);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", queryTravelInsuranceBICRequest.getRequestId(),queryTravelInsuranceBICRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);

                    }
                } else {
                    throw new CustomException("Can not find the insurance order", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),jsonObjectResultBICLog);
                }
            }else {
                throw new CustomException("Not found body response from BIC ", HttpStatus.INTERNAL_SERVER_ERROR, queryTravelInsuranceBICRequest.getRequestId(),null);
            }

        } catch (Exception ex) {
            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                    throw new APIResponseException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.INVALID_INPUT_DATA, HttpStatus.BAD_REQUEST.toString());
                    throw new InvalidInputException(ex.getMessage(), queryTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, customException.getHttpStatus().toString());
                    throw new CustomException(ex.getMessage(), customException.getHttpStatus(), queryTravelInsuranceBICRequest.getRequestId(),customException.getResponseBIC());
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //start time
        long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();

        try {
            //check validate json request
            ValidateRequest.checkValidUpdate(updateTravelInsuranceBICRequest);

            //declare value response client

            //get log time
            String logtimeStamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logtimeStamp;

            //Update BIC
            CreateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            responseCreate = mapper.writeValueAsString(updateTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logRequest vs BIC
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(),updateTravelInsuranceBICRequest.getRequestTime(), "updateOrderTravelInsurance", "request",
                    transactionDetail, logtimeStamp, messageTimestamp, timeDuration);
            logService.createTargetLog(tarObjectRequest);

            String token = authorizationService.getTokenFromDatabase();
            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from BIC", HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId());
            }

            //getOrderId to request BIC
            String orderID = null;
            if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null) {
                orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
            }
            //post Data to BIC
            ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID, hostConstants.BIC_HOST_UPDATE, null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId());

            //set response data to client
            int status = responseSelvet.getStatus();
            String responseStatus = Integer.toString(status);

            if (jsonResultPutBIC != null && jsonResultPutBIC.getBody() != null ) {
                EJson jsonObjectReponseUpdate = new EJson(jsonResultPutBIC.getBody());
                JSONObject responseBodyFromBIC = new JSONObject(jsonResultPutBIC.getBody());

                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectReponseUpdate);

                //check format reponse from BIC
                if (isValidFormatResponse) {
                    if (jsonResultPutBIC.getStatusCode() == HttpStatus.OK
                            && jsonObjectReponseUpdate != null
                            && jsonObjectReponseUpdate.getBoolean("succeeded") == true) {

                        //set response client
                        response = setResponseUtils.setResponse(response, updateTravelInsuranceBICRequest,
                                createTravelInsuranceBICResponse, jsonResultPutBIC);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromCreateorUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultPutBIC.getStatusCode().toString());

                        //log properties
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponse vs BIC
                        TargetObject tarObject = new TargetObject("targetLog",null,  updateTravelInsuranceBICRequest.getRequestId(),updateTravelInsuranceBICRequest.getRequestTime(),"updateOrderTravelInsurance", "response",
                                responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationResponse);
                        logService.createTargetLog(tarObject);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(),updateTravelInsuranceBICRequest.getRequestTime(),null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                                response.getResultMessage(), logtimeStamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);
                    } else {
                        ReponseError responseError = new ReponseError();
                        EJson dataResponse = (jsonObjectReponseUpdate.getJSONObject("data"));

                        //set data reponse error
                        responseError = setResponseUtils.setResponseError(responseError,updateTravelInsuranceBICRequest,
                                jsonResultPutBIC,dataResponse );

                        //set properties to log
                        String responseBody = mapper.writeValueAsString(responseError);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //create BICTransaction
                        bicTransactionService.createBICTransactionFromCreateorUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseUpdate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultPutBIC.getStatusCode().toString());

                        //get timeDuration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                        //logResponseError vs BIC
                        TargetObject tarObject = new TargetObject("targetLog",null, updateTravelInsuranceBICRequest.getRequestId(),updateTravelInsuranceBICRequest.getRequestTime(),"updateOrderTravelInsurance", "response",
                                responseBodyFromBIC, logtimeStamp, messageTimestamp, timeDurationResponse);
                        logService.createTargetLog(tarObject);

                        //logResponseError vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(),updateTravelInsuranceBICRequest.getRequestTime(),null, "smartMarket", "client",
                                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                                responseError.getResultMessage(), logtimeStamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);


                        return new ResponseEntity<>(responseError, HttpStatus.OK);
                    }
                } else {
                    throw new CustomException("Format of BIC response is not TRUE", HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId(),responseBodyFromBIC);
                }
            } else {
                throw new CustomException("Not found body response from BIC ", HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId(),null);
            }

        } catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getMessage());
                    throw new APIResponseException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.INVALID_INPUT_DATA, HttpStatus.BAD_REQUEST.toString());
                    throw new InvalidInputException(ex.getMessage(), updateTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, customException.getHttpStatus().toString());
                    throw new CustomException(ex.getMessage(), customException.getHttpStatus(), updateTravelInsuranceBICRequest.getRequestId(),customException.getResponseBIC());
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
