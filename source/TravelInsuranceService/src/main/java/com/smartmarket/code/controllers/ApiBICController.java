package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entity.TRVDetail;
import com.smartmarket.code.request.entity.UserLoginBIC;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.Response;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.util.APIUtils;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.MapperUtils;
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
    APIUtils apiUtils ;

    @Autowired
    CachingServiceImpl cachingService ;

    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
    @PostMapping(value = "/create-bic-travel-insurance" ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<?> createTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest) throws JsonProcessingException {
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
        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null);
        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));


        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = MapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail()) ;
        String requestCreate = null;
        requestCreate = mapper2.writeValueAsString(createTravelInsuranceToBIC);
        ResponseEntity<String> jsonResultCreateBIC = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_CREATE, null, requestCreate, token);
        JSONObject jsonObjectReponseCreate =  null ;
        if(jsonResultCreateBIC.getStatusCode() == HttpStatus.OK &&  jsonResultCreateBIC != null ){
            jsonObjectReponseCreate = new JSONObject(jsonResultCreateBIC.getBody()) ;
        }
        Long orderIdCreated = jsonObjectReponseCreate.getLong("orderId") ;
        boolean succeeded = jsonObjectReponseCreate.getBoolean("succeeded") ;

        createTravelInsuranceBICResponse.setOrderId(String.valueOf(orderIdCreated));
        createTravelInsuranceBICResponse.setSucceeded(succeeded);
        JSONObject dataResponse = (jsonObjectReponseCreate.getJSONObject("data"));
//        Object data1=  new Object();
//        createTravelInsuranceBICResponse.setData(data1);

        DataCreateBIC dataCreateBIC = new DataCreateBIC() ;
        dataCreateBIC.setMessage(dataResponse.getString("userMessage"));
        dataCreateBIC.setCreatedate(dataResponse.getString("internalMessage"));
        createTravelInsuranceBICResponse.setData(dataCreateBIC);
        response.setDetail(createTravelInsuranceBICResponse);

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PostMapping(value = "/inquire-bic-travel-insurance" ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<?> getTravelBIC(@RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest) throws JsonProcessingException {

        BaseResponse response = new BaseResponse();
        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = null ;
        List<String> data = new ArrayList<>() ;


        //post get token
        UserLoginBIC userLoginBIC = new UserLoginBIC();
        userLoginBIC.setUsername("bic-dsvn@bic.vn");
        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
        userLoginBIC.setDomainname("vetautructuyen.com.vn");

        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper mapper2 = new ObjectMapper();

        String requestToken = mapper.writeValueAsString(userLoginBIC);
        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null);
        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

        if (queryTravelInsuranceBICRequest.getDetail() != null ){
            createTravelInsuranceBICRequest = MapperUtils.queryCreateObjectToBIC(Long.parseLong(queryTravelInsuranceBICRequest.getDetail().getOrderId()),token) ;
        }


        response.setDetail(createTravelInsuranceBICRequest);
        response.setResultCode("000");
        response.setResultMessage("Successful");
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PostMapping(value = "/change-bic-travel-insurance" ,produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<?> updateTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest) {

        BaseResponse response = new BaseResponse();

        List<String> data = new ArrayList<>() ;
//        response.setResultCode(1L);
//        response.setData(data);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }


}
