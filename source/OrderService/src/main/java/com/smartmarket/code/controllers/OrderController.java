package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;


@RestController
@RequestMapping("/order/order-service/v1/")
public class OrderController {

    @Autowired
    OrderService orderService ;


    @PostMapping(value = "/create-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createOrder(@Valid @RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        return orderService.createOrder(createTravelInsuranceBICRequestBaseDetail, request, responseSelvet);
    }

    @PostMapping(value = "/update-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateOrder(@Valid @RequestBody BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        return orderService.updateOrder(updateTravelInsuranceBICRequestBaseDetail,request,responseSelvet);
    }

    @PostMapping(value = "/get-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getOrder(@Valid @RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        return orderService.getOrder(queryTravelInsuranceBICRequest,request,responseSelvet);
    }

    @PostMapping(value = "/get-all-orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllOrder(@Valid @RequestBody BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        return orderService.getAllOrder(queryAllOrdersOfUserRequest,request,responseSelvet);
    }

}
