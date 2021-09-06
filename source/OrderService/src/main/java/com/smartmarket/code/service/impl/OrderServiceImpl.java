package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
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
import com.smartmarket.code.response.ResponseError;
import com.smartmarket.code.service.OrderService;
import com.smartmarket.code.util.*;
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

    @Transactional
    public ResponseEntity<?> createOrder(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
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

        BaseResponse response = new BaseResponse();
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
            orders.setOrderId(orderId.toString());
            orders.setPayload(j.toString());
            orders.setType(j.getString("type"));
            orders.setState(OrderEntityState.PENDING);

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed CustomAuthorizeRequestFilter

            userName = (String) claims.get("user_name");
            User user = userRepository.findByUsername(userName).orElse(null);
            if(user == null){
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderId.toString(),
                                                                "userName does not exist in orderService",
                                                                createTravelInsuranceBICRequest.getRequestId(),
                                                                createTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            orders.setUserName(userName);
            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);
            orders.setCreatedLogtimestamp(createAt);

            UserRole userRole = userRoleRepository.findByUserName(userName).orElse(null);
            if(userRole!= null){
                if("CUSTOMER".equals(userRole.getRoleName())){
                    orderRepository.save(orders);
                }else {
                    ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderId.toString(),
                                                                    "userRole is not CUSTOMER",
                                                                    createTravelInsuranceBICRequest.getRequestId(),
                                                                    createTravelInsuranceBICRequest.getRequestTime());
                    return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }
            }else {
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderId.toString(),
                                                                "userRole is null",
                                                                createTravelInsuranceBICRequest.getRequestId(),
                                                                createTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }


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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?>  updateOrder(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        Gson gson = new Gson();
        String userName = "";

        String hostName = request.getRemoteHost();

        BaseResponse response = new BaseResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
            String orderIdString = updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderEntityId();
            OrderProduct orderProduct = orderProductRepository.findByOrderId(orderIdString).orElse(null);
            String orderReference = "";
            if(orderProduct!= null){
                orderReference = orderProduct.getProductId();
            }else {
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                        "orderProduct does not exist",
                        updateTravelInsuranceBICRequest.getRequestId(),
                        updateTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);

            userName = (String) claims.get("user_name");
            User user = userRepository.findByUsername(userName).orElse(null);
            if(user == null){
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                                                "userName does not exist in orderService",
                                                                updateTravelInsuranceBICRequest.getRequestId(),
                                                                updateTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            UserRole userRole = userRoleRepository.findByUserName(userName).orElse(null);
            if(userRole!= null){
                if("CUSTOMER".equals(userRole.getRoleName())){
                    Optional<OrdersServiceEntity> orders = orderRepository.findByOrderId(orderIdString);
                    if(orders.isPresent() && orders.get().getState().equals("Succeeded")) {
                        OrdersServiceEntity order = orders.get();
                        if (order.getUserName().equals(userName)) {
                            order.setPayloadUpdate(payload);
                            orderRepository.save(order);
                        } else {
                            ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                                                            "Order does not exist with user",
                                                                            updateTravelInsuranceBICRequest.getRequestId(),
                                                                            updateTravelInsuranceBICRequest.getRequestTime());
                            return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                                                        "Order does not exist or is processing",
                                                                        updateTravelInsuranceBICRequest.getRequestId(),
                                                                        updateTravelInsuranceBICRequest.getRequestTime());
                        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                    }
                }else {
                    ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                            "userRole is not CUSTOMER",
                            updateTravelInsuranceBICRequest.getRequestId(),
                            updateTravelInsuranceBICRequest.getRequestTime());
                    return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }
            }else{
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                        "userRole is null",
                        updateTravelInsuranceBICRequest.getRequestId(),
                        updateTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);

