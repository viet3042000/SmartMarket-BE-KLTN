package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.SoaObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entity.UserLoginBIC;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class ApiBICController {

    @Autowired
    AuthorizationService authorizationService ;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    LogServiceImpl logService;

    @Value("${config}")
    String loadtoken;

    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
    @PostMapping(value = "/create-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        //get current start time
        long startTime = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();

        //get time log
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String LOGTIMESTAMP = formatter.format(date);
        String MESSAGETIMESTAMP = LOGTIMESTAMP;

        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = MapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        responseCreate = mapper2.writeValueAsString(createTravelInsuranceToBIC);

//        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject(null, createTravelInsuranceBICRequest.getRequestId(),"BIC", "request",
                mapper2.writeValueAsString(createTravelInsuranceBICRequest), LOGTIMESTAMP, MESSAGETIMESTAMP, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        //post Data to BIC
        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_CREATE, null, responseCreate, token, createTravelInsuranceBICRequest.getRequestId());
        JSONObject jsonObjectReponseCreate = null;

        int status = responseSelvet.getStatus();
        String RESPONSESTATUS = Integer.toString(status);
        if (jsonResultCreateBIC.getBody() != null) {
            try {
                //get response data from BIC
                jsonObjectReponseCreate = new JSONObject(jsonResultCreateBIC.getBody());
                Long orderIdCreated = jsonObjectReponseCreate.getLong("orderId");
                boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded");

                createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
                createTravelInsuranceBICResponse.setSucceeded(succeeded);
                JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
                DataCreateBIC dataCreateBIC = new DataCreateBIC();
                dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
                dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
                createTravelInsuranceBICResponse.setData(dataCreateBIC);

                //set response data to client
                response.setDetail(createTravelInsuranceBICResponse);
                response.setResponseId(createTravelInsuranceBICRequest.getRequestId());
                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
                response.setResponseTime(dataResponse.getString("internalMessage"));

                String TRANSACTIONDETAIL = mapper.writeValueAsString(response);

                long elapsed = System.currentTimeMillis() - startTime;
                String TIMEDURATION = Long.toString(elapsed);

                //logResponse vs BIC
                TargetObject tarObject = new TargetObject(null, createTravelInsuranceBICRequest.getRequestId(),"BIC", "response",
                        TRANSACTIONDETAIL, LOGTIMESTAMP, MESSAGETIMESTAMP, TIMEDURATION);
                logService.createTargetLog(tarObject.getStringObject());

                //logResponse vs Client
                SoaObject soaObject = new SoaObject(createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "Client",
                        MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                        "response", TRANSACTIONDETAIL, RESPONSESTATUS, response.getResultCode(),
                        response.getResultMessage(), LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());

            }catch (Exception ex){
                throw new CustomException(ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR ,createTravelInsuranceBICRequest.getRequestId() ) ;
            }

        }else {
            ReponseError responseError = new ReponseError();
            responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            responseError.setResponseId(createTravelInsuranceBICRequest.getRequestId());
            responseError.setDetailErrorCode(HttpStatus.OK.toString());
            responseError.setDetailErrorMessage("Không lấy được responseBody khi tạo bảo hiểm!");

            String TRANSACTIONDETAIL = mapper.writeValueAsString(responseError);
            long elapsed = System.currentTimeMillis() - startTime;
            String TIMEDURATION = Long.toString(elapsed);

            //logResponseError vs BIC
            TargetObject tarObject = new TargetObject(null, createTravelInsuranceBICRequest.getRequestId(),"BIC", "response",
                    TRANSACTIONDETAIL, LOGTIMESTAMP, MESSAGETIMESTAMP, TIMEDURATION);
            logService.createTargetLog(tarObject.toString());

            //logResponseError vs Client
            SoaObject soaObject = new SoaObject(createTravelInsuranceBICRequest.getRequestId(), null, "BIC", "Client",
                    MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                    "response", TRANSACTIONDETAIL, RESPONSESTATUS, responseError.getResultCode(),
                    responseError.getResultMessage(), LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(responseError, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/inquire-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getTravelBIC(@RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        long startTime = System.currentTimeMillis();
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICRequest createTravelInsuranceBICResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();

        //get time log
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String LOGTIMESTAMP = formatter.format(date);
        String MESSAGETIMESTAMP = LOGTIMESTAMP;

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject(null, queryTravelInsuranceBICRequest.getRequestId(),"BIC", "request",
                mapper.writeValueAsString(queryTravelInsuranceBICRequest), LOGTIMESTAMP, MESSAGETIMESTAMP, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        if (queryTravelInsuranceBICRequest.getDetail() != null) {
            createTravelInsuranceBICResponse = MapperUtils.queryCreateObjectToBIC(Long.parseLong(queryTravelInsuranceBICRequest.getDetail().getOrderId()), token,queryTravelInsuranceBICRequest.getRequestId());
            String responseCreate = null;
            responseCreate = mapper2.writeValueAsString(createTravelInsuranceBICResponse);
        }

        int status = responseSelvet.getStatus();
        String RESPONSESTATUS = Integer.toString(status);

        response.setDetail(createTravelInsuranceBICResponse);
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        String TRANSACTIONDETAIL = mapper.writeValueAsString(response);

        long elapsed = System.currentTimeMillis() - startTime;
        String TIMEDURATION = Long.toString(elapsed);

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject(null, queryTravelInsuranceBICRequest.getRequestId(),"BIC", "response",
                TRANSACTIONDETAIL, LOGTIMESTAMP, MESSAGETIMESTAMP, TIMEDURATION);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        SoaObject soaObject = new SoaObject(queryTravelInsuranceBICRequest.getRequestId(), null, "BIC", "Client",
                MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                "response", TRANSACTIONDETAIL, RESPONSESTATUS, response.getResultCode(),
                response.getResultMessage(), LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/change-bic-travel-insurance" ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<?> updateTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        long startTime = System.currentTimeMillis();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse =  new CreateTravelInsuranceBICResponse();
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();
        BaseResponse response = new BaseResponse();

        //get log time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String LOGTIMESTAMP = formatter.format(date);
        String MESSAGETIMESTAMP = LOGTIMESTAMP;

        //Update BIC
        CreateTravelInsuranceToBIC updateTravelInsuranceToBIC = MapperUtils.mapCreateObjectToBIC(updateTravelInsuranceBICRequest.getDetail()) ;
        String responseCreate = null;
        responseCreate = mapper2.writeValueAsString(updateTravelInsuranceToBIC);

        //logRequest vs BIC
        TargetObject tarObjectRequest = new TargetObject(null, updateTravelInsuranceBICRequest.getRequestId(),"BIC", "request",
                mapper2.writeValueAsString(updateTravelInsuranceBICRequest), LOGTIMESTAMP, MESSAGETIMESTAMP, null);
        logService.createTargetLog(tarObjectRequest.getStringObject());

        //get token from database
        String token = authorizationService.getTokenFromDatabase();

        String orderID = null ;
        if (updateTravelInsuranceToBIC.getOrders().getOrderid() != null ){
            orderID = updateTravelInsuranceToBIC.getOrders().getOrderid().toString();
        }

        ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(orderID,HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_UPDATE, null, responseCreate, token,updateTravelInsuranceBICRequest.getRequestId());
        //post Data to BIC

        int status = responseSelvet.getStatus();
        String RESPONSESTATUS = Integer.toString(status);

        //response
        JSONObject jsonObjectReponseCreate =  null ;
        if(jsonResultPutBIC.getStatusCode() == HttpStatus.OK &&  jsonResultPutBIC.getBody() != null ){

            //set response data to client
            jsonObjectReponseCreate = new JSONObject(jsonResultPutBIC.getBody()) ;
            Long orderIdCreated = updateTravelInsuranceToBIC.getOrders().getOrderid();
            boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded") ;

            createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
            createTravelInsuranceBICResponse.setSucceeded(succeeded);
            JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
            DataCreateBIC dataCreateBIC = new DataCreateBIC() ;
            dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
            dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
            createTravelInsuranceBICResponse.setData(dataCreateBIC);
            response.setDetail(createTravelInsuranceBICResponse);
            response.setResponseId(updateTravelInsuranceBICRequest.getRequestId());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
            response.setResponseTime(dataResponse.getString("internalMessage"));

            //log properties
            String TRANSACTIONDETAIL = mapper.writeValueAsString(response);
            long elapsed = System.currentTimeMillis() - startTime;
            String TIMEDURATION = Long.toString(elapsed);

            //logResponse vs BIC
            TargetObject tarObject = new TargetObject(null, updateTravelInsuranceBICRequest.getRequestId(),"BIC", "response",
                    TRANSACTIONDETAIL, LOGTIMESTAMP, MESSAGETIMESTAMP, TIMEDURATION);
            logService.createTargetLog(tarObject.getStringObject());

            //logResponse vs Client
            SoaObject soaObject = new SoaObject(updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "Client",
                    MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                    "response", TRANSACTIONDETAIL, RESPONSESTATUS, response.getResultCode(),
                    response.getResultMessage(), LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());
        }else {
            ReponseError responseError = new ReponseError();
            responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            responseError.setResponseId(updateTravelInsuranceBICRequest.getRequestId());
            responseError.setDetailErrorCode(HttpStatus.OK.toString());
            responseError.setDetailErrorMessage("Không lấy được responseBody khi tạo bảo hiểm!");

            String TRANSACTIONDETAIL = mapper.writeValueAsString(responseError);
            long elapsed = System.currentTimeMillis() - startTime;
            String TIMEDURATION = Long.toString(elapsed);

            //logResponseError vs BIC
            TargetObject tarObject = new TargetObject(null, updateTravelInsuranceBICRequest.getRequestId(),"BIC", "response",
                    TRANSACTIONDETAIL, LOGTIMESTAMP, MESSAGETIMESTAMP, TIMEDURATION);
            logService.createTargetLog(tarObject.toString());

            //logResponseError vs Client
            SoaObject soaObject = new SoaObject(updateTravelInsuranceBICRequest.getRequestId(), null, "BIC", "Client",
                    MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                    "response", TRANSACTIONDETAIL, RESPONSESTATUS, responseError.getResultCode(),
                    responseError.getResultMessage(), LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(responseError, HttpStatus.OK);
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
