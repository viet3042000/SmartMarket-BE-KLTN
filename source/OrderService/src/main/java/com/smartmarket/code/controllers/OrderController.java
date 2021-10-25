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


    @PostMapping(value = "/create-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createOrder(@Valid @RequestBody BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return orderService.createOrder(createTravelInsuranceBICRequestBaseDetail, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createTravelInsuranceBICRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createTravelInsuranceBICRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
             if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createTravelInsuranceBICRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createTravelInsuranceBICRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

    @PostMapping(value = "/update-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateOrder(@Valid @RequestBody BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return orderService.updateOrder(updateTravelInsuranceBICRequestBaseDetail, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateTravelInsuranceBICRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateTravelInsuranceBICRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateTravelInsuranceBICRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

    @PostMapping(value = "/get-order", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getOrder(@Valid @RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")||roles.contains("ADMIN")) {
                    return orderService.getOrder(queryTravelInsuranceBICRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryTravelInsuranceBICRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    @PostMapping(value = "/get-all-orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllOrderOfUser(@Valid @RequestBody BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN") ||roles.contains("CUSTOMER")) {
                    return orderService.getAllOrderOfUser(queryAllOrdersOfUserRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryAllOrdersOfUserRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryAllOrdersOfUserRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryAllOrdersOfUserRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryAllOrdersOfUserRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryAllOrdersOfUserRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryAllOrdersOfUserRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryAllOrdersOfUserRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    @PostMapping(value = "/get-all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllOrder(@Valid @RequestBody BaseDetail<QueryAllOrderRequest> queryAllOrderRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return orderService.getAllOrder(queryAllOrderRequestBaseDetail, request, responseSelvet);
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
