package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateApprovalFlowRequest;
import com.smartmarket.code.request.UpdateApprovalFlowRequest;
import com.smartmarket.code.service.ApprovalFlowService;
import com.smartmarket.code.service.AuthorizationService;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.util.ArrayList;

@RestController
@RequestMapping("/approval-flow/approval-flow-service/v1/")
public class ApprovalFlowController {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    ApprovalFlowService approvalFlowService;

    //Admin
    @PostMapping(value = "/create-approval-flow", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createApprovalFlow(@Valid @RequestBody BaseDetail<CreateApprovalFlowRequest> createApprovalFlowRequestBaseDetail ,
                                                   HttpServletRequest request,
                                                   HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if (roles != null) {
                if (roles.contains("ADMIN")) {
                    return approvalFlowService.createApprovalFlow(createApprovalFlowRequestBaseDetail, request, responseSelvet);
                } else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createApprovalFlowRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createApprovalFlowRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    //AdminProvider
    @PostMapping(value = "/update-approval-flow", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateApprovalFlow(@Valid @RequestBody BaseDetail<UpdateApprovalFlowRequest> updateApprovalFlowRequestBaseDetail ,
                                                HttpServletRequest request,
                                                HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if (roles != null) {
                if (roles.contains("PROVIDER_ADMIN")) {
                    return approvalFlowService.updateApprovalFlow(updateApprovalFlowRequestBaseDetail, request, responseSelvet);
                } else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, updateApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, updateApprovalFlowRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateApprovalFlowRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateApprovalFlowRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateApprovalFlowRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }
}
