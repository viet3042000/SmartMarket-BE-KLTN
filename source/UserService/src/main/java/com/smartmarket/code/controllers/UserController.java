package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.model.UserRole;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.UserCreateResponse;
import com.smartmarket.code.service.RoleService;
import com.smartmarket.code.service.UserProfileService;
import com.smartmarket.code.service.UserRoleService;
import com.smartmarket.code.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


//@RefreshScope
@RestController
@RequestMapping("/user-service/v1/")
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    UserRoleService userRoleService;

    @Transactional
    @PostMapping(value = "/create-user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createUser(@Valid @RequestBody BaseDetail<CreateUserRequest> createUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        BaseResponse response = new BaseResponse();

        try {

            Optional<User> userExist = userService.findByUsername(createUserRequestBaseDetail.getDetail().getUser().getUserName()) ;

            if(userExist.isPresent()){
                throw new CustomException("User is exist", HttpStatus.BAD_REQUEST, createUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

            User userCreate = new User();
            UserProfile userProfileCreate = new UserProfile();


            userCreate.setUserName(createUserRequestBaseDetail.getDetail().getUser().getUserName());
            userCreate.setPassword(createUserRequestBaseDetail.getDetail().getUser().getPassword());

            userProfileCreate.setFullName(createUserRequestBaseDetail.getDetail().getUser().getFullName());
            userProfileCreate.setBirthDate(createUserRequestBaseDetail.getDetail().getUser().getBirthDate());
            userProfileCreate.setIdentifyNumber(createUserRequestBaseDetail.getDetail().getUser().getIdentifyNumber());
            userProfileCreate.setGender(createUserRequestBaseDetail.getDetail().getUser().getGender());
            userProfileCreate.setAddress(createUserRequestBaseDetail.getDetail().getUser().getAddress());
            userProfileCreate.setPhoneNumber(createUserRequestBaseDetail.getDetail().getUser().getPhoneNumber());
            userProfileCreate.setEmail(createUserRequestBaseDetail.getDetail().getUser().getEmail());
            userProfileCreate.setUserName(createUserRequestBaseDetail.getDetail().getUser().getUserName());

            User userCreated = userService.create(userCreate);
            UserProfile userProfileCreated = userProfileService.create(userProfileCreate);

            ArrayList<Long> roles = createUserRequestBaseDetail.getDetail().getRoles();

            if (roles != null && roles.size() > 0) {
                for (int i = 0; i < roles.size(); i++) {

                    UserRole userRoleCreate = new UserRole();
                    userRoleCreate.setUserId(userCreated.getId());
                    userRoleCreate.setRoleId(roles.get(i));
                    userRoleService.create(userRoleCreate);
                }
            }


            //set response data to client
            response.setDetail(userCreate);
            response.setResponseId(createUserRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createUserRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createUserRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/update-user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateUser(@Valid @RequestBody BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        BaseResponse response = new BaseResponse();

        try {

            Optional<User> userExist = userRepository.checkUserExist(updateUserRequestBaseDetail.getDetail().getUser().getUserName(),updateUserRequestBaseDetail.getDetail().getUser().getId()) ;

            if(userExist.isPresent()){
                throw new CustomException("User is exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

            Optional<User> userGetById = userRepository.findByUserId(updateUserRequestBaseDetail.getDetail().getUser().getId()) ;

            if(!userGetById.isPresent()){
                throw new CustomException("userGetById is not exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }

            User userUpdate = new User();
            UserProfile userProfileUpdate = new UserProfile();

            userUpdate.setUserName(updateUserRequestBaseDetail.getDetail().getUser().getUserName());
            userUpdate.setPassword(updateUserRequestBaseDetail.getDetail().getUser().getPassword());
            userUpdate.setId(updateUserRequestBaseDetail.getDetail().getUser().getId());
            userUpdate.setEnabled(updateUserRequestBaseDetail.getDetail().getUser().getEnabled());

            userProfileUpdate.setFullName(updateUserRequestBaseDetail.getDetail().getUser().getFullName());
            userProfileUpdate.setBirthDate(updateUserRequestBaseDetail.getDetail().getUser().getBirthDate());
            userProfileUpdate.setIdentifyNumber(updateUserRequestBaseDetail.getDetail().getUser().getIdentifyNumber());
            userProfileUpdate.setGender(updateUserRequestBaseDetail.getDetail().getUser().getGender());
            userProfileUpdate.setAddress(updateUserRequestBaseDetail.getDetail().getUser().getAddress());
            userProfileUpdate.setPhoneNumber(updateUserRequestBaseDetail.getDetail().getUser().getPhoneNumber());
            userProfileUpdate.setEmail(updateUserRequestBaseDetail.getDetail().getUser().getEmail());
            userProfileUpdate.setUserName(updateUserRequestBaseDetail.getDetail().getUser().getUserName());
            userProfileUpdate.setEnabled(updateUserRequestBaseDetail.getDetail().getUser().getEnabled());

            UserProfile userProfileUpdated = userProfileService.update(userProfileUpdate,userGetById.get().getUserName());
            User userUpdated = userService.update(userUpdate);

            userRoleService.deleteByUserId(updateUserRequestBaseDetail.getDetail().getUser().getId());
            ArrayList<Long> roles = updateUserRequestBaseDetail.getDetail().getRoles();

            if (roles != null && roles.size() > 0) {
                for (int i = 0; i < roles.size(); i++) {

                    UserRole userRoleCreate = new UserRole();
                    userRoleCreate.setUserId(userUpdated.getId());
                    userRoleCreate.setRoleId(roles.get(i));
                    userRoleService.create(userRoleCreate);
                }
            }

            //set response data to client
            response.setDetail(userUpdate);
            response.setResponseId(updateUserRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateUserRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateUserRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/delete-user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteUser(@Valid @RequestBody BaseDetail<DeleteUserRequest> deleteUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //time start
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        BaseResponse response = new BaseResponse();
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();
        String responseStatus = Integer.toString(responseSelvet.getStatus());

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());


        try {

            User userDelete = userService.delete(deleteUserRequestBaseDetail.getDetail().getId());
            UserProfile userProfileDelete = userProfileService.deleteByUserName(userDelete.getUserName());

            //set response data to client
            response.setDetail(userDelete);
            response.setResponseId(deleteUserRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);


            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);
            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog", deleteUserRequestBaseDetail.getRequestId(), deleteUserRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
            logService.createSOALog2(soaObject);

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(deleteUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(deleteUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(deleteUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), deleteUserRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), deleteUserRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/getlist-user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListUser(@Valid @RequestBody BaseDetail<QueryUserRequest> getListUserRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {


        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        Page<User> pageResult = null;
        Page<UserCreateResponse> userCreateResponsePage = null;
        Long total = null ;
        Long page =  getListUserRequestBaseDetail.getDetail().getPage()  ;
        Long size =  getListUserRequestBaseDetail.getDetail().getSize()   ;
        int totalPage = 0 ;

        Pageable pageable =PageRequest.of(page.intValue() - 1 , size.intValue());

        BaseResponse response = new BaseResponse();
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();
        String responseStatus = Integer.toString(responseSelvet.getStatus());

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        try {

            pageResult = userRepository.findAllUser(pageable);

            userCreateResponsePage = pageResult.map(new Function<User, UserCreateResponse>() {
                @Override
                public UserCreateResponse apply(User user) {
                    UserCreateResponse userCreateResponse  = new UserCreateResponse() ;
                    userCreateResponse.setId(user.getId());
                    Optional<UserProfile> userProfile = userProfileService.findByUsername(user.getUserName()) ;

                    if(userProfile.isPresent()){
                        userCreateResponse.setFullName(userProfile.get().getFullName());
                        userCreateResponse.setBirthDate(userProfile.get().getBirthDate());
                        userCreateResponse.setIdentifyNumber(userProfile.get().getIdentifyNumber());
                        userCreateResponse.setGender(userProfile.get().getGender());
                        userCreateResponse.setAddress(userProfile.get().getAddress());
                        userCreateResponse.setPhoneNumber(userProfile.get().getPhoneNumber());
                        userCreateResponse.setEmail(userProfile.get().getEmail());
                        userCreateResponse.setUserName(userProfile.get().getUserName());
                        userCreateResponse.setPassword(user.getPassword());
                        userCreateResponse.setEnabled(userProfile.get().getEnabled());
                    }


                    return userCreateResponse;
                }
            });

            total = pageResult.getTotalElements() ;
            page =  getListUserRequestBaseDetail.getDetail().getPage();
            totalPage =(int) Math.ceil((double) total/size) ;
            //set response data to client
            response.setDetail(userCreateResponsePage.getContent());
            response.setPage(page);
            response.setTotalPage(Long.valueOf(totalPage));
            response.setTotal(total);

            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);
            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog", getListUserRequestBaseDetail.getRequestId(), getListUserRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
            logService.createSOALog2(soaObject);

        } catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(getListUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(getListUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(getListUserRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), getListUserRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), getListUserRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }


        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // URL: http://localhost:9979/SomeContextPath/provider/{id}
    @RequestMapping(value = "/getdetail-user/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<?> getDetailProvider(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        BaseResponse response = new BaseResponse();
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();
        String responseStatus = Integer.toString(responseSelvet.getStatus());

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        UserCreateResponse userCreateResponse = new UserCreateResponse() ;
        try {

            Optional<User> user = userService.findByUserId(id);

            if(!user.isPresent()){
                throw new CustomException("User is not exist", HttpStatus.BAD_REQUEST, null,null,null, null, HttpStatus.BAD_REQUEST);
            }

            Optional<UserProfile> userProfile = userProfileService.findByUsername(user.get().getUserName());

            if(user.isPresent()){
                userCreateResponse.setId(user.get().getId());
                userCreateResponse.setFullName(userProfile.get().getFullName());
                userCreateResponse.setBirthDate(userProfile.get().getBirthDate());
                userCreateResponse.setIdentifyNumber(userProfile.get().getIdentifyNumber());
                userCreateResponse.setGender(userProfile.get().getGender());
                userCreateResponse.setAddress(userProfile.get().getAddress());
                userCreateResponse.setPhoneNumber(userProfile.get().getPhoneNumber());
                userCreateResponse.setEmail(userProfile.get().getEmail());
                userCreateResponse.setUserName(userProfile.get().getUserName());
                userCreateResponse.setPassword(user.get().getPassword());
                userCreateResponse.setEnabled(userProfile.get().getEnabled());
            }

            //set response data to client
            response.setDetail(userCreateResponse);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);
            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog", null, null, null, "smartMarket", "client",
                    messageTimestamp, "travelinsuranceservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
            logService.createSOALog2(soaObject);

        } catch (Exception ex) {

            //catch truong hop loi kết nối database
            if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), null, customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
