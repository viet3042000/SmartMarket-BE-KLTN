package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.QueryOpenDataRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.OpenDataService;
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
public class QueryOpenDataServiceImpl implements OpenDataService {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    SetResponseUtils setResponseUtils;

    @Autowired
    ConfigurableEnvironment environment;


    @Override
    public ResponseEntity<?> queryOpenData(BaseDetail<QueryOpenDataRequest> queryOpenDataRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //time start
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        //SET TIMEOUT
        //set Time out get token api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryQueryData = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryQueryData.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.queryOpenData")));
        //Read timeout
        clientHttpRequestFactoryQueryData.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.queryOpenData")));


        BaseResponse response = new BaseResponse();

        //check validate json request

        try {

            //delcare used value
            ObjectMapper mapper = new ObjectMapper();

            // declare value for log
            //get time log
            String logTimestamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logTimestamp;

            //properties log
            String q = queryOpenDataRequest.getDetail().getQ();
            Long start = queryOpenDataRequest.getDetail().getStart();
            Long rows = queryOpenDataRequest.getDetail().getRows();
            String responseStatus = Integer.toString(responseSelvet.getStatus());
            JSONObject transactionDetail = new JSONObject();



            //get token from database
//            String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI3MmJmNzBlMS1jZTEyLTQyODktYTgyOS1kZjI5ZDgyNjViM2MiLCJyb2xlcyI6WyJhZG1pbiIsInNlYXJjaGVyIl0sImlzcyI6InRvYW5saDQiLCJkYXRhR3JvdXBzIjpbInF1eWV0ZGluaF90b3RyaW5oOkZJUyBGUFMiLCJob3Bkb25nX2hvc29fbmhhbnN1OkZJUyBCT018RklTIEZUUyIsImNvbmd2YW5faG9zb2dpYW9kaWNoOkZJUyBFUlAiXSwiZXhwIjoxNjI1NTUyNjgxLCJkYXRhU291cmNlcyI6WyJob3Bkb25nX2hvc29fbmhhbnN1IiwicXV5ZXRkaW5oX3RvdHJpbmgiLCJjb25ndmFuX2hvc29naWFvZGljaCJdLCJpYXQiOjE2MjU1NTIzODF9.-pQV9DY6U7FI-u1pMfzdtr2C8WtRh0nQx-a0Dj6i9ts";
//            String token = authorizationService.getTokenFromDatabase();
            String token = authorizationService.getToken();

            if (StringUtils.isEmpty(token)) {
                throw new CustomException("Not found token response from open data", HttpStatus.INTERNAL_SERVER_ERROR, queryOpenDataRequest.getRequestId(),ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            }

            transactionDetail.put("q", q);
            transactionDetail.put("start", start);
            transactionDetail.put("rows", rows);
            transactionDetail.put("token", token);

            //logRequest vs OpenData
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryOpenDataRequest.getRequestId(), queryOpenDataRequest.getRequestTime(), "getOrderTravelInsurance", "request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            Map<String, Object> map = new HashMap<>();
            ResponseEntity<String> resultOpenData = null;
            long startTime = 0;
            String timeDurationOpenData = null;


            if (queryOpenDataRequest.getDetail() != null) {
                q = queryOpenDataRequest.getDetail().getQ();
                start = queryOpenDataRequest.getDetail().getStart();
                rows = queryOpenDataRequest.getDetail().getRows();

                map.put("q", q);
                map.put("start", start);
                map.put("rows", rows);
                map.put("token", token);
                startTime = System.currentTimeMillis();
                resultOpenData = apiUtils.getApiWithParam(environment.getRequiredProperty("api.queryOpenData"), map,null, token, queryOpenDataRequest.getRequestId(),clientHttpRequestFactoryQueryData);
                timeDurationOpenData = DateTimeUtils.getElapsedTimeStr(startTime);

            }

            if (resultOpenData != null && resultOpenData.getBody() != null) {
                EJson jsonObjectResultOpenData = new EJson(resultOpenData.getBody());
                JSONObject jsonObjectResultOpenDataLog = new JSONObject(resultOpenData.getBody());

                //check valid response
//                boolean isValidFormatResponse = CheckFormatUtils.checkFormat(jsonObjectResultOpenData);

//                if (!isValidFormatResponse) {
                    if (resultOpenData.getStatusCode() == HttpStatus.OK && resultOpenData.getBody() != null) {

                        //convert jsonstring to object
                        Object responseOpenData = mapper.readValue(resultOpenData.getBody(), Object.class);

                        //set response to client
                        response = setResponseUtils.setResponseInquery(queryOpenDataRequest ,response,responseOpenData);
                        String responseBody = mapper.writeValueAsString(response);
                        JSONObject transactionDetailResponse = new JSONObject(responseBody);

                        //logResponse vs OpenData
                        TargetObject tarObject = new TargetObject("targetLog", null, queryOpenDataRequest.getRequestId(), queryOpenDataRequest.getRequestTime(), "getOrderTravelInsurance", "response",
                                jsonObjectResultOpenDataLog, logTimestamp, messageTimestamp, timeDurationOpenData);
                        logService.createTargetLog(tarObject);

                        //calculate time duration
                        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

                        //logResponse vs Client
                        ServiceObject soaObject = new ServiceObject("serviceLog", queryOpenDataRequest.getRequestId(), queryOpenDataRequest.getRequestTime(), null, "smartMarket", "client",
                                messageTimestamp, "opendataservice", "1", timeDurationResponse,
                                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
                        logService.createSOALog2(soaObject);

                    }
//                } else {
//                    //logResponse vs OpenData
//                    EJson dataResponse = (jsonObjectResultOpenData.getJSONObject("data"));
//                    String requestURL = request.getRequestURL().toString();
//                    String targetService = requestURL.substring(requestURL.indexOf("v1/") + 3, requestURL.length());
//
//                    TargetObject tarObject = new TargetObject("targetLog", null, queryOpenDataRequest.getRequestId(), queryOpenDataRequest.getRequestTime(), targetService, "response", jsonObjectResultOpenDataLog,
//                            logTimestamp, messageTimestamp, timeDurationOpenData);
//                    logService.createTargetLog(tarObject);
//                    throw new CustomException("Not found order", resultOpenData.getStatusCode(), queryOpenDataRequest.getRequestId(), jsonObjectResultOpenDataLog, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
//                }
            } else {
                //logResponse vs OpenData
                String requestURL = request.getRequestURL().toString();
                String targetService = requestURL.substring(requestURL.indexOf("v1/") + 3, requestURL.length());
                TargetObject tarObject = new TargetObject("targetLog", null, queryOpenDataRequest.getRequestId(), queryOpenDataRequest.getRequestTime(), targetService, "response", null,
                        logTimestamp, messageTimestamp, timeDurationOpenData);
                logService.createTargetLog(tarObject);

                throw new CustomException("", resultOpenData.getStatusCode(), queryOpenDataRequest.getRequestId(), null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            }

        } catch (Exception ex) {
            try {
                //catch truong hop chua goi dc sang OpenData
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        throw new APIAccessException(queryOpenDataRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        throw new APIAccessException(queryOpenDataRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang OpenData nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    throw new APIResponseException(queryOpenDataRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    throw new InvalidInputException(ex.getMessage(), queryOpenDataRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException) {
                    CustomException customException = (CustomException) ex;
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryOpenDataRequest.getRequestId(), customException.getResponseBackend(), customException.getHttpStatusCode(), customException.getErrorMessage());
                } else {
                    throw ex;
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
