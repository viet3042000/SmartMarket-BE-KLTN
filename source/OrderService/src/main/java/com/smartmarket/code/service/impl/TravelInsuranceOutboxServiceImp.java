package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.*;
import com.smartmarket.code.dao.OrderProductRepository;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.model.OrderProduct;
import com.smartmarket.code.model.Orders;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.request.entity.ItemDetailCancelRequest;
import com.smartmarket.code.request.entity.ItemDetailCreateRequest;
import com.smartmarket.code.service.TravelInsuranceOutboxService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class TravelInsuranceOutboxServiceImp implements TravelInsuranceOutboxService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    @Autowired
    OutboxRepository outboxRepository;


    public void processMessageFromTravelOutbox(JSONObject jsonPayload, String aggregateId, String type) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String stringFinishedAt = formatter.format(date);
        Date finishedAt = formatter.parse(stringFinishedAt);

        if (type.equals("createOrder")) {
            //j = response + orderReference + requestPayload
            String requestPayload = jsonPayload.getString("requestPayload");
            JSONObject itemDetailJsonObject = new JSONObject(requestPayload);
            JSONObject detail = itemDetailJsonObject.getJSONObject("detail");
            JSONArray orderItems = detail.getJSONArray("orderItems");
            ObjectMapper mapper = new ObjectMapper();
            List<ItemDetailCreateRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCreateRequest[].class));

            String requestId = itemDetailJsonObject.getString("requestId");
            int createItemIndex = itemDetailJsonObject.getInt("createItemIndex");
            String orderReference = jsonPayload.getString("OrderReference");
            String status = jsonPayload.getString("status");
            Long startTime = itemDetailJsonObject.getLong("startTime");

            Orders order = orderRepository.findByOrderId(aggregateId).orElse(null);
            SagaState sagaState = sagaStateRepository.findById(requestId).orElse(null);
            if (order !=null && sagaState !=null) {
                if(order.getState().equals("Pending")) {
                    String productName = items.get(createItemIndex).getProductName();
                    String currentStep = "CreateOrder-Index-"+Integer.toString(createItemIndex);
                    sagaState.setCurrentStep(currentStep);

                    ItemDetailCreateRequest itemDetailCreateSuccess = items.get(createItemIndex);
                    //insert to outbox
                    if (status.equals("success")) {
                        itemDetailCreateSuccess.getProductDetailCreateRequest().getOrders().setOrderReference(orderReference);
                        items.set(createItemIndex,itemDetailCreateSuccess);

                        String newItems = new Gson().toJson(items);
                        JSONArray newOrderItems = new JSONArray(newItems);
                        detail.put("orderItems",newOrderItems);
                        itemDetailJsonObject.put("detail",detail);

                        if (createItemIndex==0) {
                            JSONObject stepState = new JSONObject();
                            stepState.put(currentStep, SagaStepState.SUCCEEDED);
                            sagaState.setStepState(stepState.toString());
                        }else {
                            JSONObject stepState = new JSONObject(sagaState.getStepState());
                            stepState.put(currentStep, SagaStepState.SUCCEEDED);
                            sagaState.setStepState(stepState.toString());
                        }

                        OrderProduct orderProduct = new OrderProduct();
                        orderProduct.setOrderId(aggregateId);
                        orderProduct.setIndex(createItemIndex);
                        orderProduct.setState(OrderProductState.SUCCEEDED);
                        orderProduct.setItemPrice(itemDetailCreateSuccess.getItemPrice().toString());
                        orderProduct.setProductId(orderReference);
                        orderProduct.setProductName(productName);
                        orderProduct.setFinishedLogtimestamp(finishedAt);
                        orderProductRepository.save(orderProduct);

                        //create outbox
                        if(createItemIndex < items.size()-1) {
                            ItemDetailCreateRequest itemDetailCreateRequest = items.get(createItemIndex + 1);

                            itemDetailJsonObject.put("startTime", startTime);
                            itemDetailJsonObject.put("createItemIndex", createItemIndex + 1);
//                            itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
//                            itemDetailJsonObject.put("hostName", hostName);
//                            itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));

                            Outbox outBox = new Outbox();
                            outBox.setCreatedLogtimestamp(finishedAt);
                            if (itemDetailCreateRequest.getProductProvider().equals("BIC") && itemDetailCreateRequest.getProductType().equals("bảo hiểm du lịch")) {
                                outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
                            }
                            outBox.setAggregateId(aggregateId);
                            outBox.setType(OutboxType.CREATE_ORDER);
                            outBox.setPayload(itemDetailJsonObject.toString());
                            outboxRepository.save(outBox);
                        } else if(createItemIndex == items.size()-1){
                            order.setState(OrderState.SUCCEEDED);
                            order.setFinishedLogtimestamp(finishedAt);
                            sagaState.setCurrentStep(null);
                            sagaState.setStatus(SagaStatus.SUCCEEDED);
                            sagaState.setFinishedLogtimestamp(finishedAt);
                        }
                    } else {
                        OrderProduct orderProduct = new OrderProduct();
                        orderProduct.setOrderId(aggregateId);
                        orderProduct.setIndex(createItemIndex);
                        orderProduct.setState(OrderProductState.ABORTED);
                        orderProduct.setItemPrice(itemDetailCreateSuccess.getItemPrice().toString());
                        orderProduct.setProductId(orderReference);
                        orderProduct.setProductName(productName);
                        orderProduct.setFinishedLogtimestamp(finishedAt);
                        orderProductRepository.save(orderProduct);

                        if(createItemIndex==0){
                            order.setState(OrderState.ABORTED);
                            order.setFinishedLogtimestamp(finishedAt);

                            JSONObject stepState = new JSONObject();
                            stepState.put(currentStep, SagaStepState.ABORTED);
                            sagaState.setStepState(stepState.toString());
                            sagaState.setStatus(SagaStatus.ABORTED);
                            sagaState.setCurrentStep(null);
                            sagaState.setFinishedLogtimestamp(finishedAt);

                        }else { //createItemIndex>=1
                            order.setState(OrderState.ABORTING);

                            JSONObject stepState = new JSONObject(sagaState.getStepState());
                            stepState.put(currentStep, SagaStepState.ABORTING);
                            sagaState.setStepState(stepState.toString());
                            sagaState.setStatus(SagaStatus.ABORTING);

                            ItemDetailCreateRequest itemDetailCreateRequest = items.get(createItemIndex-1);
                            orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.ABORTING,createItemIndex-1);

//                            JSONObject itemDetailJsonObject = new JSONObject(createOrderRequest);
                            itemDetailJsonObject.put("startTime", startTime);
                            itemDetailJsonObject.put("abortItemIndex", createItemIndex-1);
//                            itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
//                            itemDetailJsonObject.put("orderReference",orderReference);
//                                itemDetailJsonObject.put("hostName", hostName);
//                                itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));

                            Outbox outBox = new Outbox();
                            outBox.setCreatedLogtimestamp(finishedAt);
                            if (itemDetailCreateRequest.getProductProvider().equals("BIC") && itemDetailCreateRequest.getProductType().equals("bảo hiểm du lịch")) {
                                outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
                            }
                            outBox.setAggregateId(aggregateId);
                            outBox.setType(OutboxType.ABORT_ORDER);
                            outBox.setPayload(itemDetailJsonObject.toString());
                            outboxRepository.save(outBox);
                        }
                    }
                    orderRepository.save(order);
                    sagaStateRepository.save(sagaState);
                }
            }
        }

        if (type.equals("cancelOrder")) {
            String requestPayload = jsonPayload.getString("requestPayload");
            JSONObject itemDetailJsonObject = new JSONObject(requestPayload);
            JSONObject detail = itemDetailJsonObject.getJSONObject("detail");
            JSONArray orderItems = detail.getJSONArray("orderItems");
            ObjectMapper mapper = new ObjectMapper();
            List<ItemDetailCancelRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCancelRequest[].class));

            String requestId = itemDetailJsonObject.getString("requestId");
            int cancelItemIndex = itemDetailJsonObject.getInt("cancelItemIndex");
            String status = jsonPayload.getString("status");
            Long startTime = itemDetailJsonObject.getLong("startTime");

            Orders order = orderRepository.findByOrderId(aggregateId).orElse(null);
            SagaState sagaState = sagaStateRepository.findById(requestId).orElse(null);
            if (order !=null && sagaState != null) {
                if(order.getState().equals("Canceling")) {

                    String currentStep = "cancelOrder-Index-"+Integer.toString(cancelItemIndex);
                    sagaState.setCurrentStep(currentStep);

                    if (status.equals("success")) {
                         orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.CANCELED,cancelItemIndex);

                        if(cancelItemIndex ==0) {
                            JSONObject stepState = new JSONObject();
                            stepState.put(currentStep, SagaStepState.SUCCEEDED);
                            sagaState.setStepState(stepState.toString());
                        }else {
                            JSONObject stepState = new JSONObject(sagaState.getStepState());
                            stepState.put(currentStep, SagaStepState.SUCCEEDED);
                            sagaState.setStepState(stepState.toString());
                        }
                    } else {
                        //if cancel failure
                        orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.ERROR,cancelItemIndex);
                        if(cancelItemIndex ==0) {
                            JSONObject stepState = new JSONObject();
                            stepState.put(currentStep, SagaStepState.ERROR);
                            sagaState.setStepState(stepState.toString());
                        }else{
                            JSONObject stepState = new JSONObject(sagaState.getStepState());
                            stepState.put(currentStep, SagaStepState.ERROR);
                            sagaState.setStepState(stepState.toString());
                        }
                    }

                    //create outbox
                    if(cancelItemIndex < items.size()-1) {
                        ItemDetailCancelRequest itemDetailCancelRequest = items.get(cancelItemIndex+1);
                        Outbox outBox = new Outbox();
                        outBox.setCreatedLogtimestamp(finishedAt);
                        if(itemDetailCancelRequest.getProductProvider().equals("BIC") && itemDetailCancelRequest.getProductType().equals("bảo hiểm du lịch")){
                            outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
                        }
                        outBox.setAggregateId(aggregateId);
                        outBox.setType(OutboxType.CANCEL_ORDER);

//                        JSONObject itemDetailJsonObject = new JSONObject(cancelOrderRequest);
                        itemDetailJsonObject.put("startTime", startTime);
                        itemDetailJsonObject.put("cancelItemIndex", cancelItemIndex+1);
//                        itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
//                            itemDetailJsonObject.put("hostName", hostName);
//                            itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));
                        outBox.setPayload(itemDetailJsonObject.toString());
                        outboxRepository.save(outBox);
                    } else {//cancelItemIndex == orderItems.size()-1

                        int countSucceeded = orderProductRepository.countByState(aggregateId,"Canceled");
                        if(countSucceeded ==items.size()){
                            order.setState(OrderState.CANCELED);
                            sagaState.setStatus(SagaStatus.SUCCEEDED);
                        }else{
                            order.setState(OrderState.ERROR);
                            sagaState.setStatus(SagaStatus.ERROR);
                        }
                        sagaState.setCurrentStep(null);
                        sagaState.setFinishedLogtimestamp(finishedAt);
                    }
                    orderRepository.save(order);
                    sagaStateRepository.save(sagaState);
                }
            }
        }


        if (type.equals("abortOrder")) {
            String requestPayload = jsonPayload.getString("requestPayload");
            JSONObject itemDetailJsonObject = new JSONObject(requestPayload);
            JSONObject detail = itemDetailJsonObject.getJSONObject("detail");
            JSONArray orderItems = detail.getJSONArray("orderItems");
            ObjectMapper mapper = new ObjectMapper();
            List<ItemDetailCreateRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCreateRequest[].class));

            String requestId = itemDetailJsonObject.getString("requestId");
            int abortItemIndex = itemDetailJsonObject.getInt("abortItemIndex");
            String status = jsonPayload.getString("status");
            Long startTime = itemDetailJsonObject.getLong("startTime");
