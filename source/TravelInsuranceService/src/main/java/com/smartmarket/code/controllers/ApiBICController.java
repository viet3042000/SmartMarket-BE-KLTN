package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APIResponseException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.HandleResponseException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entity.TRVDetail;
import com.smartmarket.code.request.entity.UserLoginBIC;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class ApiBICController {

//    @Autowired
//    AuthorizationService authorizationService ;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    CachingServiceImpl cachingService;

    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
    @PostMapping(value = "/create-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest) throws JsonProcessingException {
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse = new CreateTravelInsuranceBICResponse();
        //post get token
        UserLoginBIC userLoginBIC = new UserLoginBIC();
        userLoginBIC.setUsername("bic-dsvn@bic.vn");
        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
        userLoginBIC.setDomainname("vetautructuyen.com.vn");

        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();

        String requestToken = mapper.writeValueAsString(userLoginBIC);
        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null, createTravelInsuranceBICRequest.getRequestId());
        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));


        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = MapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
        String requestCreate = null;
        requestCreate = mapper2.writeValueAsString(createTravelInsuranceToBIC);

        //post Data to BIC
        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_CREATE, null, requestCreate, token, createTravelInsuranceBICRequest.getRequestId());
        JSONObject jsonObjectReponseCreate = null;
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
            return new ResponseEntity<>(responseError, HttpStatus.OK);

        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/inquire-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getTravelBIC(@RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest) throws JsonProcessingException {

        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = null;
        List<String> data = new ArrayList<>();

        //post get token
        UserLoginBIC userLoginBIC = new UserLoginBIC();
        userLoginBIC.setUsername("bic-dsvn@bic.vn");
        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
        userLoginBIC.setDomainname("vetautructuyen.com.vn");

        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();

        //mapper object to request BIC
        String requestToken = mapper.writeValueAsString(userLoginBIC);
        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null, queryTravelInsuranceBICRequest.getRequestId());
        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

        if (queryTravelInsuranceBICRequest.getDetail() != null) {
            createTravelInsuranceBICRequest = MapperUtils.queryCreateObjectToBIC(Long.parseLong(queryTravelInsuranceBICRequest.getDetail().getOrderId()), token,queryTravelInsuranceBICRequest.getRequestId());
        }

        response.setDetail(createTravelInsuranceBICRequest);
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/change-bic-travel-insurance" ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<?> updateTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest) throws JsonProcessingException {
        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICResponse createTravelInsuranceBICResponse =  new CreateTravelInsuranceBICResponse();

        //post get token
        UserLoginBIC userLoginBIC = new UserLoginBIC();
        userLoginBIC.setUsername("bic-dsvn@bic.vn");
        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
        userLoginBIC.setDomainname("vetautructuyen.com.vn");

        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();

        String requestToken = mapper.writeValueAsString(userLoginBIC);
        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null,updateTravelInsuranceBICRequest.getRequestId());
        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

        //Update BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = MapperUtils.mapCreateObjectToBIC(updateTravelInsuranceBICRequest.getDetail()) ;
        String requestCreate = null;
        requestCreate = mapper2.writeValueAsString(createTravelInsuranceToBIC);

        String OrderID = createTravelInsuranceToBIC.getOrders().getOrderid().toString();
        System.out.print(OrderID);

        //post Data to BIC
        ResponseEntity<String> jsonResultPutBIC = apiUtils.putDataByApiBody(OrderID,HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_UPDATE, null, requestCreate, token,updateTravelInsuranceBICRequest.getRequestId());

        //response
        JSONObject jsonObjectReponseCreate =  null ;
        if(jsonResultPutBIC.getStatusCode() == HttpStatus.OK &&  jsonResultPutBIC.getBody() != null ){

//          set reponse data to client
            jsonObjectReponseCreate = new JSONObject(jsonResultPutBIC.getBody()) ;
//            Long orderIdCreated = jsonObjectReponseCreate.getLong("orderId") ;
            Long orderIdCreated = createTravelInsuranceToBIC.getOrders().getOrderid();
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
            response.setResultCode("000");
            response.setResultMessage("Successful");
            response.setResponseTime(dataResponse.getString("internalMessage"));
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
