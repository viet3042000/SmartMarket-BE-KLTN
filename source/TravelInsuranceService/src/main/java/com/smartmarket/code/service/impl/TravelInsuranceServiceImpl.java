//package com.smartmarket.code.service.impl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.smartmarket.code.constants.HostConstants;
//import com.smartmarket.code.constants.ResponseCode;
//import com.smartmarket.code.exception.CustomException;
//import com.smartmarket.code.model.entitylog.TargetObject;
//import com.smartmarket.code.request.BaseDetail;
//import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
//import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
//import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
//import com.smartmarket.code.response.BaseResponse;
//import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
//import com.smartmarket.code.response.DataCreateBIC;
//import com.smartmarket.code.response.ReponseError;
//import com.smartmarket.code.service.AuthorizationService;
//import com.smartmarket.code.util.APIUtils;
//import com.smartmarket.code.util.DateTimeUtils;
//import com.smartmarket.code.util.MapperUtils;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Service
//public class TravelInsuranceServiceImpl {
//
//    @Autowired
//    AuthorizationService authorizationService ;
//
//    @Autowired
//    APIUtils apiUtils;
//
//    @Autowired
//    LogServiceImpl logService;
//
//    @Autowired
//    MapperUtils mapperUtils ;
//
//    @Autowired
//    HostConstants hostConstants ;
//
//    public ResponseEntity<?> createTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
//
////        System.out.println(configToken);
//
//        //get current start time
//        long startTime = System.currentTimeMillis();
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectMapper mapper2 = new ObjectMapper();
//        BaseResponse response = new BaseResponse();
//        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
//
//        //get time log
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        String logTimestamp = formatter.format(date);
//        String messageTimestamp = logTimestamp;
//
//        //Create BIC
//        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
//        String responseCreate = null;
//        responseCreate = mapper2.writeValueAsString(createTravelInsuranceToBIC);
//
////        //logRequest vs BIC
//        TargetObject tarObjectRequest = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(),"BIC", "request", "request",
//                mapper2.writeValueAsString(createTravelInsuranceBICRequest), logTimestamp, messageTimestamp, null);
//        logService.createTargetLog(tarObjectRequest.getStringObject());
//
//        //get token from database
//        String token = authorizationService.getTokenFromDatabase();
//
//        //post Data to BIC
//        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(hostConstants.BIC_HOST_CREATE, null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId());
//        JSONObject jsonObjectReponseCreate = null;
//
//        int status = responseSelvet.getStatus();
//        String responseStatus = Integer.toString(status);
//        if (jsonResultCreateBIC.getBody() != null) {
//            try {
//                //get response data from BIC
//                jsonObjectReponseCreate = new JSONObject(jsonResultCreateBIC.getBody());
//                Long orderIdCreated = jsonObjectReponseCreate.getLong("orderId");
//                boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded");
//
//                createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
//                createTravelInsuranceBICResponse.setSucceeded(succeeded);
//                JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
//                DataCreateBIC dataCreateBIC = new DataCreateBIC();
//                dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
//                dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
//                createTravelInsuranceBICResponse.setData(dataCreateBIC);
//
//                //set response data to client
//                response.setDetail(createTravelInsuranceBICResponse);
//                response.setResponseId(createTravelInsuranceBICRequest.getRequestId());
//                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//                response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
//                response.setResponseTime(dataResponse.getString("internalMessage"));
//
//                String transactionDetail = mapper.writeValueAsString(response);
//
//                long elapsed = System.currentTimeMillis() - startTime;
//                String timeDuration = Long.toString(elapsed);
//
//                //logResponse vs BIC
//                TargetObject tarObject = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(),"BIC", "response","response",
//                        transactionDetail, logTimestamp, messageTimestamp, timeDuration);
//                logService.createTargetLog(tarObject.getStringObject());
//
//                //logResponse vs Client
//                SoaObject soaObject = new SoaObject("serviceLog",createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
//                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
//                        "response", transactionDetail, responseStatus, response.getResultCode(),
//                        response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
//                logService.createSOALog2(soaObject.getStringObject());
//
//            }catch (Exception ex){
//                throw new CustomException(ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR ,createTravelInsuranceBICRequest.getRequestId() ) ;
//            }
//
//        }else {
//            ReponseError responseError = new ReponseError();
//            responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
//            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
//            responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
//            responseError.setResponseId(createTravelInsuranceBICRequest.getRequestId());
//            responseError.setDetailErrorCode(HttpStatus.OK.toString());
//            responseError.setDetailErrorMessage("Không lấy được responseBody khi tạo bảo hiểm!");
//
//            String transactionDetail = mapper.writeValueAsString(responseError);
//            long elapsed = System.currentTimeMillis() - startTime;
//            String timeDuration = Long.toString(elapsed);
//
//            //logResponseError vs BIC
//            TargetObject tarObject = new TargetObject("targetLog", createTravelInsuranceBICRequest.getRequestId(),"BIC", "response","response",
//                    transactionDetail, logTimestamp, messageTimestamp, timeDuration);
//            logService.createTargetLog(tarObject.toString());
//
//            //logResponseError vs Client
//            SoaObject soaObject = new SoaObject("serviceLog",createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
//                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
//                    "response", transactionDetail, responseStatus, responseError.getResultCode(),
//                    responseError.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
//            logService.createSOALog2(soaObject.getStringObject());
//
//            return new ResponseEntity<>(responseError, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    public ResponseEntity<?> getTravelBIC(@RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
//        long startTime = System.currentTimeMillis();
//        BaseResponse response = new BaseResponse();
//        CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectMapper mapper2 = new ObjectMapper();
//
//        //get time log
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        String logTimestamp = formatter.format(date);
//        String messageTimestamp = logTimestamp;
//
//        //logRequest vs BIC
//        TargetObject tarObjectRequest = new TargetObject("targetLog", queryTravelInsuranceBICRequest.getRequestId(),"BIC", "request","request",
//                mapper.writeValueAsString(queryTravelInsuranceBICRequest), logTimestamp, messageTimestamp, null);
//        logService.createTargetLog(tarObjectRequest.getStringObject());
//
//        //get token from database
//        String token = authorizationService.getTokenFromDatabase();
//
//        if (queryTravelInsuranceBICRequest.getDetail() != null) {
//            createTravelInsuranceBICResponse = mapperUtils.queryCreateObjectToBIC(queryTravelInsuranceBICRequest, token,queryTravelInsuranceBICRequest.getRequestId());
//            String responseCreate = null;
//            responseCreate = mapper2.writeValueAsString(createTravelInsuranceBICResponse);
//        }
//
//        int status = responseSelvet.getStatus();
//        String responseStatus = Integer.toString(status);
//
//        response.setDetail(createTravelInsuranceBICResponse);
//        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
//
//        String transactionDetail = mapper.writeValueAsString(response);
//
//        long elapsed = System.currentTimeMillis() - startTime;
//        String timeDuration = Long.toString(elapsed);
//
//        //logResponse vs BIC
//        TargetObject tarObject = new TargetObject("targetLog", queryTravelInsuranceBICRequest.getRequestId(),"BIC", "response","response",
//                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
//        logService.createTargetLog(tarObject.getStringObject());
//
//        //logResponse vs Client
//        SoaObject soaObject = new SoaObject("serviceLog",queryTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
//                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
//                "response", transactionDetail, responseStatus, response.getResultCode(),
//                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
//        logService.createSOALog2(soaObject.getStringObject());
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//
//    public ResponseEntity<?> updateTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
//        long startTime = System.currentTimeMillis();
//        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse =  new CreateTravelInsuranceBICResponse();
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectMapper mapper2 = new ObjectMapper();
//        BaseResponse response = new BaseResponse();
//
//        //get log time
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        String logtimeStamp = formatter.format(date);
//        String messageTimestamp = logtimeStamp;
//
//        //Update BIC
//        CreateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail()) ;
//        String responseCreate = null;
//        responseCreate = mapper2.writeValueAsString(updateTravelInsuranceToBIC);
//
//        //logRequest vs BIC
//        TargetObject tarObjectRequest = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(),"BIC", "request","request",
//                mapper2.writeValueAsString(updateTravelInsuranceBICRequest), logtimeStamp, messageTimestamp, null);
//        logService.createTargetLog(tarObjectRequest.getStringObject());
//
//        //get token from database
//        String token = authorizationService.getTokenFromDatabase();
//
//        String orderID = null ;
//        if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null ){
//            orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
//        }
//
//        ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,hostConstants.BIC_HOST_UPDATE, null, responseCreate, token,updateTravelInsuranceBICRequest.getRequestId());
//        //post Data to BIC
//
//        int status = responseSelvet.getStatus();
//        String responseStatus = Integer.toString(status);
//
//        //response
//        JSONObject jsonObjectReponseCreate =  null ;
//        if(jsonResultPutBIC.getStatusCode() == HttpStatus.OK &&  jsonResultPutBIC.getBody() != null ){
//
//            //set response data to client
//            jsonObjectReponseCreate = new JSONObject(jsonResultPutBIC.getBody()) ;
//            Long orderIdCreated = updateTravelInsuranceToBIC.getOrders().getOrderid();
//            boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded") ;
//
//            createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
//            createTravelInsuranceBICResponse.setSucceeded(succeeded);
//            JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
//            DataCreateBIC dataCreateBIC = new DataCreateBIC() ;
//            dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
//            dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
//            createTravelInsuranceBICResponse.setData(dataCreateBIC);
//            response.setDetail(createTravelInsuranceBICResponse);
//            response.setResponseId(updateTravelInsuranceBICRequest.getRequestId());
//            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
//            response.setResponseTime(dataResponse.getString("internalMessage"));
//
//            //log properties
//            String transactionDetail = mapper.writeValueAsString(response);
//            long elapsed = System.currentTimeMillis() - startTime;
//            String timeDuration = Long.toString(elapsed);
//
//            //logResponse vs BIC
//            TargetObject tarObject = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(),"BIC", "response","response",
//                    transactionDetail, logtimeStamp, messageTimestamp, timeDuration);
//            logService.createTargetLog(tarObject.getStringObject());
//
//            //logResponse vs Client
//            SoaObject soaObject = new SoaObject("serviceLog",updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
//                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
//                    "response", transactionDetail, responseStatus, response.getResultCode(),
//                    response.getResultMessage(), logtimeStamp, request.getRemoteHost(),logService.getIp());
//            logService.createSOALog2(soaObject.getStringObject());
//        }else {
//            ReponseError responseError = new ReponseError();
//            responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
//            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
//            responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
//            responseError.setResponseId(updateTravelInsuranceBICRequest.getRequestId());
//            responseError.setDetailErrorCode(HttpStatus.OK.toString());
//            responseError.setDetailErrorMessage("Không lấy được responseBody khi tạo bảo hiểm!");
//
//            String transactionDetail = mapper.writeValueAsString(responseError);
//            long elapsed = System.currentTimeMillis() - startTime;
//            String timeDuration = Long.toString(elapsed);
//
//            //logResponseError vs BIC
//            TargetObject tarObject = new TargetObject("targetLog", updateTravelInsuranceBICRequest.getRequestId(),"BIC", "response","response",
//                    transactionDetail, logtimeStamp, messageTimestamp, timeDuration);
//            logService.createTargetLog(tarObject.toString());
//
//            //logResponseError vs Client
//            SoaObject soaObject = new SoaObject("serviceLog",updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "client",
//                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
//                    "response", transactionDetail, responseStatus, responseError.getResultCode(),
//                    responseError.getResultMessage(), logtimeStamp, request.getRemoteHost(),logService.getIp());
//            logService.createSOALog2(soaObject.getStringObject());
//
//            return new ResponseEntity<>(responseError, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(response,HttpStatus.OK);
//    }
//}