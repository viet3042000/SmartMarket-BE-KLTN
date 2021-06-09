//package com.smartmarket.code.controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.smartmarket.code.constants.ResponseCode;
//import com.smartmarket.code.request.entity.User;
//import com.smartmarket.code.request.entity.UserLoginBIC;
//import com.smartmarket.code.response.Response;
//import com.smartmarket.code.service.impl.CachingServiceImpl;
//import com.smartmarket.code.util.APIUtils;
//import com.smartmarket.code.util.JwtUtils;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class HelloController {
//
//    public static final Logger logger = LoggerFactory.getLogger(HelloController.class);
//
//    @Autowired
//    ConfigurableEnvironment environment;
//
//    @Value("${config}")
//    private String config;
////    @Autowired
////    AuthorizationService authorizationService ;
//
//    @Autowired
//    APIUtils apiUtils;
//
//    @Autowired
//    CachingServiceImpl cachingService;
//
//    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
////    @PostMapping(value = "/hello", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
////    public ResponseEntity<?> hello(@RequestBody User userid) throws JsonProcessingException {
//
////        Response response = new Response();
////
////        UserLoginBIC userLoginBIC = new UserLoginBIC();
////        userLoginBIC.setUsername("bic-dsvn@bic.vn");
////        userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
////        userLoginBIC.setDomainname("vetautructuyen.com.vn");
////
////        ObjectMapper mapper = new ObjectMapper();
////
////        String bodyrequest = mapper.writeValueAsString(userLoginBIC);
////
////
////        //post
////        String path = "https://app.bic.vn/EbizApiTest/api/v1/token/login";
////        ResponseEntity<String> jsonResult = apiUtils.postDataByApiBody("https://app.bic.vn/EbizApiTest/api/v1/token/login", null, bodyrequest, null);
////
////        String token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResult.getBody()));
////
////        //get api
////        Map<String, Object> map = new HashMap<>();
////        map.put("id", "102295");
////        ResponseEntity<String> jsonResult2 = apiUtils.getApiWithParam("https://app.bic.vn/EbizApiTest/api/v1/TRV/Get/", map, null, token);
////        System.out.println(userid);
////        List<String> data = new ArrayList<>();
////        data.add("hop");
////        response.setCode(1);
////        response.setData(data);
////        logger.info("dday la log test");
////        return new ResponseEntity<>(response, HttpStatus.OK);
//
////    }
//
//
//    @GetMapping(value = "/hello/getcache")
//    public ResponseEntity<?> getStaticCache() {
//
//        Response response = new Response();
//        System.out.println(config);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//
//    }
//
//    @GetMapping(value = "/hello/")
//    public ResponseEntity<?> refresh() throws SQLException {
//
//        final String PROPERTY_SOURCE_NAME = "databaseProperties";
//        Map<String, Object> propertySource = new HashMap<>();
//        // Build manually datasource to ServiceConfig
//        try {
//        DataSource ds = DataSourceBuilder
//                .create()
//                .username(environment.getProperty("spring.datasource.username"))
//                .password(environment.getProperty("spring.datasource.password"))
//                .url(environment.getProperty("spring.datasource.url"))
//                .driverClassName("com.mysql.jdbc.Driver")
//                .build();
//
//        // Fetch all properties
//        Connection connection = ds.getConnection();
//
//        PreparedStatement preparedStatement = connection.prepareStatement("SELECT key,value FROM service_config");
//        ResultSet rs = preparedStatement.executeQuery();
//
//        //set properties
//        while (rs.next()) {
//            if(rs.getString("value") != null ){
//                propertySource.put(rs.getString("key"), rs.getString("value"));
//                continue;
//            }
//        }
//        rs.close();
//        preparedStatement.clearParameters();
//
//        preparedStatement.close();
//        connection.close();
//
//        // Create a custom property source with the highest precedence and add it to Spring Environment
//        environment.getPropertySources()
//                .replace(
//                        PROPERTY_SOURCE_NAME,
//                        new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource)
//                );
//
//
//    } catch (Throwable e) {
//        throw new RuntimeException(e);
//    }finally {
//
//    }
//        return new ResponseEntity<>("load config thành công", HttpStatus.OK);
//    }
//}
//
