package com.smartmarket.code.controllers;

import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.util.APIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/home")
    public String home(){
        return "home";
    }

    @RequestMapping("/beforelogin")
    public String login(){
        return "login";
    }


}

