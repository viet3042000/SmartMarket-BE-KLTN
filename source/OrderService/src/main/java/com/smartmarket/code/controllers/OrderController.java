package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.service.impl.ListenerServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


//@RefreshScope
@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class OrderController {

    @Autowired
    TravelInsuranceService travelInsuranceService ;

    @Autowired
    ListenerServiceImp listenerServiceImp;

    @PostMapping(value = "/createorder", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String createOrder(@Valid @RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        return travelInsuranceService.createOrder(createTravelInsuranceBICRequestBaseDetail, request, responseSelvet);
    }

}
