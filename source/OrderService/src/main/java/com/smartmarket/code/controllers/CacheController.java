package com.smartmarket.code.controllers;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.ConnectDataBaseException;
import com.smartmarket.code.request.entity.User;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.Response;
import com.smartmarket.code.service.ClientService;
import com.smartmarket.code.service.ServiceConfigService;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.service.impl.ClientServiceImpl;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.management.CacheStatisticsMXBean;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CacheController {


    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    CachingServiceImpl cachingService ;

    @Autowired
    ClientService clientService ;

    @Autowired
    ServiceConfigService serviceConfigService;

    @GetMapping(value = "/hello/refreshcacheandconfig")
    public ResponseEntity<?> getStaticCache() {

        final String PROPERTY_SOURCE_NAME = "databaseProperties";
        Map<String, Object> propertySource = new HashMap<>();
        BaseResponse response = new BaseResponse();
        try {

            //refresh cache config
            propertySource = serviceConfigService.getAllCacheListServiceConfig();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources()
                    .replace(
                            PROPERTY_SOURCE_NAME,
                            new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource)
                    );

            //refresh cache
            cachingService.evictAllCaches();

            response.setDetail(null);
            response.setResponseId(null);
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("refresh configuration is successful");
            response.setResponseTime(new Date().toString());

        } catch (Exception ex) {
            //catch truong hop loi connect database
            if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
