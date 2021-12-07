package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.*;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.ProductApprovalFlowRepository;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductApprovalFlow;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.request.entity.CurrentStepSaga;
import com.smartmarket.code.request.entity.StepDetail;
import com.smartmarket.code.request.entity.StepFlow;
import com.smartmarket.code.service.ProductOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ProductOutboxServiceImp implements ProductOutboxService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    @Autowired
    ProductApprovalFlowRepository productApprovalFlowRepository;

    @Autowired
    OutboxRepository outboxRepository;


    public void processMessageFromOrderOutbox(String op,String aggregateId,String type,
                                              String payload) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);

        Gson gson = new Gson();
        StepDetail stepDetail = gson.fromJson(payload, StepDetail.class);
        if (type.equals(OutboxType.APPROVE_CREATED_PRODUCT)) {
            try {
                Product product = productRepository.findByProductId(Long.parseLong(aggregateId)).orElse(null);
                SagaState sagaState = sagaStateRepository.findById(stepDetail.getRequestId()).orElse(null);
                CurrentStepSaga currentStepSaga = new CurrentStepSaga();
                currentStepSaga.setCurrentStep(stepDetail.getStepNumber());
                currentStepSaga.setSagaType(SagaType.APPROVE_CREATED_PRODUCT);
//                String currentStep = SagaType.APPROVE_CREATED_PRODUCT+"-CurrentStep-"+Integer.toString(stepDetail.getStepNumber());
                sagaState.setCurrentStep(gson.toJson(currentStepSaga));

                ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findProductApprovalFlow(stepDetail.getFlowName(), Long.parseLong(aggregateId)).orElse(null);
                ObjectMapper mapper = new ObjectMapper();
                List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));

                if ("DisApprove".equals(stepDetail.getDecision())) {
                    product.setState(ProductState.DISAPPROVED);
                    product.setCurrentSagaId(null);
                    sagaState.setStatus(SagaStatus.SUCCEEDED);
                    sagaState.setFinishedLogtimestamp(createAt);
                }

                if(stepDetail.getStepNumber() == stepFlows.size()){
                    if ("Approve".equals(stepDetail.getDecision())) {
                        product.setState(ProductState.APPROVED);
                        product.setCurrentSagaId(null);
                    }
                }
                productRepository.save(product);

                if(stepDetail.getStepNumber() == 1){
                    JSONObject stepState = new JSONObject();
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.SUCCEEDED);
                    sagaState.setStepState(stepState.toString());
                }else {
                    JSONObject stepState = new JSONObject(sagaState.getStepState());
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.SUCCEEDED);
                    sagaState.setStepState(stepState.toString());
                }

                if(stepDetail.getStepNumber() == stepFlows.size()){
                    sagaState.setCurrentStep(null);
                    sagaState.setStatus(SagaStatus.SUCCEEDED);
                    sagaState.setFinishedLogtimestamp(createAt);
                }
                sagaStateRepository.save(sagaState);

                if ("Approve".equals(stepDetail.getDecision()) && stepDetail.getStepNumber() < stepFlows.size()) {
                    //create outbox
                    Outbox outBox = new Outbox();
                    outBox.setAggregateId(aggregateId);
                    outBox.setCreatedLogtimestamp(new Date());
                    outBox.setAggregateType("Product");
                    outBox.setType(OutboxType.WAITING_APPROVE);

                    // index i --> stepNumber = i+1
                    stepDetail.setStepName(stepFlows.get(stepDetail.getStepNumber()).getStepName());
                    stepDetail.setRoleName(stepFlows.get(stepDetail.getStepNumber()).getRoleName());
                    stepDetail.setStepNumber(stepDetail.getStepNumber()+1);
                    outBox.setPayload(new JSONObject(stepDetail).toString());
                    outboxRepository.save(outBox);
                }

            }catch (Exception ex) {
                Product product = productRepository.findByProductId(Long.parseLong(aggregateId)).orElse(null);
                product.setCurrentSagaId(null);
                productRepository.save(product);

                SagaState sagaState = sagaStateRepository.findById(stepDetail.getRequestId()).orElse(null);
                sagaState.setCurrentStep(null);

                if(stepDetail.getStepNumber() == 1){
                    JSONObject stepState = new JSONObject();
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.ERROR);
                    sagaState.setStepState(stepState.toString());
                }else {
                    JSONObject stepState = new JSONObject(sagaState.getStepState());
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.ERROR);
                    sagaState.setStepState(stepState.toString());
                }
                sagaState.setStatus(SagaStatus.ERROR);
                sagaState.setFinishedLogtimestamp(createAt);
                sagaStateRepository.save(sagaState);

                throw ex;
            }
        }

        if (type.equals(OutboxType.WAITING_APPROVE)) {
            try {
                SagaState sagaState = sagaStateRepository.findById(stepDetail.getRequestId()).orElse(null);
                CurrentStepSaga currentStepSaga = new CurrentStepSaga();
                currentStepSaga.setCurrentStep(stepDetail.getStepNumber());
                currentStepSaga.setSagaType(SagaType.APPROVE_CREATED_PRODUCT);
//                String currentStep = SagaType.APPROVE_CREATED_PRODUCT+"-CurrentStep-"+Integer.toString(stepDetail.getStepNumber());
                sagaState.setCurrentStep(gson.toJson(currentStepSaga));

                if(stepDetail.getStepNumber() == 1){
                    JSONObject stepState = new JSONObject();
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.STARTED);
                    sagaState.setStepState(stepState.toString());
                }else {
                    JSONObject stepState = new JSONObject(sagaState.getStepState());
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.STARTED);
                    sagaState.setStepState(stepState.toString());
                }
                sagaStateRepository.save(sagaState);

            } catch (Exception ex) {
                Product product = productRepository.findByProductId(Long.parseLong(aggregateId)).orElse(null);
                product.setCurrentSagaId(null);
                productRepository.save(product);

                SagaState sagaState = sagaStateRepository.findById(stepDetail.getRequestId()).orElse(null);
                sagaState.setCurrentStep(null);

                if(stepDetail.getStepNumber() == 1){
                    JSONObject stepState = new JSONObject();
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.ERROR);
                    sagaState.setStepState(stepState.toString());
                }else {
                    JSONObject stepState = new JSONObject(sagaState.getStepState());
                    stepState.put(sagaState.getCurrentStep(), SagaStepState.ERROR);
                    sagaState.setStepState(stepState.toString());
                }
                sagaStateRepository.save(sagaState);

                throw ex;
            }
        }
    }
}
