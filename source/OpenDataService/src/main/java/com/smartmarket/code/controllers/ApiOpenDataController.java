package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.QueryOpenDataRequest;
import com.smartmarket.code.service.OpenDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/opendata-service/v1/")
public class ApiOpenDataController {

    @Autowired
    OpenDataService openDataService ;

    //    /opendata-service/v1/query-data/**
    @PostMapping(value = "/inquiry-smart-search-contract", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> queryOpenData(@Valid @RequestBody BaseDetail<QueryOpenDataRequest> queryOpenDataRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        return openDataService.queryOpenData(queryOpenDataRequestBaseDetail,request,responseSelvet) ;
    }


}
