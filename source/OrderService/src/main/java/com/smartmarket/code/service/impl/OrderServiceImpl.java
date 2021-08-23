package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.OrdersServiceEntity;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.UpdateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.OrderService;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.MapperUtils;
import com.smartmarket.code.util.Utils;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OutboxRepository outboxRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    MapperUtils mapperUtils;

    @Autowired
    LogServiceImpl logService;

    @Transactional
    public String createOrder(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        OrdersServiceEntity orders = new OrdersServiceEntity();
        Outbox outBox = new Outbox();
        SagaState sagaState = new SagaState();
        String userName = "";

        String hostName = request.getRemoteHost();

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        try {
            //Create BIC
            CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            Gson gson = new Gson();
            responseCreate = gson.toJson(createTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            //logRequest vs TravelInsuranceService
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"TravelInsuranceService","createOrder","request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            String requestBody = gson.toJson(createTravelInsuranceBICRequest);
            JSONObject j = new JSONObject(requestBody);

            UUID orderId = UUID.randomUUID();
            j.getJSONObject("detail").getJSONObject("orders").put("orderReference",orderId);

            orders.setOrderId(orderId.toString());
            orders.setPayload(j.toString());
            orders.setType(j.getString("type"));
            orders.setState("Pending");

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);
            //get clientid from claims
            if (claims != null) {
                userName = (String) claims.get("user_name");
            }else{
                return null;
            }
            orders.setUserName(userName);

            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);
            orders.setCreatedLogtimestamp(createAt);
            orderRepository.save(orders);

            sagaState.setOrderId(orders.getOrderId().toString());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            if("BICTravelInsurance".equals(j.getString("type"))) {
                sagaState.setType("createTravelInsuranceBIC");
            }
            sagaState.setPayload(j.toString());
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setAggregateType("Order");
            outBox.setAggregateId(createTravelInsuranceBICRequest.getRequestId());
            if("BICTravelInsurance".equals(j.getString("type"))) {
                outBox.setType("createTravelInsuranceBIC");
            }

            j.put("orderId",orders.getOrderId().toString());
            j.put("startTime",startTime);
            j.put("hostName",hostName);
            j.put("clientId",clientId);
            j.put("clientIp",Utils.getClientIp(request));
            outBox.setPayload(j.toString());

            outboxRepository.save(outBox);

        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createTravelInsuranceBICRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
        return null;
    }


    public String updateOrder(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException {
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        Outbox outBox = new Outbox();
        Gson gson = new Gson();
        String userName = "";

        String hostName = request.getRemoteHost();

        try {
            //get log time
            String logtimeStamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logtimeStamp;

            //Update BIC
            UpdateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
            String responseCreate = null;
            responseCreate = gson.toJson(updateTravelInsuranceToBIC);
            JSONObject transactionDetail = new JSONObject(responseCreate);

            //logRequest vs TravelInsuranceService
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "TravelInsuranceService","updateOrder","request",
                    transactionDetail, logtimeStamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            String payload = gson.toJson(updateTravelInsuranceBICRequest);
            String orderReferenceString = updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderReference();

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);
            //get clientid from claims
            if (claims != null) {
                userName = (String) claims.get("user_name");
            }else{
                return "userName is null";
            }

            OrdersServiceEntity orders = orderRepository.findByOrderId(orderReferenceString);
            if(orders != null && (orders.getState().equals("Success")||orders.getState().equals("UpdateAborted")) ) {
                if (orders.getUserName().equals(userName)) {
                    orders.setPayloadUpdate(payload);
                    orders.setState("Pending");
                    orderRepository.save(orders);
                } else {
                    return "Order does not exist with user";
                }
            }else{
                return "Order does not exist or is processing";
            }

//            SagaState sagaState = sagaStateRepository.findByOrderId(UUID.fromString(orderReferenceString));
            SagaState sagaState = sagaStateRepository.findByOrderId(orderReferenceString);
            sagaState.setOrderId(orders.getOrderId().toString());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            if("BICTravelInsurance".equals(updateTravelInsuranceBICRequest.getType())) {
                sagaState.setType("updateTravelInsuranceBIC");
            }
            sagaState.setPayload(payload);
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setAggregateType("Order");
            outBox.setAggregateId(updateTravelInsuranceBICRequest.getRequestId());
            if("BICTravelInsurance".equals(updateTravelInsuranceBICRequest.getType())) {
                outBox.setType("updateTravelInsuranceBIC");
            }

            JSONObject j = new JSONObject(payload);
            j.put("orderId",orders.getOrderId().toString());
            j.put("startTime",startTime);
            j.put("hostName",hostName);
            j.put("clientId",clientId);
            j.put("clientIp",Utils.getClientIp(request));
            outBox.setPayload(j.toString());

            outboxRepository.save(outBox);

        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateTravelInsuranceBICRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
        return null;
    }


    @Override
    public String getOrder(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        Outbox outBox = new Outbox();
        String userName = "";

        String hostName = request.getRemoteHost();

        try {
            //get time log
            String logTimestamp = DateTimeUtils.getCurrentDate();
            String messageTimestamp = logTimestamp;
            //properties log
            String orderID = queryTravelInsuranceBICRequest.getDetail().getOrderId();
            String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
            org.json.JSONObject transactionDetail = new org.json.JSONObject();
            transactionDetail.put("orderId", orderID);
            transactionDetail.put("orderRef", orderReference);

            //logRequest vs TravelInsuranceService
            TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "TravelInsuranceService","getOrder","request",
                    transactionDetail, logTimestamp, messageTimestamp, null);
            logService.createTargetLog(tarObjectRequest);

            Gson gson = new Gson();
            String orderReferenceString = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
            String payload = gson.toJson(queryTravelInsuranceBICRequest);

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);
            //get clientid from claims
            if (claims != null) {
                userName = (String) claims.get("user_name");
            }else{
                return "userName is null";
            }

//            OrdersServiceEntity orders = orderRepository.findByOrderId(UUID.fromString(orderReferenceString));
            OrdersServiceEntity orders = orderRepository.findByOrderId(orderReferenceString);
            if(orders != null &&
                (orders.getState().equals("Success")
                ||orders.getState().equals("UpdateAborted")
                ||orders.getState().equals("GetAborted"))) {

                if(orders.getUserName().equals(userName)) {
                    orders.setState("Pending");
                    orderRepository.save(orders);
                }else {
                    return "Order does not exist with user";
                }
            }else{
                return "Order does not exist or is processing";
            }

            SagaState sagaState = sagaStateRepository.findByOrderId(orderReferenceString);
            sagaState.setOrderId(orders.getOrderId().toString());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            if("BICTravelInsurance".equals(queryTravelInsuranceBICRequest.getType())) {
                sagaState.setType("getTravelInsuranceBIC");
            }
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setAggregateType("Order");
            outBox.setAggregateId(queryTravelInsuranceBICRequest.getRequestId());
            if("BICTravelInsurance".equals(queryTravelInsuranceBICRequest.getType())) {
                outBox.setType("getTravelInsuranceBIC");
            }

            JSONObject j = new JSONObject(payload);
            j.put("orderId",orders.getOrderId().toString());
            j.put("startTime",startTime);
            j.put("hostName",hostName);
            j.put("clientId",clientId);
            j.put("clientIp",Utils.getClientIp(request));
            outBox.setPayload(j.toString());
            outboxRepository.save(outBox);

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
        return null;
    }


    @Override
    public ResponseEntity<?> getAllOrder(BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        String userName = "";
        int totalPage = 0 ;
        BaseResponse response = new BaseResponse();
        ObjectMapper mapper = new ObjectMapper();
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        String hostName = request.getRemoteHost();
        try {
            int page =  queryAllOrdersOfUserRequest.getDetail().getPage()  ;
            int size =  queryAllOrdersOfUserRequest.getDetail().getSize()   ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);

            //get clientid from claims
            if (claims != null) {
                userName = (String) claims.get("user_name");
            }

            Pageable pageable = PageRequest.of(page-1, size);
            //find by user name
            Page<OrdersServiceEntity> allOrders =
                    orderRepository.findByUserName(userName,queryAllOrdersOfUserRequest.getType(),pageable);

            if(!allOrders.isEmpty()) {
                totalPage = (int) Math.ceil((double) allOrders.getTotalElements()/size);

                //set response data to client
                response.setDetail(allOrders);
                response.setPage(page);
                response.setTotalPage(totalPage);
                response.setTotal(allOrders.getTotalElements());

                response.setResponseTime(DateTimeUtils.getCurrentDate());
                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

                String responseBody = mapper.writeValueAsString(response);
                JSONObject transactionDetailResponse = new JSONObject(responseBody);
                //get time log
                String logTimestamp = DateTimeUtils.getCurrentDate();
                String messageTimestamp = logTimestamp;

                //calculate time duration
                String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                int status = responseSelvet.getStatus();
                String responseStatus = Integer.toString(status);

                ServiceObject soaObject = new ServiceObject("serviceLog", queryAllOrdersOfUserRequest.getRequestId(), queryAllOrdersOfUserRequest.getRequestTime(), null, "smartMarket", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                        "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                        response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
                logService.createSOALog2(soaObject);

            }else{
//                return "User has no orders";
                //set response data to client
                response.setResponseTime(DateTimeUtils.getCurrentDate());
                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                response.setResultMessage("User has no orders");

                String responseBody = mapper.writeValueAsString(response);
                JSONObject transactionDetailResponse = new JSONObject(responseBody);

                //get time log
                String logTimestamp = DateTimeUtils.getCurrentDate();
                String messageTimestamp = logTimestamp;

                int status = responseSelvet.getStatus();
                String responseStatus = Integer.toString(status);

                //calculate time duration
                String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

                ServiceObject soaObject = new ServiceObject("serviceLog", queryAllOrdersOfUserRequest.getRequestId(), queryAllOrdersOfUserRequest.getRequestTime(), null, "smartMarket", "client",
                        messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                        "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                        response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
                logService.createSOALog2(soaObject);
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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
