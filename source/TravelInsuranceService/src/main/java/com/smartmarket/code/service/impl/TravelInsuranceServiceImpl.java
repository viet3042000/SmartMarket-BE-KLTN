package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APITimeOutRequestException;
import com.smartmarket.code.exception.CustomException;
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
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


    @Override
    public ResponseEntity<?> createTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {

        //check validate json request
        ValidateRequest.checkValidCreateOrUpdate(createTravelInsuranceBICRequest);

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        //get current start time
        long startTime = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();

        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        responseCreate = mapper.writeValueAsString(createTravelInsuranceToBIC);

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(), "BIC", "request", "request",
                mapper.writeValueAsString(createTravelInsuranceBICRequest), logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        //post Data to BIC
        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(hostConstants.BIC_HOST_CREATE, null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId());
        JSONObject jsonObjectReponseCreate = null;

        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        jsonObjectReponseCreate = new JSONObject(jsonResultCreateBIC.getBody());

        try {
            if (jsonResultCreateBIC.getStatusCode() == HttpStatus.OK
                    && jsonObjectReponseCreate != null
                    && jsonObjectReponseCreate.getBoolean("succeeded") == true) {

                //set response data to client
                response = setResponseUtils.setResponse(response,createTravelInsuranceBICRequest,
                        createTravelInsuranceBICResponse,jsonResultCreateBIC);

                String transactionDetail = mapper.writeValueAsString(response);
                //get duration time
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //create BICTransaction
                bicTransactionService.createBICTransactionFromCreateorUpdateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultCreateBIC.getStatusCode().toString());

                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(), "BIC", "response", "response",
                        transactionDetail, logTimestamp, messageTimestamp, timeDuration);
                logService.createTargetLog(tarObject.getStringObject());

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                        "response", transactionDetail, responseStatus, response.getResultCode(),
                        response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());

            } else {
                JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));

                //set Response error
                ReponseError responseError = new ReponseError();
                responseError = setResponseUtils.setResponse(responseError,createTravelInsuranceBICRequest,
                        jsonResultCreateBIC,dataResponse );

                String transactionDetail = mapper.writeValueAsString(responseError);
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //create BICTransaction
                bicTransactionService.createBICTransactionFromCreateorUpdateTravel(createTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultCreateBIC.getStatusCode().toString());

                //logResponseError vs BIC
                TargetObject tarObject = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(), "BIC", "response", "response",
                        transactionDetail, logTimestamp, messageTimestamp, timeDuration);
                logService.createTargetLog(tarObject.toString());

                //logResponseError vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog", createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                        "response", transactionDetail, responseStatus, responseError.getResultCode(),
                        responseError.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());

                return new ResponseEntity<>(responseError, HttpStatus.OK);
            }
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, createTravelInsuranceBICRequest.getRequestId());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getTravelBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {
        //time start
        long startTime = System.currentTimeMillis();

        //delcare used value
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;
        ObjectMapper mapper = new ObjectMapper();

        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        //properties log
        String orderID = queryTravelInsuranceBICRequest.getDetail().getOrderId();
        String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
        String requestParameter = "orderId: " + orderID + ", orderRef: " + orderReference;
        String responseStatus = Integer.toString(responseSelvet.getStatus());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", queryTravelInsuranceBICRequest.getRequestId(), "BIC", "request", "request",
                requestParameter, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //
        if (queryTravelInsuranceBICRequest.getDetail() != null) {
            createTravelInsuranceBICResponse = mapperUtils.queryCreateObjectToBIC(queryTravelInsuranceBICRequest, token, queryTravelInsuranceBICRequest.getRequestId());
            String responseCreate = null;
            responseCreate = mapper.writeValueAsString(createTravelInsuranceBICResponse);
        }

        //set response to client
        response = setResponseUtils.setResponse(response, createTravelInsuranceBICResponse);

        String transactionDetail = mapper.writeValueAsString(response);

        //calculate time duration
        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", queryTravelInsuranceBICRequest.getRequestId(), "BIC", "response", "response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", queryTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", transactionDetail, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateTravelBIC( BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {
        //start time
        long startTime = System.currentTimeMillis();

        //check validate json request
        ValidateRequest.checkValidCreateOrUpdate(updateTravelInsuranceBICRequest);

        //declare value response client
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
        ObjectMapper mapper = new ObjectMapper();
        BaseResponse response = new BaseResponse();

        //get log time
        String logtimeStamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logtimeStamp;

        //Update BIC
        CreateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        responseCreate = mapper.writeValueAsString(updateTravelInsuranceToBIC);

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(), "BIC", "request", "request",
                mapper.writeValueAsString(updateTravelInsuranceBICRequest), logtimeStamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        //getOrderId to request BIC
        String orderID = null;
        if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null) {
            orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
        }
        //post Data to BIC
        ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID, hostConstants.BIC_HOST_UPDATE, null, responseCreate, token, updateTravelInsuranceBICRequest.getRequestId());

        //set response data to client
        JSONObject jsonObjectReponseCreate = new JSONObject(jsonResultPutBIC.getBody());
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);

        try {
            if (jsonResultPutBIC.getStatusCode() == HttpStatus.OK
                    && jsonObjectReponseCreate != null
                    && jsonObjectReponseCreate.getBoolean("succeeded") == true) {

                //set response client
                response = setResponseUtils.setResponse(response,updateTravelInsuranceBICRequest,
                        createTravelInsuranceBICResponse,jsonResultPutBIC);

                //create BICTransaction
                bicTransactionService.createBICTransactionFromCreateorUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.TRANSACTION_SUCCESSFUL, jsonResultPutBIC.getStatusCode().toString());

                //log properties
                String transactionDetail = mapper.writeValueAsString(response);
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //logResponse vs BIC
                TargetObject tarObject = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(), "BIC", "response", "response",
                        transactionDetail, logtimeStamp, messageTimestamp, timeDuration);
                logService.createTargetLog(tarObject.getStringObject());

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                        "response", transactionDetail, responseStatus, response.getResultCode(),
                        response.getResultMessage(), logtimeStamp, request.getRemoteHost(), logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());
            } else {
                ReponseError responseError = new ReponseError();
                JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));

                //sey data reponse error
                responseError = setResponseUtils.setResponse(responseError,updateTravelInsuranceBICRequest,
                        jsonResultPutBIC,dataResponse );

                //set properties to log
                String transactionDetail = mapper.writeValueAsString(responseError);
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //create BICTransaction
                bicTransactionService.createBICTransactionFromCreateorUpdateTravel(updateTravelInsuranceBICRequest, jsonObjectReponseCreate, ResponseCode.CODE.ERROR_IN_BACKEND, jsonResultPutBIC.getStatusCode().toString());

                //logResponseError vs BIC
                TargetObject tarObject = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(), "BIC", "response", "response",
                        transactionDetail, logtimeStamp, messageTimestamp, timeDuration);
                logService.createTargetLog(tarObject.toString());

                //logResponseError vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog", updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                        "response", transactionDetail, responseStatus, responseError.getResultCode(),
                        responseError.getResultMessage(), logtimeStamp, request.getRemoteHost(), logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());

                return new ResponseEntity<>(responseError, HttpStatus.OK);
            }

        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, updateTravelInsuranceBICRequest.getRequestId());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
