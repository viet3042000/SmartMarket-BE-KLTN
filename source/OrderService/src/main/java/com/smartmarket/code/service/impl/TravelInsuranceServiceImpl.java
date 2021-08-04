package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.model.OrderOutbox;
import com.smartmarket.code.model.OrdersServiceEntity;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.util.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TravelInsuranceServiceImpl implements TravelInsuranceService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OutboxRepository outboxRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    @Autowired
    UserRepository userRepository;


    public String createOrder(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException {

        OrdersServiceEntity orders = new OrdersServiceEntity();
        OrderOutbox outBox = new OrderOutbox();
        SagaState sagaState = new SagaState();
        String userName = "";

        try {
            Gson gson = new Gson();
            String requestBody = gson.toJson(createTravelInsuranceBICRequestBaseDetail);
            JSONObject j = new JSONObject(requestBody);

            j.getJSONObject("detail").getJSONObject("orders").remove("orderReference");
            UUID orderId = UUID.randomUUID();
            j.getJSONObject("detail").getJSONObject("orders").put("orderReference",orderId);
            String payload = j.toString();

//            check orderReference to know order have been created hoặc để cho BIC tự check như hiện tại.
//            (cần thêm 1 cột orderReference để lưu những order đã tạo)
//            (order đã mới nếu trùng orderReference của order đã lưu và order đã lưu có trạng thái Aborted)

            orders.setOrderId(orderId);
            orders.setPayload(payload);
            orders.setType("createTravelInsuranceBIC");
            orders.setState("Pending");

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

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);
            orders.setCreateAt(createAt);
            orderRepository.save(orders);

            sagaState.setOrderId(orders.getOrderId());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            sagaState.setType("createTravelInsuranceBIC");
            sagaState.setPayload(payload);
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setOrderId(orders.getOrderId());
            outBox.setAggregateType("TravelInsuranceService");
            outBox.setAggregateId(createTravelInsuranceBICRequestBaseDetail.getRequestId());
            outBox.setPayload(payload);
            outBox.setType("createTravelInsuranceBIC");
            outboxRepository.save(outBox);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public String updateOrder(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException {
        OrderOutbox outBox = new OrderOutbox();
        Gson gson = new Gson();
        String userName = "";

        try {
            String payload = gson.toJson(updateTravelInsuranceBICRequest);
            String orderReferenceString = updateTravelInsuranceBICRequest.getDetail().getOrders().getOrderReference();

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

            OrdersServiceEntity orders = orderRepository.findByOrderId(UUID.fromString(orderReferenceString));
            if(orders != null && (orders.getState().equals("Success")||orders.getState().equals("UpdateAborted")) ) {
                if (orders.getUserName().equals(userName)) {

                    orders.setPayloadUpdate(payload);
                    orders.setType("updateTravelInsuranceBIC");
                    orders.setState("Pending");
                    orderRepository.save(orders);
                } else {
                    return "Order does not exist with user";
                }
            }else{
                return "Order does not exist or is processing";
            }

            SagaState sagaState = sagaStateRepository.findByOrderId(UUID.fromString(orderReferenceString));
            sagaState.setOrderId(orders.getOrderId());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            sagaState.setType("updateTravelInsuranceBIC");
            sagaState.setPayload(payload);
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setOrderId(orders.getOrderId());
            outBox.setAggregateType("TravelInsuranceService");
            outBox.setAggregateId(updateTravelInsuranceBICRequest.getRequestId());
            outBox.setPayload(payload);
            outBox.setType("updateTravelInsuranceBIC");
            outboxRepository.save(outBox);

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }


    @Override
    public String getOrder(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        OrderOutbox outBox = new OrderOutbox();
        String userName = "";
        try {
            Gson gson = new Gson();
            String orderReferenceString = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
            String payload = gson.toJson(queryTravelInsuranceBICRequest);

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

            OrdersServiceEntity orders = orderRepository.findByOrderId(UUID.fromString(orderReferenceString));
            if(orders != null &&
                (orders.getState().equals("Success")
                ||orders.getState().equals("UpdateAborted")
                ||orders.getState().equals("GetAborted"))) {

                if(orders.getUserName().equals(userName)) {
                    orders.setType("getTravelInsuranceBIC");
                    orders.setState("Pending");
                    orderRepository.save(orders);
                }else {
                    return "Order does not exist with user";
                }
            }else{
                return "Order does not exist or is processing";
            }

            SagaState sagaState = sagaStateRepository.findByOrderId(UUID.fromString(orderReferenceString));
            sagaState.setOrderId(orders.getOrderId());
            sagaState.setCurrentStep("");
            sagaState.setStepState("");
            sagaState.setType("getTravelInsuranceBIC");
            sagaState.setStatus("STARTED");
            sagaStateRepository.save(sagaState);

            outBox.setOrderId(orders.getOrderId());
            outBox.setAggregateType("TravelInsuranceService");
            outBox.setAggregateId(queryTravelInsuranceBICRequest.getRequestId());
            outBox.setPayload(payload);
            outBox.setType("getTravelInsuranceBIC");
            outboxRepository.save(outBox);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }


    @Override
    public String getAllOrder(BaseRequest baseRequest, HttpServletRequest request, HttpServletResponse responseSelvet){
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        String userName = "";
        try {
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

            Pageable page = PageRequest.of(0, 5);
            //find by user name
            List<OrdersServiceEntity> allOrders =
                    orderRepository.findByUserName(userName, page);
//            List<OrdersServiceEntity> all =
//                    orderRepository.findByUserName(userName, page.next());
            if(!allOrders.isEmpty()) {
                for (int i = 0; i < allOrders.size(); i++) {
                    String orderString = gson.toJson(allOrders.get(i));
                    String key = "Order:" + String.valueOf(i);
                    jsonObject.put(key, orderString);
                    System.out.println(orderString);
                }
            }else{
                return "User has no orders";
            }
        }catch (Exception ex){
            return ex.getMessage();
        }
        return jsonObject.toString();
    }

}