//            SagaState sagaState = sagaStateRepository.findByOrderId(orderReferenceString);
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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getOrder(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        String userName = "";
        String hostName = request.getRemoteHost();

        BaseResponse response = new BaseResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
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
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                        "orderProduct does not exist",
                        queryTravelInsuranceBICRequest.getRequestId(),
                        queryTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }
            String payload = gson.toJson(queryTravelInsuranceBICRequest);

            //get client Id
            String clientId = JwtUtils.getClientId() ;

            //get user token
            Map<String, Object> claims = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            claims = JwtUtils.getClaimsMap(authentication);

            userName = (String) claims.get("user_name");
            User user = userRepository.findByUsername(userName).orElse(null);
            if(user == null){
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                                                "userName does not exist in orderService",
                                                                queryTravelInsuranceBICRequest.getRequestId(),
                                                                queryTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            UserRole userRole = userRoleRepository.findByUserName(userName).orElse(null);
            if(userRole!= null) {
                if ("CUSTOMER".equals(userRole.getRoleName())) {
                    //
                    Optional<OrdersServiceEntity> orders = orderRepository.findByOrderId(orderIdString);
                    if (orders.isPresent() && orders.get().getState().equals("Succeeded")) {
                        OrdersServiceEntity order = orders.get();
                        if (!order.getUserName().equals(userName)) {
                            ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                    "Order does not exist with user",
                                    queryTravelInsuranceBICRequest.getRequestId(),
                                    queryTravelInsuranceBICRequest.getRequestTime());

                            return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                                "Order does not exist or is processing",
                                queryTravelInsuranceBICRequest.getRequestId(),
                                queryTravelInsuranceBICRequest.getRequestTime());
                        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                    }
                }else {
                    ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                            "userRole is not CUSTOMER",
                            queryTravelInsuranceBICRequest.getRequestId(),
                            queryTravelInsuranceBICRequest.getRequestTime());
                    return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
                }
            }else{
                ResponseError responseError = setResponseUtils.setResponseErrorOrderService(orderReference,
                        "userRole is null",
                        queryTravelInsuranceBICRequest.getRequestId(),
                        queryTravelInsuranceBICRequest.getRequestTime());
                return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
            }

            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);

            SagaState sagaState = new SagaState();
            sagaState.setCreatedLogtimestamp(createAt);
            sagaState.setId(queryTravelInsuranceBICRequest.getRequestId());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            if ("BICTravelInsurance".equals(queryTravelInsuranceBICRequest.getType())) {
                sagaState.setType(SagaStateType.GET_TRAVEL_INSURANCE_BIC);
            }
            sagaState.setPayload(payload);
            sagaState.setStatus(SagaStateStatus.STARTED);
            sagaState.setAggregateId(orderIdString);
            sagaStateRepository.save(sagaState);

            Outbox outBox = new Outbox();
            outBox.setCreatedLogtimestamp(createAt);
            outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);
            outBox.setAggregateId(orderIdString);
            if ("BICTravelInsurance".equals(queryTravelInsuranceBICRequest.getType())) {
                outBox.setType(OutboxType.GET_TRAVEL_INSURANCE_BIC);
            }
            JSONObject j = new JSONObject(payload);
            j.getJSONObject("detail").remove("orderEntityId");
            j.getJSONObject("detail").put("inquiryType", 2);
            j.getJSONObject("detail").put("orderReference", orderReference);
            j.put("startTime", startTime);
            j.put("hostName", hostName);
            j.put("clientId", clientId);
            j.put("clientIp", Utils.getClientIp(request));
            outBox.setPayload(j.toString());
            outboxRepository.save(outBox);

            response.setOrderId(orderIdString);
            response.setResponseId(queryTravelInsuranceBICRequest.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("Successful");
            //

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
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> getAllOrder(BaseDetail<QueryAllOrdersOfUserRequest> queryAllOrdersOfUserRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        String userName = "";
        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
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
                        messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                        "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                        response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
                logService.createSOALog2(soaObject);

            }else{
//                return "User has no orders";
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
