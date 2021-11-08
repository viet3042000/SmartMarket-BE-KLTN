package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.*;
import com.smartmarket.code.dao.*;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.UpdateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.service.OrderService;
import com.smartmarket.code.util.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    OrderProductRepository orderProductRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    MapperUtils mapperUtils;

    @Autowired
    SetResponseUtils setResponseUtils;

    @Autowired
    LogServiceImpl logService;

    //user
    public ResponseEntity<?> createOrder(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        //get user token
        Map<String, Object> claims = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed CustomAuthorizeRequestFilter

        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in orderService", HttpStatus.BAD_REQUEST, createTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Orders orders = new Orders();
        Outbox outBox = new Outbox();
        SagaState sagaState = new SagaState();

        String hostName = request.getRemoteHost();

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        BaseResponse response = new BaseResponse();

        //Create BIC
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(createTravelInsuranceBICRequest.getDetail());
        Gson gson = new Gson();
        String responseCreate = gson.toJson(createTravelInsuranceToBIC);
        JSONObject transactionDetail = new JSONObject(responseCreate);

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, createTravelInsuranceBICRequest.getRequestId(), createTravelInsuranceBICRequest.getRequestTime(),"TravelInsuranceService","createOrder","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        String requestBody = gson.toJson(createTravelInsuranceBICRequest);
        JSONObject j = new JSONObject(requestBody);

        UUID orderId = UUID.randomUUID();
        orders.setOrderId(orderId.toString());
        orders.setPayload(j.toString());
        orders.setType(j.getString("type"));
        orders.setState(OrderEntityState.PENDING);

        //get client Id
        String clientId = JwtUtils.getClientId() ;

        orders.setUserName(userName);
        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);
        orders.setCreatedLogtimestamp(createAt);
        orderRepository.save(orders);

        sagaState.setCreatedLogtimestamp(createAt);
        sagaState.setId(createTravelInsuranceBICRequest.getRequestId());
        sagaState.setCurrentStep("");
        sagaState.setStepState("");
        if("BICTravelInsurance".equals(j.getString("type"))) {
            sagaState.setType(SagaStateType.CREATE_TRAVEL_INSURANCE_BIC);
        }
        sagaState.setPayload(j.toString());
        sagaState.setStatus(SagaStateStatus.STARTED);
        sagaState.setAggregateId(orderId.toString());
        sagaStateRepository.save(sagaState);

        outBox.setCreatedLogtimestamp(createAt);
        outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);
        outBox.setAggregateId(orderId.toString());
        if("BICTravelInsurance".equals(j.getString("type"))) {
            outBox.setType(OutboxType.CREATE_TRAVEL_INSURANCE_BIC);
        }
        j.put("startTime",startTime);
        j.put("hostName",hostName);
        j.put("clientId",clientId);
        j.put("clientIp",Utils.getClientIp(request));
        outBox.setPayload(j.toString());
        outboxRepository.save(outBox);

        response.setOrderId(orderId.toString());
        response.setResponseId(createTravelInsuranceBICRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //user
    public ResponseEntity<?>  updateOrder(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        //get user token
        Map<String, Object> claims = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        claims = JwtUtils.getClaimsMap(authentication);

        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in orderService", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        Gson gson = new Gson();

        String hostName = request.getRemoteHost();

        BaseResponse response = new BaseResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //get log time
        String logtimeStamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logtimeStamp;

        UpdateTravelInsuranceToBIC updateTravelInsuranceToBIC = mapperUtils.mapUpdateObjectToBIC(updateTravelInsuranceBICRequest.getDetail());
        String responseCreate = gson.toJson(updateTravelInsuranceToBIC);
        JSONObject transactionDetail = new JSONObject(responseCreate);

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, updateTravelInsuranceBICRequest.getRequestId(), updateTravelInsuranceBICRequest.getRequestTime(), "TravelInsuranceService","updateOrder","request",
                transactionDetail, logtimeStamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        String payload = gson.toJson(updateTravelInsuranceBICRequest);
        String orderIdString = updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderEntityId();
        OrderProduct orderProduct = orderProductRepository.findByOrderId(orderIdString).orElse(null);
        String orderReference = "";
        if(orderProduct!= null){
            orderReference = orderProduct.getProductId();
        }else {
            //after receives create_mess from kk, orderService creates orderProduct
            //--> if orderProduct does not exist ~ Order is Pending
            throw new CustomException("Order is Pending", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //get client Id
        String clientId = JwtUtils.getClientId() ;

        Orders order = orderRepository.findByOrderId(orderIdString).orElse(null);
        if(order != null && order.getState().equals("Succeeded")) {
            if (order.getUserName().equals(userName)) {
                order.setPayloadUpdate(payload);
                orderRepository.save(order);
            } else {
                throw new CustomException("Order does not exist with user", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }else{
            throw new CustomException("Order is Aborted", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);

        SagaState sagaState = new SagaState();
        sagaState.setCreatedLogtimestamp(createAt);
        sagaState.setId(updateTravelInsuranceBICRequest.getRequestId());
        sagaState.setCurrentStep("");
        sagaState.setStepState("");
        if("BICTravelInsurance".equals(updateTravelInsuranceBICRequest.getType())) {
            sagaState.setType(SagaStateType.UPDATE_TRAVEL_INSURANCE_BIC);
        }
        sagaState.setPayload(payload);
        sagaState.setStatus(SagaStateStatus.STARTED);
        sagaState.setAggregateId(orderIdString);
        sagaStateRepository.save(sagaState);

        Outbox outBox = new Outbox();
        outBox.setCreatedLogtimestamp(createAt);
        outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);
        outBox.setAggregateId(orderIdString);
        if("BICTravelInsurance".equals(updateTravelInsuranceBICRequest.getType())) {
            outBox.setType(OutboxType.UPDATE_TRAVEL_INSURANCE_BIC);
        }
        JSONObject j = new JSONObject(payload);
        j.getJSONObject("detail").getJSONObject("orders").remove("orderEntityId");
        j.getJSONObject("detail").getJSONObject("orders").put("orderReference",orderReference);
        j.put("startTime",startTime);
        j.put("hostName",hostName);
        j.put("clientId",clientId);
        j.put("clientIp",Utils.getClientIp(request));
        outBox.setPayload(j.toString());
        outboxRepository.save(outBox);

        response.setOrderId(orderIdString);
        response.setResponseId(updateTravelInsuranceBICRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //user + admin
    @Override
    public ResponseEntity<?> getOrder(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        String userName = queryTravelInsuranceBICRequest.getDetail().getUserName();
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in orderService", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        String hostName = request.getRemoteHost();

        BaseResponse response = new BaseResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        //properties log
        String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderEntityId();
        org.json.JSONObject transactionDetail = new org.json.JSONObject();
        transactionDetail.put("orderId", orderId);

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryTravelInsuranceBICRequest.getRequestId(), queryTravelInsuranceBICRequest.getRequestTime(), "TravelInsuranceService","getOrder","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        Gson gson = new Gson();
        String orderIdString = queryTravelInsuranceBICRequest.getDetail().getOrderEntityId();
        OrderProduct orderProduct = orderProductRepository.findByOrderId(orderIdString).orElse(null);
        String orderReference = "";
        if(orderProduct!= null){
            orderReference = orderProduct.getProductId();
        }else {
            //after receives create_mess from kk, orderService creates orderProduct
            //--> if orderProduct does not exist ~ Order is Pending
            throw new CustomException("Order is Pending", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        String payload = gson.toJson(queryTravelInsuranceBICRequest);

        //get client Id
        String clientId = JwtUtils.getClientId() ;

        Orders order = orderRepository.findByOrderIdAndUserName(orderIdString,userName).orElse(null);
        if (order == null) {
            throw new CustomException("Order does not exist with user", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        response.setDetail(order);
        response.setOrderId(orderIdString);
        response.setResponseId(queryTravelInsuranceBICRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //user + admin
    @Override
    public ResponseEntity<?> getListOrderOfUser(BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        String userName = queryAllOrdersOfUserRequest.getDetail().getUserName();
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in orderService", HttpStatus.BAD_REQUEST, queryAllOrdersOfUserRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        String hostName = request.getRemoteHost();

        int page =  queryAllOrdersOfUserRequest.getDetail().getPage()  ;
        int size =  queryAllOrdersOfUserRequest.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

        //find by user name
//        Page<Orders> allOrders =
//                orderRepository.findByUserName(userName,pageable);

        //find by user name and type
        Page<Orders> allOrders =
                orderRepository.findByUserNameAndType(userName,queryAllOrdersOfUserRequest.getType(),pageable);

        if(!allOrders.isEmpty()) {
            totalPage = (int) Math.ceil((double) allOrders.getTotalElements()/size);

            //set response data to client
            response.setResponseId(queryAllOrdersOfUserRequest.getRequestId());
            response.setDetail(allOrders.getContent());
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
                    messageTimestamp, "orderservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

        }else{
            //set response data to client
            response.setResponseId(queryAllOrdersOfUserRequest.getRequestId());
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
                    messageTimestamp, "orderservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //admin
    @Override
    public ResponseEntity<?> getListOrder(BaseDetail<QueryAllOrderRequest> queryAllOrderRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();

        int page =  queryAllOrderRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllOrderRequestBaseDetail.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

//        All orders
        Page<Orders> allOrders =
                orderRepository.getAll(pageable);

//        All orders by type
//        Page<Orders> allOrders =
//                orderRepository.getAllByType(pageable,queryAllOrdersOfUserRequest.getType());

        //get time log
        String hostName = request.getRemoteHost();
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);

        if(!allOrders.isEmpty()) {
            totalPage = (int) Math.ceil((double) allOrders.getTotalElements()/size);

            //set response data to client
            response.setResponseId(queryAllOrderRequestBaseDetail.getRequestId());
            response.setDetail(allOrders.getContent());
            response.setPage(page);
            response.setTotalPage(totalPage);
            response.setTotal(allOrders.getTotalElements());

            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllOrderRequestBaseDetail.getRequestId(), queryAllOrderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "orderservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

        }else{
            //set response data to client
            response.setResponseId(queryAllOrderRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("User has no orders");

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllOrderRequestBaseDetail.getRequestId(), queryAllOrderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "orderservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
