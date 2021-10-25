package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.RoleService;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.util.Date;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    LogServiceImpl logService;


    public ResponseEntity<?> createRole(@Valid @RequestBody BaseDetail<CreateRoleRequest> createRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        BaseResponse response = new BaseResponse();
        Role roleCreate =  new Role();
        roleCreate.setRoleName(createRoleRequestBaseDetail.getDetail().getRole().getRoleName());
        roleCreate.setCreatedLogtimestamp(new Date());
        roleCreate.setDesc(createRoleRequestBaseDetail.getDetail().getRole().getDesc());
        roleCreate.setEnabled(createRoleRequestBaseDetail.getDetail().getRole().getEnabled());
        roleCreate.setEnabled(createRoleRequestBaseDetail.getDetail().getRole().getEnabled());
        roleRepository.save(roleCreate);

        //set response data to client
        response.setDetail(roleCreate);
        response.setResponseId(createRoleRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> updateRole(@Valid @RequestBody BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        BaseResponse response = new BaseResponse();

        Role roleUpdate = this.update(updateRoleRequestBaseDetail) ;
        //set response data to client
        response.setDetail(roleUpdate);
        response.setResponseId(updateRoleRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteRole(@Valid @RequestBody BaseDetail<DeleteRoleRequest> deleteRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        BaseResponse response = new BaseResponse();

        Role roleDelete = this.deleteByRoleName(deleteRoleRequestBaseDetail) ;
        //set response data to client
        response.setDetail(roleDelete);
        response.setResponseId(deleteRoleRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> getListRole(@Valid @RequestBody BaseDetail<QueryRoleRequest> getListRoleRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {

        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        Page<Role> pageResult = null;
        int page =  getListRoleRequestBaseDetail.getDetail().getPage()  ;
        int size =  getListRoleRequestBaseDetail.getDetail().getSize()   ;
        int totalPage = 0 ;

        Pageable pageable = PageRequest.of(page - 1 , size);

        BaseResponse response = new BaseResponse();
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();
        String responseStatus = Integer.toString(responseSelvet.getStatus());

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        pageResult = this.getList(pageable);

        page =  getListRoleRequestBaseDetail.getDetail().getPage();
        totalPage =(int) Math.ceil((double) pageResult.getTotalElements()/size) ;
        //set response data to client
        response.setDetail(pageResult.getContent());
        response.setPage(page);
        response.setTotalPage(totalPage);
        response.setTotal(pageResult.getTotalElements());

        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        //calculate time duration
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);
        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", getListRoleRequestBaseDetail.getRequestId(), getListRoleRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @Override
//    public Role create(Role object) {
//        object.setEnabled(Constant.STATUS.ACTIVE);
//        return roleRepository.save(object);
//    }

    @Override
    public Role update(BaseDetail<UpdateRoleRequest> updateRoleRequestBaseDetail) throws Exception {
//        Role roleUpdate = roleRepository.findById(updateRoleRequestBaseDetail.getDetail().getRole().getId()).orElse(null);
        Role roleUpdate = roleRepository.findByUserRoleName(updateRoleRequestBaseDetail.getDetail().getRole().getRoleName()).orElse(null);
        if (roleUpdate != null) {
//            roleUpdate.setRoleName(updateRoleRequestBaseDetail.getDetail().getRole().getRoleName());
            roleUpdate.setDesc(updateRoleRequestBaseDetail.getDetail().getRole().getDesc());
            roleUpdate.setEnabled(updateRoleRequestBaseDetail.getDetail().getRole().getEnabled());
            roleRepository.save(roleUpdate);
        }else {
            throw new CustomException("Role_name does not exist", HttpStatus.BAD_REQUEST, updateRoleRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        return roleUpdate;
    }

    @Override
    public Role deleteByRoleName(BaseDetail<DeleteRoleRequest> deleteRoleRequestBaseDetail) throws Exception {
        Role roleDelete = roleRepository.findByUserRoleName(deleteRoleRequestBaseDetail.getDetail().getRoleName()).orElse(null);
        if (roleDelete != null) {
            roleRepository.delete(roleDelete);
        }else {
            throw new CustomException("Role_name does not exist", HttpStatus.BAD_REQUEST, deleteRoleRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        return roleDelete;
    }


    @Override
    public Page<Role> getList(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
}
