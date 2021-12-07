package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ApprovalFlowRepository;
import com.smartmarket.code.model.ApprovalFlow;
import com.smartmarket.code.service.ApprovalFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ApprovalFlowServiceImp implements ApprovalFlowService {

    @Autowired
    ApprovalFlowRepository approvalFlowRepository;


    public void createApprovalFlow(Map<String, Object> keyPairs) throws ParseException {

        ApprovalFlow approvalFlow = new ApprovalFlow();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                approvalFlow.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("flow_name")) {
                approvalFlow.setFlowName((String) keyPairs.get(k));
            }
            if (k.equals("step_detail")) {
                approvalFlow.setStepDetail((String) keyPairs.get(k));
            }
            if (k.equals("product_provider_name")) {
                approvalFlow.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                approvalFlow.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        approvalFlowRepository.save(approvalFlow);
    }

    public void updateApprovalFlow(Map<String, Object> keyPairs) throws ParseException{

        ApprovalFlow approvalFlow = new ApprovalFlow();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                approvalFlow.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("flow_name")) {
                approvalFlow.setFlowName((String) keyPairs.get(k));
            }
            if (k.equals("step_detail")) {
                approvalFlow.setStepDetail((String) keyPairs.get(k));
            }
            if (k.equals("product_provider_name")) {
                approvalFlow.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                approvalFlow.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        approvalFlowRepository.save(approvalFlow);
    }

    public void deleteApprovalFlow(Map<String, Object> keyPairs){
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                Long id = ((Number)keyPairs.get(k)).longValue();
                approvalFlowRepository.deleteApprovalFlowKafka(id);
            }
        }
    }

    public void truncateApprovalFlow(){
        approvalFlowRepository.truncateApprovalFlowKafka();
    }
}
