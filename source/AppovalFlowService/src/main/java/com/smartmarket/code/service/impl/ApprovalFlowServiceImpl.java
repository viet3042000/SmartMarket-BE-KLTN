package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.ApprovalFlowRepository;
import com.smartmarket.code.dao.ProductProviderRepository;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.ApprovalFlow;
import com.smartmarket.code.model.User;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateApprovalFlowRequest;
import com.smartmarket.code.request.UpdateApprovalFlowRequest;
import com.smartmarket.code.request.entity.StepFlow;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.ApprovalFlowService;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Service
public class ApprovalFlowServiceImpl implements ApprovalFlowService {

    @Autowired
    ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    ProductProviderRepository productProviderRepository;

    @Autowired
    UserRepository userRepository;


    //Admin (admin create flow before admin_provider create product)
    public ResponseEntity<?> createApprovalFlow(@Valid @RequestBody BaseDetail<CreateApprovalFlowRequest> createApprovalFlowRequestBaseDetail ,
                                                HttpServletRequest request,
                                                HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        String productProviderName = createApprovalFlowRequestBaseDetail.getDetail().getProductProviderName();
        String flowName = createApprovalFlowRequestBaseDetail.getDetail().getFlowName();

        ApprovalFlow approvalFlow = approvalFlowRepository.findApprovalFlow(productProviderName,flowName).orElse(null);
        if(approvalFlow !=null) {
            throw new CustomException("approvalFlow existed", HttpStatus.BAD_REQUEST, createApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        String providerName = productProviderRepository.getByProductProviderName(productProviderName);
        if(providerName == null){
            throw new CustomException("productProvider does not exist", HttpStatus.BAD_REQUEST, createApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        ApprovalFlow newApprovalFlow = new ApprovalFlow();
        newApprovalFlow.setFlowName(flowName);
        newApprovalFlow.setProductProviderName(productProviderName);
        newApprovalFlow.setCreatedLogtimestamp(new Date());
        approvalFlowRepository.save(newApprovalFlow);

        BaseResponse response = new BaseResponse();
        response.setDetail(newApprovalFlow);
        response.setResponseId(createApprovalFlowRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //AdminProvider (update flow after create product)
    public ResponseEntity<?> updateApprovalFlow(@Valid @RequestBody BaseDetail<UpdateApprovalFlowRequest> updateApprovalFlowRequestBaseDetail ,
                                                HttpServletRequest request,
                                                HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed CustomAuthorizeRequestFilter

        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName doesn't exist in ApprovalFlowService", HttpStatus.BAD_REQUEST, updateApprovalFlowRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String productProviderName = updateApprovalFlowRequestBaseDetail.getDetail().getProductProviderName();
        String flowName = updateApprovalFlowRequestBaseDetail.getDetail().getFlowName();

        ApprovalFlow approvalFlow = approvalFlowRepository.findApprovalFlow(productProviderName,flowName).orElse(null);
        if(approvalFlow ==null){
            throw new CustomException("approvalFlow does not exist", HttpStatus.BAD_REQUEST, updateApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        ArrayList<StepFlow> flowStepDetail =  updateApprovalFlowRequestBaseDetail.getDetail().getFlowStepDetail();
        approvalFlow.setStepDetail(new Gson().toJson(flowStepDetail));
        approvalFlowRepository.save(approvalFlow);

        BaseResponse response = new BaseResponse();
        response.setDetail(approvalFlow);
        response.setResponseId(updateApprovalFlowRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
