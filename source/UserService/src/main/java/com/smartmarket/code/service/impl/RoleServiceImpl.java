package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.*;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.RoleService;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.GetKeyPairUtil;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;


    public ResponseEntity<?> createRole(@Valid @RequestBody BaseDetail<CreateRoleRequest> createRoleRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        BaseResponse response = new BaseResponse();
        //check roleName existed
        String roleName = createRoleRequestBaseDetail.getDetail().getRoleName();
        Role role = roleRepository.findByRoleName(createRoleRequestBaseDetail.getDetail().getRoleName()).orElse(null);
        if(role != null){
            throw new CustomException("roleName existed", HttpStatus.BAD_REQUEST, createRoleRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Role roleCreate =  new Role();
        roleCreate.setRoleName(createRoleRequestBaseDetail.getDetail().getRoleName());
        roleCreate.setCreatedLogtimestamp(new Date());
        roleCreate.setDesc(createRoleRequestBaseDetail.getDetail().getDesc());
        roleCreate.setEnabled(createRoleRequestBaseDetail.getDetail().getEnabled());
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

        //eliminate null value from request body
        JSONObject detail = new JSONObject(updateRoleRequestBaseDetail.getDetail());
        Map<String, Object> keyPairs = new HashMap<>();
        getKeyPairUtil.getKeyPair(detail, keyPairs);

        Role roleUpdate = roleRepository.findByRoleName(updateRoleRequestBaseDetail.getDetail().getRoleName()).orElse(null);
        if (roleUpdate != null) {
            for (String k : keyPairs.keySet()) {
                if (k.equals("type")) {
                    roleUpdate.setEnabled(((Number)keyPairs.get(k)).intValue());
                }
                if (k.equals("desc")) {
                    roleUpdate.setDesc((String) keyPairs.get(k));
                }
            }
            roleRepository.save(roleUpdate);
        }else {
            throw new CustomException("roleName doesn't exist", HttpStatus.BAD_REQUEST, updateRoleRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

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

        Role roleDelete = roleRepository.findByRoleName(deleteRoleRequestBaseDetail.getDetail().getRoleName()).orElse(null);
        if (roleDelete != null) {
            roleRepository.delete(roleDelete);
        }else {
            throw new CustomException("roleName does not exist", HttpStatus.BAD_REQUEST, deleteRoleRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        //delete user+ user_role
        String roleName = deleteRoleRequestBaseDetail.getDetail().getRoleName();
        List<UserRole> listUserRoles = userRoleRepository.findByRoleName(roleName);
        if(!listUserRoles.isEmpty()){
            for (UserRole userRole : listUserRoles) {
                String userName =userRole.getUserName();
                userRepository.deleteByUserName(userName);
                userProfileRepository.deleteByUserName(userName);
                userRoleRepository.delete(userRole);
                userProductProviderRepository.deleteByUserName(userName);
            }
        }

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

        pageResult = roleRepository.findAll(pageable);

        page =  getListRoleRequestBaseDetail.getDetail().getPage();
        int totalPage =(int) Math.ceil((double) pageResult.getTotalElements()/size) ;
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

}
