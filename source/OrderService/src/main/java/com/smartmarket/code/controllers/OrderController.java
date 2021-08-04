package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.service.impl.ListenerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


//@RefreshScope
@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class OrderController {

    @Autowired
    TravelInsuranceService travelInsuranceService ;


    @PostMapping(value = "/createorder", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String createOrder(@Valid @RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        return travelInsuranceService.createOrder(createTravelInsuranceBICRequestBaseDetail, request, responseSelvet);
    }

    @PostMapping(value = "/updateorder", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String updateOrder(@Valid @RequestBody BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        return travelInsuranceService.updateOrder(updateTravelInsuranceBICRequestBaseDetail,request,responseSelvet);
    }

    @PostMapping(value = "/getorder", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String getOrder(@Valid @RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        return travelInsuranceService.getOrder(queryTravelInsuranceBICRequest,request,responseSelvet);
    }

    @PostMapping(value = "/getallorders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String getAllOrder(@Valid @RequestBody BaseRequest baseRequest, HttpServletRequest request, HttpServletResponse responseSelvet) {
        return travelInsuranceService.getAllOrder(baseRequest,request,responseSelvet);
    }

}