//            String orderReference = jsonPayload.getString("OrderReference");

            Orders order = orderRepository.findByOrderId(aggregateId).orElse(null);
            SagaState sagaState = sagaStateRepository.findById(requestId).orElse(null);
            if (order!=null && sagaState != null) {
                if(order.getState().equals("Aborting")) {
                    String currentStep = "abortOrder-Index-"+Integer.toString(abortItemIndex);
                    sagaState.setCurrentStep(currentStep);

                    if (status.equals("success")) {
                        JSONObject stepState = new JSONObject(sagaState.getStepState());
                        stepState.put(currentStep, SagaStepState.ABORTED);
                        sagaState.setStepState(stepState.toString());

                        orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.ABORTED,abortItemIndex);

                        //create outbox to aborting (for product succeeded before this product failure)
                        if(abortItemIndex >=1) {
                            ItemDetailCreateRequest itemDetailCreateRequest = items.get(abortItemIndex-1);
                            orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.ABORTING,abortItemIndex-1);

//                            JSONObject itemDetailJsonObject = new JSONObject(createOrderRequest);
                            itemDetailJsonObject.put("startTime", startTime);
                            itemDetailJsonObject.put("abortItemIndex", abortItemIndex-1);
//                            itemDetailJsonObject.put("clientId", JwtUtils.getClientId());
//                            itemDetailJsonObject.put("orderReference",orderReference);
//                                itemDetailJsonObject.put("hostName", hostName);
//                                itemDetailJsonObject.put("clientIp", Utils.getClientIp(request));

                            Outbox outBox = new Outbox();
                            outBox.setCreatedLogtimestamp(finishedAt);
                            if (itemDetailCreateRequest.getProductProvider().equals("BIC") && itemDetailCreateRequest.getProductType().equals("bảo hiểm du lịch")) {
                                outBox.setAggregateType(AggregateType.TRAVEL_INSURANCE);//target services
                            }
                            outBox.setAggregateId(aggregateId);
                            outBox.setType(OutboxType.ABORT_ORDER);
                            outBox.setPayload(itemDetailJsonObject.toString());
                            outboxRepository.save(outBox);
                        }else{//abortItemIndex ==0
                            order.setState(OrderState.ABORTED);
                            sagaState.setCurrentStep(null);
                            sagaState.setFinishedLogtimestamp(finishedAt);
                            sagaState.setStatus(SagaStatus.ABORTED);
                        }
                    } else {
                        order.setState(OrderState.ERROR);

                        JSONObject stepState = new JSONObject(sagaState.getStepState());
                        stepState.put(currentStep, SagaStepState.ERROR);
                        sagaState.setStepState(stepState.toString());
                        sagaState.setFinishedLogtimestamp(finishedAt);
                        sagaState.setCurrentStep(null);
                        sagaState.setStatus(SagaStatus.ERROR);

                        orderProductRepository.updateOrderProduct(aggregateId,OrderProductState.ERROR,abortItemIndex);
                    }
                    orderRepository.save(order);
                    sagaStateRepository.save(sagaState);
                }
            }
        }
    }
}
