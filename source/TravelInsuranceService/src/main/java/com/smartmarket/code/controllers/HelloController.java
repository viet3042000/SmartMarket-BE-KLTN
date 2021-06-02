package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.request.entity.User;
import com.smartmarket.code.request.entity.UserLoginBIC;
import com.smartmarket.code.response.Response;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.util.APIUtils;
import com.smartmarket.code.util.JwtUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HelloController {

    public static final Logger logger = LoggerFactory.getLogger(HelloController.class);


//    @Autowired
//    AuthorizationService authorizationService ;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    CachingServiceImpl cachingService;

    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
//    @PostMapping(value = "/hello", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<?> hello(@RequestBody User userid) throws JsonProcessingException {

//        Response response = new Response();
//
//        UserLoginBIC userLoginBIC = new UserLoginBIC();
//        userLoginBIC.setUsername("bic-dsvn@bic.vn");
//        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
//        userLoginBIC.setDomainname("vetautructuyen.com.vn");
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        String bodyrequest = mapper.writeValueAsString(userLoginBIC);
//
//
//        //post
//        String path = "https://app.bic.vn/EbizApiTest/api/v1/token/login";
//        ResponseEntity<String> jsonResult = apiUtils.postDataByApiBody("https://app.bic.vn/EbizApiTest/api/v1/token/login", null, bodyrequest, null);
//
//        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResult.getBody()));
//
//        //get api
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", "102295");
//        ResponseEntity<String> jsonResult2 = apiUtils.getApiWithParam("https://app.bic.vn/EbizApiTest/api/v1/TRV/Get/", map, null, token);
//        System.out.println(userid);
//        List<String> data = new ArrayList<>();
//        data.add("hop");
//        response.setCode(1);
//        response.setData(data);
//        logger.info("dday la log test");
//        return new ResponseEntity<>(response, HttpStatus.OK);

//    }


    @GetMapping(value = "/hello/getcache")
    public ResponseEntity<?> getStaticCache() {

        Response response = new Response();
//        boolean checkUserAccess = authorizationService.AuthorUserAccess(userid);

//        if(checkUserAccess ==  false) {
//            throw new CustomException("This user information is not accessible in this function",HttpStatus.FORBIDDEN) ;
//        }
        Object set = cachingService.getFromCache("hoptest", 1L);
        response.setCode(1);
        response.setData(set);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
