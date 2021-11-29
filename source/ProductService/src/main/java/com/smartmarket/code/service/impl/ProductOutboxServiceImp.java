package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.OutboxType;
import com.smartmarket.code.constants.SagaStateStatus;
import com.smartmarket.code.constants.SagaStateStepState;
import com.smartmarket.code.dao.ProductApprovalFlowRepository;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductApprovalFlow;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.request.entity.StateApproval;
import com.smartmarket.code.request.entity.StepDecision;
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


    public void processMessageFromOrderOutbox(String op,String aggregateId,String type,
                                              String payload) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String stringCreateAt = formatter.format(date);
        Date createAt = formatter.parse(stringCreateAt);

        Gson gson = new Gson();
        StepDecision stepDecision = gson.fromJson(payload, StepDecision.class);
        if (type.equals(OutboxType.APPROVE_CREATED_PRODUCT)) {
            try {
                Product product = productRepository.findByProductId(stepDecision.getProductId()).orElse(null);
                SagaState sagaState = sagaStateRepository.findById(stepDecision.getRequestId()).orElse(null);
                ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findProductApprovalFlow(stepDecision.getFlowName(),stepDecision.getProductId()).orElse(null);

                StateApproval stateApproval = new StateApproval();
                if ("Approve".equals(stepDecision.getDecision())) {
                    if(stepDecision.getCurrentStepNumber() == productApprovalFlow.getNumberOfSteps()){
                        stateApproval.setStateName("Completed");
                    }else {
                        ObjectMapper mapper = new ObjectMapper();
                        List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));

                        for(int i = 0 ; i< stepFlows.size()-1; i++){
                            StepFlow stepFlow = stepFlows.get(i);
                            if(stepDecision.getRoleName().equals(stepFlow.getRoleName())){
                                stateApproval.setStateName("PendingApprove");
                                stateApproval.setRoleName(stepFlows.get(i+1).getRoleName());
                            }
                        }
                    }
                    product.setState(new JSONObject(stateApproval).toString());

                } else {//DisApprove
                    stateApproval.setStateName("DisApproved");
                    stateApproval.setRoleName(stepDecision.getRoleName());
                    product.setState(new JSONObject(stateApproval).toString());
                }
                product.setState(new JSONObject(stateApproval).toString());
                productRepository.save(product);

                //temporary 1 saga save 1 step
//                if(stepDecision.getCurrentStepNumber() == 1){
//                    JSONObject stepState = new JSONObject();
//                    stepState.put(sagaState.getCurrentStep(), SagaStateStepState.SUCCEEDED);
//                    sagaState.setStepState(stepState.toString());
//                }else {
//                    JSONObject stepState = new JSONObject(sagaState.getStepState());
//                    stepState.put(sagaState.getCurrentStep(), SagaStateStepState.SUCCEEDED);
//                    sagaState.setStepState(stepState.toString());
//                }
                JSONObject stepState = new JSONObject();
//                String currentStep = "Index-"+Integer.toString(abortItemIndex)+"-"+AggregateType.TRAVEL_INSURANCE;//orderService
                String currentStep = "CurrentStep-"+Integer.toString(stepDecision.getCurrentStepNumber())+"-"+"Product";
                stepState.put(currentStep, SagaStateStepState.SUCCEEDED);
                sagaState.setStepState(stepState.toString());

                //temporary 1 saga save 1 step
//                if(stepDecision.getCurrentStepNumber() == productApprovalFlow.getNumberOfSteps()){
//                    sagaState.setCurrentStep("");
//                    sagaState.setStatus(SagaStateStatus.SUCCEEDED);
//                    sagaState.setFinishedLogtimestamp(createAt);
//                }else {
//                    //temporary 1 saga save 1 step
//                    sagaState.setCurrentStep(Integer.toString(stepDecision.getCurrentStepNumber()));
//                }

                sagaState.setCurrentStep("");
                sagaState.setStatus(SagaStateStatus.SUCCEEDED);
                sagaState.setFinishedLogtimestamp(createAt);
                sagaStateRepository.save(sagaState);
            }catch (Exception ex) {
                SagaState sagaState = sagaStateRepository.findById(stepDecision.getRequestId()).orElse(null);

                //temporary 1 saga save 1 step
//                if(stepDecision.getCurrentStepNumber() == 1){
//                    JSONObject stepState = new JSONObject();
//                    stepState.put(sagaState.getCurrentStep(), SagaStateStepState.ERROR);
//                    sagaState.setStepState(stepState.toString());
//                }else {
//                    JSONObject stepState = new JSONObject(sagaState.getStepState());
//                    stepState.put(sagaState.getCurrentStep(), SagaStateStepState.ERROR);
//                    sagaState.setStepState(stepState.toString());
//                }
                JSONObject stepState = new JSONObject();
                stepState.put(sagaState.getCurrentStep(), SagaStateStepState.SUCCEEDED);
                sagaState.setStepState(stepState.toString());

                sagaState.setCurrentStep("");
                sagaState.setStatus(SagaStateStatus.ERROR);
                sagaState.setFinishedLogtimestamp(createAt);
                sagaStateRepository.save(sagaState);

                throw ex;
            }
        }
    }
}
