package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.*;
import com.smartmarket.code.dao.*;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entity.ItemDetailCancelRequest;
import com.smartmarket.code.request.entity.ItemDetailCreateRequest;
import com.smartmarket.code.request.entity.QueryConditions;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.service.OrderService;
import com.smartmarket.code.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;


    //consumer
    public ResponseEntity<?> createOrder(BaseDetail<CreateOrderRequest> createOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed CustomAuthorizeRequestFilter

        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName doesn't exist in orderService", HttpStatus.BAD_REQUEST, createOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String hostName = request.getRemoteHost();
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        JSONObject transactionDetail = new JSONObject(createOrderRequest.getDetail());
        UUID orderId = UUID.randomUUID();
        transactionDetail.put("orderId",orderId.toString());

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, createOrderRequest.getRequestId(), createOrderRequest.getRequestTime(),"TravelInsuranceService","createOrder","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        JSONObject j = new JSONObject(createOrderRequest);

        List<Product> listProducts = new ArrayList<>();
        ArrayList<ItemDetailCreateRequest> orderItems = createOrderRequest.getDetail().getOrderItems();
        for(ItemDetailCreateRequest itemDetailCreateRequest : orderItems) {
            //find product by productName and set AggregateType = productType
            String productName = itemDetailCreateRequest.getProductName();
            Product product = productRepository.findByProductName(productName).orElse(null);
            if (product == null) {
                throw new CustomException("Product doesn't exist in orderService", HttpStatus.BAD_REQUEST, createOrderRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
            if(!"Completed".equals(product.getState())){
                throw new CustomException("Uncompleted product", HttpStatus.BAD_REQUEST, createOrderRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
            itemDetailCreateRequest.setProductProvider(product.getProductProvider());
            itemDetailCreateRequest.setProductType(product.getType());
            listProducts.add(product);//to get product without find again in DB
        }
        createOrderRequest.getDetail().setOrderItems(orderItems);

        Orders orders = new Orders();
        orders.setOrderId(orderId.toString());
        orders.setState(OrderState.PENDING);
        orders.setPayload(j.toString());
        orders.setUserName(userName);
        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);
        orders.setCreatedLogtimestamp(createAt);
        orders.setQuantityItems(orderItems.size());
        orders.setOrderPrice(createOrderRequest.getDetail().getOrderPrice().toString());
        orderRepository.save(orders);

        SagaState sagaState = new SagaState();
        sagaState.setCreatedLogtimestamp(createAt);
        sagaState.setId(createOrderRequest.getRequestId());
        sagaState.setCurrentStep("");
        sagaState.setStepState("");
        sagaState.setStatus(SagaStatus.STARTED);
        sagaState.setType(SagaType.CREATE_ORDER);
        sagaState.setPayload(j.toString());
        sagaState.setAggregateId(orderId.toString());
        sagaStateRepository.save(sagaState);

        ItemDetailCreateRequest itemDetailCreateRequest = orderItems.get(0);
        JSONObject itemDetailJsonObject = new JSONObject(createOrderRequest);
        itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
        itemDetailJsonObject.put("startTime", startTime);
        itemDetailJsonObject.put("hostName", hostName);
        itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));
        itemDetailJsonObject.put("createItemIndex", 0);

        Outbox outBox = new Outbox();
        outBox.setCreatedLogtimestamp(createAt);
        if(itemDetailCreateRequest.getProductProvider().equals("BIC") && itemDetailCreateRequest.getProductType().equals("bảo hiểm du lịch")){
            outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
        }
        outBox.setAggregateId(orderId.toString());
        outBox.setType(OutboxType.CREATE_ORDER);
        outBox.setPayload(itemDetailJsonObject.toString());
        outboxRepository.save(outBox);

        BaseResponse response = new BaseResponse();
        response.setOrderId(orderId.toString());
        response.setResponseId(createOrderRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //consumer
    @Transactional
    public ResponseEntity<?> cancelOrder(BaseDetail<CancelOrderRequest> cancelOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException, ParseException {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);

        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName doesn't exist in orderService", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        String hostName = request.getRemoteHost();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //get log time
        String logtimeStamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logtimeStamp;

        Gson gson = new Gson();
        JSONObject transactionDetail = new JSONObject(cancelOrderRequest.getDetail());

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, cancelOrderRequest.getRequestId(), cancelOrderRequest.getRequestTime(), "TravelInsuranceService","updateOrder","request",
                transactionDetail, logtimeStamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        String payload = gson.toJson(cancelOrderRequest);
        String orderIdString = cancelOrderRequest.getDetail().getOrderId();

//        Orders order = orderRepository.findByOrderId(orderIdString).orElse(null);
        Orders order = orderRepository.findAndLock(orderIdString).orElse(null);
        if(order == null){
            throw new CustomException("Order doesn't exist in OrderService", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        if (!order.getUserName().equals(userName)){
            throw new CustomException("Order doesn't exist with user", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        if(!order.getState().equals(OrderState.SUCCEEDED)){
            throw new CustomException("Order's state is not Succeeded", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        ArrayList<ItemDetailCancelRequest> orderItems = new ArrayList<>();//empty
        //order is succeeded --> orderProduct is succeeded
        List<OrderProduct> orderProductList = orderProductRepository.findByOrderId(orderIdString);

        for(int i =0 ; i < orderProductList.size(); i++) {
            ItemDetailCancelRequest itemDetailCancelRequest = new ItemDetailCancelRequest();
            OrderProduct orderProduct = orderProductList.get(i);
            //in case product deleted after order had created
            Product product = productRepository.findByProductName(orderProduct.getProductName()).orElse(null);
            if(product == null){
                throw new CustomException("Product doesn't exist", HttpStatus.BAD_REQUEST, cancelOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
            orderProduct.setState("Canceling");

            itemDetailCancelRequest.setProductName(orderProduct.getProductName());
            itemDetailCancelRequest.setOrderReference(orderProduct.getProductId());
            itemDetailCancelRequest.setProductProvider(product.getProductProvider());
            itemDetailCancelRequest.setProductType(product.getType());

            orderItems.add(itemDetailCancelRequest);
        }
        cancelOrderRequest.getDetail().setOrderItems(orderItems);

        order.setState(OrderState.CANCELING);
        orderRepository.save(order);

        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);

        SagaState sagaState = new SagaState();
        sagaState.setCreatedLogtimestamp(createAt);
        sagaState.setId(cancelOrderRequest.getRequestId());
        sagaState.setCurrentStep("");
        sagaState.setStepState("");
        sagaState.setType(SagaType.CANCEL_ORDER);
        sagaState.setPayload(payload);
        sagaState.setStatus(SagaStatus.STARTED);
        sagaState.setAggregateId(orderIdString);
        sagaStateRepository.save(sagaState);

        ItemDetailCancelRequest itemDetailCancelRequest = orderItems.get(0);
        Outbox outBox = new Outbox();
        outBox.setCreatedLogtimestamp(createAt);
        if(itemDetailCancelRequest.getProductProvider().equals("BIC") && itemDetailCancelRequest.getProductType().equals("bảo hiểm du lịch")){
            outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
        }
        outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);
        outBox.setAggregateId(orderIdString);
        outBox.setType(OutboxType.CANCEL_ORDER);

        JSONObject itemDetailJsonObject = new JSONObject(cancelOrderRequest);
        itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
        itemDetailJsonObject.put("startTime", startTime);
        itemDetailJsonObject.put("hostName", hostName);
        itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));
        itemDetailJsonObject.put("cancelItemIndex", 0);
        outBox.setPayload(itemDetailJsonObject.toString());
        outboxRepository.save(outBox);

        BaseResponse response = new BaseResponse();
        response.setOrderId(orderIdString);
        response.setResponseId(cancelOrderRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //consumer + admin
    @Override
    public ResponseEntity<?> getOrder(BaseDetail<QueryOrderRequest> queryOrderRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        String userName = queryOrderRequest.getDetail().getUserName();
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName doesn't exist in orderService", HttpStatus.BAD_REQUEST, queryOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        //properties log
        String orderId = queryOrderRequest.getDetail().getOrderEntityId();
        JSONObject transactionDetail = new JSONObject();
        transactionDetail.put("orderId", orderId);

        //logRequest vs TravelInsuranceService
        TargetObject tarObjectRequest = new TargetObject("targetLog", null, queryOrderRequest.getRequestId(), queryOrderRequest.getRequestTime(), "TravelInsuranceService","getOrder","request",
                transactionDetail, logTimestamp, messageTimestamp, null);
        logService.createTargetLog(tarObjectRequest);

        Orders order = orderRepository.findByOrderIdAndUserName(orderId,userName).orElse(null);
        if (order == null) {
            throw new CustomException("Order doesn't exist with user", HttpStatus.BAD_REQUEST, queryOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        if(!order.getState().equals(OrderState.SUCCEEDED)){
            throw new CustomException("Order's state is not Succeeded", HttpStatus.BAD_REQUEST, queryOrderRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        BaseResponse response = new BaseResponse();
        response.setDetail(order);
        response.setOrderId(orderId);
        response.setResponseId(queryOrderRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage("Successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //customer + admin
    @Override
    public ResponseEntity<?> getAllOrders(BaseDetail<QueryAllOrderRequest> queryAllOrderRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);

        String userName = (String) claims.get("user_name");
        UserRole userRole = userRoleRepository.findByUserName(userName).orElse(null);
        if(userRole == null){
            throw new CustomException("UserName doesn't exist in orderService", HttpStatus.BAD_REQUEST, queryAllOrderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //eliminate null value from conditions
        JSONObject detailCondition = new JSONObject(queryAllOrderRequestBaseDetail.getDetail().getConditions());
        if(!"ADMIN".equals(userRole.getRoleName())){
            detailCondition.put("userName",userName);
        }
        int page =  queryAllOrderRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllOrderRequestBaseDetail.getDetail().getSize()   ;

        String userNameCondition = "";
        String stateCondition = "";
        stateCondition = CheckExistUtils.hasValue(detailCondition,"state") ? detailCondition.getString("state") : null;
        userNameCondition = CheckExistUtils.hasValue(detailCondition,"userName") ? detailCondition.getString("userName") : null;

        List<Orders> allOrders = orderRepository.findListOrder(userNameCondition,stateCondition);
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        //get time log
        String hostName = request.getRemoteHost();
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        if(allOrders.isEmpty()){
            response.setResponseId(queryAllOrderRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("No orders satisfy the condition");

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllOrderRequestBaseDetail.getRequestId(), queryAllOrderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "orderservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List listOrders = PagingUtil.getPageLimit(allOrders, page, size);

        if(!allOrders.isEmpty()) {
            int totalPage = (int) Math.ceil((double) allOrders.size() / size);

            //set response data to client
            response.setResponseId(queryAllOrderRequestBaseDetail.getRequestId());
            response.setDetail(listOrders);
            response.setPage(page);
            response.setTotalPage(totalPage);
            response.setTotal(allOrders.stream().count());
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
            response.setResultMessage("No orders satisfy the condition");

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
