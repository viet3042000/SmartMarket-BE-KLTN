package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

public interface OrderService {
    public ResponseEntity<?> createOrder(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;

    public ResponseEntity<?> updateOrder(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;

    public ResponseEntity<?> getOrder(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;

    public ResponseEntity<?> getAllOrder(BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest,
                                                     HttpServletRequest request,
                                                     HttpServletResponse responseSelvet) throws JsonProcessingException;
}


