package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.AggregateType;
import com.smartmarket.code.constants.OrderEntityState;
import com.smartmarket.code.constants.SagaStateStatus;
import com.smartmarket.code.constants.SagaStateStepState;
import com.smartmarket.code.dao.OrderProductRepository;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.model.OrderProduct;
import com.smartmarket.code.model.Orders;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.service.TravelInsuranceOutboxService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class TravelInsuranceOutboxServiceImp implements TravelInsuranceOutboxService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    public void processMessageFromTravelOutbox(JSONObject j, String requestId, String status,
                                               String aggregateId, String type) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String stringFinishedAt = formatter.format(date);
        Date finishedAt = formatter.parse(stringFinishedAt);

        if (type.equals("createTravelInsuranceBIC")) {
            String orderReference = j.getString("OrderReference");

            Optional<Orders> orders = orderRepository.findByOrderId(aggregateId);
            Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);
            OrderProduct orderProduct = new OrderProduct();
            if (orders.isPresent() && sagaState.isPresent()) {
                Orders order = orders.get();
                order.setFinishedLogtimestamp(finishedAt);

                SagaState st = sagaState.get();
                st.setType(type);
                st.setFinishedLogtimestamp(finishedAt);
                st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
                JSONObject s = new JSONObject();
                //insert to outbox
                if (status.equals("success")) {
                    order.setState(OrderEntityState.SUCCEEDED);

                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
                    st.setStepState(s.toString());
                    st.setStatus(SagaStateStatus.SUCCEEDED);

                    orderProduct.setOrderId(aggregateId);
                    orderProduct.setProductId(orderReference);
                    orderProduct.setProductName("TravelInsuranceBIC");
                    orderProduct.setState("Succeeded");
                    orderProduct.setFinishedLogtimestamp(finishedAt);
                } else {
                    order.setState(OrderEntityState.ABORTED);

                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
                    st.setStepState(s.toString());
                    st.setStatus(SagaStateStatus.ABORTED);

                    orderProduct.setOrderId(aggregateId);
                    orderProduct.setProductId(orderReference);
                    orderProduct.setProductName("TravelInsuranceBIC");
                    orderProduct.setState("Aborted");
                    orderProduct.setFinishedLogtimestamp(finishedAt);
                }
                orderProductRepository.save(orderProduct);
                orderRepository.save(order);
                sagaStateRepository.save(st);
            }
        }

        if (type.equals("updateTravelInsuranceBIC")) {
            Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);

            if (sagaState.isPresent()) {
                SagaState st = sagaState.get();
                st.setFinishedLogtimestamp(finishedAt);
                st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
                st.setType(type);
                JSONObject s = new JSONObject();
                //insert to outbox
                if (status.equals("success")) {
                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
                    st.setStepState(s.toString());
                    st.setStatus(SagaStateStatus.SUCCEEDED);
                } else {
                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
                    st.setStepState(s.toString());
                    st.setStatus(SagaStateStatus.ABORTED);
                }
                sagaStateRepository.save(st);
            }
        }

//        if (type.equals("getTravelInsuranceBIC")) {
//            Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);
//
//            if (sagaState.isPresent()) {
//                SagaState st = sagaState.get();
//                st.setFinishedLogtimestamp(finishedAt);
//                st.setType(type);
//                st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
//                JSONObject s = new JSONObject();
//                //insert to outbox
//                if (status.equals("success")) {
//                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
//                    st.setStepState(s.toString());
//                    st.setStatus(SagaStateStatus.SUCCEEDED);
//                } else {
//                    s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
//                    st.setStepState(s.toString());
//                    st.setStatus(SagaStateStatus.ABORTED);
//                }
//                sagaStateRepository.save(st);
//            }
//        }

    }
}
