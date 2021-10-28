package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.RoleService;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.Utils;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Date;


@RestController
@RequestMapping("/user/user-service/v1/")
public class RoleController {

    @Autowired
    RoleService roleService;

    @Autowired
    AuthorizationService authorizationService;

    @PostMapping(value = "/create-role", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createRole(@Valid @RequestBody BaseDetail<CreateRoleRequest> createRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return roleService.createRole(createRoleRequestBaseDetail,request,responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createRoleRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException){
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createRoleRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
            }else {
                throw ex ;
            }
        }
    }

    @PostMapping(value = "/update-role", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateRole(@Valid @RequestBody BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return roleService.updateRole(updateRoleRequestBaseDetail,request,responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, updateRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, updateRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateRoleRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException){
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateRoleRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
            }else {
                throw ex ;
            }
        }
    }


    @PostMapping(value = "/delete-role", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteRole(@Valid @RequestBody BaseDetail<DeleteRoleRequest> deleteRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return roleService.deleteRole(deleteRoleRequestBaseDetail,request,responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, deleteRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, deleteRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(deleteRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(deleteRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(deleteRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), deleteRoleRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException){
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), deleteRoleRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
            }else {
                throw ex ;
            }
        }
    }


    @PostMapping(value = "/get-list-role", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListRole(@Valid @RequestBody BaseDetail<QueryRoleRequest> getListRoleRequestBaseDetail ,
                              HttpServletRequest request,
                              HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return roleService.getListRole(getListRoleRequestBaseDetail,request,responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, getListRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, getListRoleRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(getListRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(getListRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(getListRoleRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), getListRoleRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), getListRoleRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

}
