package com.smartmarket.code.controllers;

import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.util.APIUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    public static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    ConfigurableEnvironment environment;

    @Value("${config}")
    private String config;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    CachingServiceImpl cachingService;

    @Autowired
    AuthorizationService authorizationService;


    @RequestMapping("/reset-password-form")
    public String demoResetPassword(){
        return "demoResetPassword";
    }


    @RequestMapping("/home")
    public String home(){
        try {
            ArrayList<String> roles = authorizationService.getRoles();

            if (roles.contains("CUSTOMER")) {
                return "home";
            } else {
                throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, null,null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch invalid input exception
            if(ex instanceof NullPointerException){
                throw new NullPointerException(ex.getMessage());
            }
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), null);
            }
            else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), null, customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }



//    @PostMapping(value = "/hello/getcache", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<?> getStaticCache(@Valid @RequestBody User user) {
//
//        Response response = new Response();
//        response.setData(config);
////        System.out.println(config);
////        BICTransaction bicTransaction=  new BICTransaction() ;
////
////        bicTransaction.setBicResultCode("000");
////        bicTransaction.setConsumerId("aaa");
////        bicTransaction.setCustomerName("cusname");
////        bicTransaction.setEmail("email");
////        bicTransaction.setFromDate("fromdate");
////        bicTransaction.setBicResultCode("resultCode");
////        bicTransaction.setLogTimestamp(new Date());
////        bicTransaction.setOrderId("21313");
////        bicTransaction.setOrderReference("UUUDI");
////        bicTransaction.setOrdPaidMoney("5172313");
////        bicTransaction.setPhoneNumber("phone");
////        bicTransaction.setRequestId("requestId");
////        bicTransaction.setToDate("todate");
////        bicTransaction.setOrdDate("ordDate");
////        bicTransaction.setProductId("ordDate");
////        bicTransaction.setCustomerAddress("ordDate");
////        bicTransaction.setResultCode("resultCode");
////
////        bicTransactionService.create(bicTransaction) ;
//        return new ResponseEntity<>(response, HttpStatus.OK);
//
//    }

    @GetMapping(value = "/hello")
    public ResponseEntity<?> refresh() throws SQLException {

        final String PROPERTY_SOURCE_NAME = "databaseProperties";
        Map<String, Object> propertySource = new HashMap<>();
        HikariConfig config = new HikariConfig();
        HikariDataSource ds = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            // Build manually datasource to ServiceConfig
            config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
            config.setUsername(environment.getProperty("spring.datasource.username"));
            config.setPassword(environment.getProperty("spring.datasource.password"));
            config.setDriverClassName("org.postgresql.Driver");
            config.setIdleTimeout(10000);
            config.setMaxLifetime(20000);
            config.setMaximumPoolSize(1);
//                .driverClassName("com.mysql.jdbc.Driver")

            ds = new HikariDataSource(config);
            // Fetch all properties
            connection = ds.getConnection();

            preparedStatement = connection.prepareStatement("SELECT key,value FROM service_config");
            rs = preparedStatement.executeQuery();

            //set properties
            while (rs.next()) {
                if (rs.getString("value") != null) {
                    propertySource.put(rs.getString("key"), rs.getString("value"));
                    continue;
                }
            }
            rs.close();
            preparedStatement.clearParameters();
            ds.close();
            preparedStatement.close();
            connection.close();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources()
                    .replace(
                            PROPERTY_SOURCE_NAME,
                            new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource)
                    );


        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }

                if (preparedStatement != null) {
                    preparedStatement.close();
                }

                if (connection != null) {
                    connection.close();
                }
                if (ds != null) {
                    ds.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return new ResponseEntity<>("load config thành công", HttpStatus.OK);
    }
}

