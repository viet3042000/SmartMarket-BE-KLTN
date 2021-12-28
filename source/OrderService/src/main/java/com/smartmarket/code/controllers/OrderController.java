package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.OrderService;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.ArrayList;


@RestController
@RequestMapping("/order/order-service/v1/")
public class OrderController {

    @Autowired
    OrderService orderService ;

    @Autowired
    AuthorizationService authorizationService;

    //consumer
    @PostMapping(value = "/create-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createOrder(@Valid @RequestBody BaseDetail<CreateOrderRequest> createOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return orderService.createOrder(createOrderRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
             if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createOrderRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createOrderRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createOrderRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

    //consumer
    @PostMapping(value = "/cancel-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> cancelOrder(@Valid @RequestBody BaseDetail<CancelOrderRequest> cancelOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return orderService.cancelOrder(cancelOrderRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(cancelOrderRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(cancelOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(cancelOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), cancelOrderRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), cancelOrderRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

    //consumer + admin
    @PostMapping(value = "/get-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getOrder(@Valid @RequestBody BaseDetail<QueryOrderRequest> queryOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")||roles.contains("ADMIN")) {
                    return orderService.getOrder(queryOrderRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryOrderRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryOrderRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryOrderRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryOrderRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    //admin + customer
    @PostMapping(value = "/get-all-orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllOrder(@Valid @RequestBody BaseDetail<QueryAllOrderRequest> queryAllOrderRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")||roles.contains("CUSTOMER")) {
                    return orderService.getAllOrders(queryAllOrderRequestBaseDetail, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryAllOrderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryAllOrderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryAllOrderRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryAllOrderRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryAllOrderRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryAllOrderRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryAllOrderRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

}
