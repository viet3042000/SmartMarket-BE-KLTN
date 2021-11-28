package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateApprovalFlowRequest;
import com.smartmarket.code.request.UpdateApprovalFlowRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface ApprovalFlowService {

    //Admin
    public ResponseEntity<?> createApprovalFlow(@Valid @RequestBody BaseDetail<CreateApprovalFlowRequest> createApprovalFlowRequestBaseDetail ,
                                                HttpServletRequest request,
                                                HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException ;

    //AdminProvider
    public ResponseEntity<?> updateApprovalFlow(@Valid @RequestBody BaseDetail<UpdateApprovalFlowRequest> updateApprovalFlowRequestBaseDetail ,
                                                HttpServletRequest request,
                                                HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException ;
}
