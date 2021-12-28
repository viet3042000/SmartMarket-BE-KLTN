package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

public interface OrderService {
    //consumer
    //1 order = 1 type product
    public ResponseEntity<?> createOrder(BaseDetail<CreateOrderRequest> createOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;

    //consumer
    //hủy order đã được BIC tạo thành công
    public ResponseEntity<?> cancelOrder(BaseDetail<CancelOrderRequest> cancelOrderRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;

    //consumer + admin
    public ResponseEntity<?> getOrder(BaseDetail<QueryOrderRequest> queryOrderRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException;


    //admin + customer
    public ResponseEntity<?> getAllOrders(BaseDetail<QueryAllOrderRequest> queryAllOrderRequestBaseDetail,
                                          HttpServletRequest request,
                                          HttpServletResponse responseSelvet) throws JsonProcessingException;
}


