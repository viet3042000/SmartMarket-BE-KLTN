package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.*;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.model.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entity.DeleteUserRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.UserCreateResponse;
//import com.smartmarket.code.service.KeycloakAdminClientService;
import com.smartmarket.code.service.*;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.GetKeyPairUtil;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserProductProviderService userProductProviderService;

    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    @Autowired
    ProductProviderRepository productProviderRepository;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    RoleRepository roleRepository;

//    @Autowired
//    KeycloakAdminClientService keycloakAdminClientService;


    //admin
    public ResponseEntity<?> createProviderAdminUser(@Valid @RequestBody BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        String productProviderName = createProviderAdminUserRequestBaseDetail.getDetail().getProductProviderName();
        ProductProvider productProvider = productProviderRepository.findByProductTypeName(productProviderName).orElse(null);
        if(productProvider == null){
            throw new CustomException("productProvider doesn't exist", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Long productProviderId = productProviderRepository.getId(productProviderName);
        List<UserProductProvider> userProductProviders = userProductProviderRepository.findByProductProviderId(productProviderId);
        if(!userProductProviders.isEmpty()){
            throw new CustomException("Admin of this productProvider existed", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String username = createProviderAdminUserRequestBaseDetail.getDetail().getUserName();
        Optional<User> userExist = userRepository.findByUsername(username);
        if (userExist.isPresent()) {
            throw new CustomException("UserName has already existed", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

//            check email
        User user = userRepository.findByEmailAndProvider(createProviderAdminUserRequestBaseDetail.getDetail().getEmail()).orElse(null);
        if(user != null) {
            throw new CustomException("Email existed", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //check role
        Role role = roleRepository.findByRoleName(createProviderAdminUserRequestBaseDetail.getDetail().getRole()).orElse(null);
        if(role==null){
            throw new CustomException("Role doesn't exist", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        User userCreate = new User();
        userCreate.setUserName(username);
        userCreate.setPassword(bCryptPasswordEncoder.encode(createProviderAdminUserRequestBaseDetail.getDetail().getPassword()));
        userCreate.setEmail(createProviderAdminUserRequestBaseDetail.getDetail().getEmail());
        userCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getEnabled());
        userRepository.save(userCreate);

        userProfileService.createProviderAdminUser(createProviderAdminUserRequestBaseDetail);
        userRoleService.createProviderAdminUser(createProviderAdminUserRequestBaseDetail);
        userProductProviderService.create(username,productProviderId);

//            keycloakAdminClientService.addUser(userCreate, createUserRequestBaseDetail.getDetail().getUser().getPassword());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userCreate);
        response.setResponseId(createProviderAdminUserRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //provider_admin (create role provider1 -5 , 1 provider i = 1 user)
    public ResponseEntity<?> createProviderUser(@Valid @RequestBody BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        //get user token
        Map<String, Object> claims = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nameOfAuthentication = authentication.getClass().getName();
        if(nameOfAuthentication.contains("KeycloakAuthenticationToken")) {
            claims = JwtUtils.getClaimsMapFromKeycloakAuthenticationToken(authentication);
        }else {
            claims = JwtUtils.getClaimsMap(authentication);
        }
        String userNameProviderAdmin = (String) claims.get("user_name");

        Long productProviderId = productProviderRepository.getId(createProviderAdminUserRequestBaseDetail.getDetail().getProductProviderName());
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userNameProviderAdmin, productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String username = createProviderAdminUserRequestBaseDetail.getDetail().getUserName();
        Optional<User> userExist = userRepository.findByUsername(username);
        if (userExist.isPresent()) {
            throw new CustomException("UserName has already existed", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

//            check email
        User user = userRepository.findByEmailAndProvider(createProviderAdminUserRequestBaseDetail.getDetail().getEmail()).orElse(null);
        if(user != null) {
            throw new CustomException("Email existed", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //check role
        Role role = roleRepository.findByRoleName(createProviderAdminUserRequestBaseDetail.getDetail().getRole()).orElse(null);
        if(role==null){
            throw new CustomException("Role doesn't exist", HttpStatus.BAD_REQUEST, createProviderAdminUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        User userCreate = new User();
        userCreate.setUserName(username);
        userCreate.setPassword(bCryptPasswordEncoder.encode(createProviderAdminUserRequestBaseDetail.getDetail().getPassword()));
        userCreate.setEmail(createProviderAdminUserRequestBaseDetail.getDetail().getEmail());
        userCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getEnabled());
        userRepository.save(userCreate);

        userProfileService.createProviderAdminUser(createProviderAdminUserRequestBaseDetail);
        userRoleService.createProviderAdminUser(createProviderAdminUserRequestBaseDetail);
        userProductProviderService.create(username,productProviderId);

//            keycloakAdminClientService.addUser(userCreate, createUserRequestBaseDetail.getDetail().getUser().getPassword());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userCreate);
        response.setResponseId(createProviderAdminUserRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> registerUser(@Valid @RequestBody BaseDetail<CreateUserRequest> createUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {

        String username = createUserRequestBaseDetail.getDetail().getUserName();
        Optional<User> userExist = userRepository.findByUsername(username);
        if (userExist.isPresent()) {
            throw new CustomException("UserName has already existed", HttpStatus.BAD_REQUEST, createUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String provider = createUserRequestBaseDetail.getDetail().getProvider();
//            check email
        if(provider == null) {
            User user = userRepository.findByEmailAndProvider(createUserRequestBaseDetail.getDetail().getEmail()).orElse(null);
            if (user != null) {
                throw new CustomException("Email existed", HttpStatus.BAD_REQUEST, createUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
        }

        //check role
        Role role = roleRepository.findByRoleName(createUserRequestBaseDetail.getDetail().getRole()).orElse(null);
        if(role==null){
            throw new CustomException("Role doesn't exist", HttpStatus.BAD_REQUEST, createUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        User userCreate = new User();
        userCreate.setUserName(username);
        userCreate.setPassword(bCryptPasswordEncoder.encode(createUserRequestBaseDetail.getDetail().getPassword()));
        userCreate.setOauthProvider(createUserRequestBaseDetail.getDetail().getProvider());
        userCreate.setEmail(createUserRequestBaseDetail.getDetail().getEmail());
        userCreate.setEnabled(createUserRequestBaseDetail.getDetail().getEnabled());
        userRepository.save(userCreate);

        userProfileService.create(createUserRequestBaseDetail);
        userRoleService.create(createUserRequestBaseDetail);

//            keycloakAdminClientService.addUser(userCreate, createUserRequestBaseDetail.getDetail().getUser().getPassword());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userCreate);
        response.setResponseId(createUserRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //consumer+admin
    public ResponseEntity<?> updateUser(@Valid @RequestBody BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");
        User userUpdate = userRepository.findByUsername(userName).orElse(null) ;
        if(userUpdate == null){
            throw new CustomException("User does not exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

//            check email
        User userFoundByEmail = userRepository.findByEmailAndProvider(updateUserRequestBaseDetail.getDetail().getEmail()).orElse(null);
        if(userFoundByEmail != null) {
            if(!userName.equals(userFoundByEmail.getUserName())) {
                throw new CustomException("Email existed", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
        }

        UserProfile userProfileUpdate = userProfileRepository.findByUsername(userName).orElse(null);
        if (userProfileUpdate == null) {
            throw new CustomException("userProfile does not exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        UserRole userRoleUpdate = userRoleRepository.findByUserName(userName).orElse(null);
        if (userRoleUpdate == null) {
            throw new CustomException("UserRole does not exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        //eliminate null value from request body
        JSONObject detail = new JSONObject(updateUserRequestBaseDetail.getDetail());
        Map<String, Object> keyPairs = new HashMap<>();
        getKeyPairUtil.getKeyPair(detail, keyPairs);

        for (String k : keyPairs.keySet()) {
            if (k.equals("newRole")) {
                //check role existed
                Role role= roleRepository.findByRoleName((String) keyPairs.get(k)).orElse(null);
                if(role ==null){
                    throw new CustomException("newRole doesn't exist", HttpStatus.BAD_REQUEST, updateUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
                userRoleUpdate.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("email")) {
                userUpdate.setEmail((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                userUpdate.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
        }
        userRepository.save(userUpdate);

        userProfileService.update(userProfileUpdate,keyPairs);
        userRoleService.update(userRoleUpdate,keyPairs,updateUserRequestBaseDetail.getRequestId());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userUpdate);
        response.setResponseId(updateUserRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //admin
    public ResponseEntity<?> deleteUser(@Valid @RequestBody BaseDetail <DeleteUserRequest>  deleteUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        String userName = deleteUserRequestBaseDetail.getDetail().getUserName();
        User userDelete = userRepository.findByUsername(userName).orElse(null) ;
        if(userDelete == null){
            throw new CustomException("User does not exist", HttpStatus.BAD_REQUEST, deleteUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        userRepository.delete(userDelete);

        UserProfile userProfileDelete = userProfileRepository.findByUsername(userName).orElse(null);
        if (userProfileDelete == null) {
            throw new CustomException("userProfile does not exist", HttpStatus.BAD_REQUEST, deleteUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        userProfileRepository.delete(userProfileDelete);

        UserRole userRoleDelete = userRoleRepository.findByUserName(userName).orElse(null);
        if (userRoleDelete == null) {
            throw new CustomException("UserRole does not exist", HttpStatus.BAD_REQUEST, deleteUserRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }
        userRoleRepository.delete(userRoleDelete);

        userProductProviderRepository.deleteByUserName(userName);

//            keycloakAdminClientService.deleteUser(userDelete.getUserName());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userDelete);
        response.setResponseId(deleteUserRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);


        //calculate time duration
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        String responseStatus = Integer.toString(responseSelvet.getStatus());
        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", deleteUserRequestBaseDetail.getRequestId(), deleteUserRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                messageTimestamp, "userservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //admin
    public ResponseEntity<?> getListUser(@Valid @RequestBody BaseDetail<QueryAllUserRequest> getListUserRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {

        Page<User> pageResult = null;
        Page<UserCreateResponse> userCreateResponsePage = null;

        int page =  getListUserRequestBaseDetail.getDetail().getPage()  ;
        int size =  getListUserRequestBaseDetail.getDetail().getSize()   ;

        Pageable pageable = PageRequest.of(page - 1 , size);

        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        pageResult = userRepository.findAllUser(pageable);
        userCreateResponsePage = pageResult.map(new Function<User, UserCreateResponse>() {
            @Override
            public UserCreateResponse apply(User user) {
                UserCreateResponse userCreateResponse  = new UserCreateResponse() ;
                userCreateResponse.setId(user.getId());
                Optional<UserProfile> userProfile = userProfileService.findByUsername(user.getUserName()) ;
                String role = userRoleRepository.findRoleByUserName(user.getUserName());

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

                    userCreateResponse.setRole(role);
                }
                return userCreateResponse;
            }
        });


        page =  getListUserRequestBaseDetail.getDetail().getPage();
        int totalPage =(int) Math.ceil((double) pageResult.getTotalElements()/size) ;
        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userCreateResponsePage.getContent());
        response.setPage(page);
        response.setTotalPage(totalPage);
        if(pageResult.isEmpty()){
            response.setTotalPage(1);
        }
        response.setTotal(pageResult.getTotalElements());

        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        //calculate time duration
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        String responseStatus = Integer.toString(responseSelvet.getStatus());
        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", getListUserRequestBaseDetail.getRequestId(), getListUserRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                messageTimestamp, "userservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
        logService.createSOALog2(soaObject);


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //consumer + admin
    public ResponseEntity<?> getDetailUser(@Valid @RequestBody BaseRequest getDetailUserRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        // declare value for log
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("User does not exist", HttpStatus.BAD_REQUEST, null,null,null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<UserProfile> userProfile = userProfileService.findByUsername(userName);

        UserCreateResponse userCreateResponse = new UserCreateResponse() ;
        userCreateResponse.setId(user.getId());
        userCreateResponse.setFullName(userProfile.get().getFullName());
        userCreateResponse.setBirthDate(userProfile.get().getBirthDate());
        userCreateResponse.setIdentifyNumber(userProfile.get().getIdentifyNumber());
        userCreateResponse.setGender(userProfile.get().getGender());
        userCreateResponse.setAddress(userProfile.get().getAddress());
        userCreateResponse.setPhoneNumber(userProfile.get().getPhoneNumber());
        userCreateResponse.setEmail(userProfile.get().getEmail());
        userCreateResponse.setUserName(userName);
        userCreateResponse.setPassword(user.getPassword());
        userCreateResponse.setEnabled(userProfile.get().getEnabled());

        String role = userRoleRepository.findRoleByUserName(userName);
        userCreateResponse.setRole(role);

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userCreateResponse);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        //calculate time duration
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        String responseStatus = Integer.toString(responseSelvet.getStatus());
        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", null, null, null, "smartMarket", "client",
                messageTimestamp, "userservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //front end check 2 password is difference before
    //consumer+admin
    public ResponseEntity<?> changePassword(@Valid @RequestBody BaseDetail<UpdatePasswordRequest> updatePasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");
        User userUpdate = userRepository.findByUsername(userName).orElse(null) ;
        if(userUpdate ==null){
            throw new CustomException("User is not exist", HttpStatus.BAD_REQUEST, updatePasswordRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        BaseResponse response = new BaseResponse();
        if(bCryptPasswordEncoder.matches(updatePasswordRequestBaseDetail.getDetail().getOldPassword(),userUpdate.getPassword())){
//                if (updatePasswordRequestBaseDetail.getDetail().getOldPassword().equals(updatePasswordRequestBaseDetail.getDetail().getNewPassword())){
//                    throw new CustomException("newPassword equals with oldPassword", HttpStatus.BAD_REQUEST, updatePasswordRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
//                }
            userUpdate.setPassword(bCryptPasswordEncoder.encode(updatePasswordRequestBaseDetail.getDetail().getNewPassword()));
            userRepository.save(userUpdate);

//                keycloakAdminClientService.changePassword(userUpdate,updatePasswordRequestBaseDetail.getDetail().getNewPassword());

            //set response data to client
            response.setDetail(userUpdate);
            response.setResponseId(updatePasswordRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
        }else {
            throw new CustomException("oldPassword didn't exist", HttpStatus.BAD_REQUEST, updatePasswordRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //consumer
    public ResponseEntity<?> forgotpassword(@Valid @RequestBody BaseDetail<ForgotPasswordRequest> forgotPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        Optional<User> user = userRepository.findByEmailAndProvider(forgotPasswordRequestBaseDetail.getDetail().getEmail());
        if(!user.isPresent()) {
            throw new CustomException("Email does not match with all users", HttpStatus.BAD_REQUEST, forgotPasswordRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String role = userRoleRepository.findRoleByUserName(user.get().getUserName());
        if(role != null) {
            if (!role.equals("CUSTOMER")) {
                throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, forgotPasswordRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }else {
            throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, forgotPasswordRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        User userReset = user.get();

        //send message to email
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        mimeMessageHelper.setFrom("viet3040200@gmail.com");
        mimeMessageHelper.setTo(forgotPasswordRequestBaseDetail.getDetail().getEmail());
        mimeMessageHelper.setSubject("Password reset");

        Context context = new Context();
        context.setVariable("username", userReset.getUserName());

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUserName(userReset.getUserName());
        Date date = new Date();
        long timeInSecs = date.getTime();
        Date afterAdding = new Date(timeInSecs + 120000);
        passwordResetToken.setExpiredTime(afterAdding);
        passwordResetTokenRepository.save(passwordResetToken);

        //url of reset password page (fake)
        String url = "http://localhost:8081/reset-password-form?token="+token;
        context.setVariable("url", url);

        String process = templateEngine.process("Email-Template", context);
        mimeMessageHelper.setText(process, true);
        javaMailSender.send(message);

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userReset);
        response.setResponseId(forgotPasswordRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //consumer
    public ResponseEntity<?> resetpassword(@Valid @RequestBody BaseDetail<ResetPasswordRequest> resetPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequestBaseDetail.getDetail().getToken()).orElse(null);
        if(passwordResetToken == null){
            throw new CustomException("Token does not exist", HttpStatus.BAD_REQUEST, resetPasswordRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        Long timeDuration = DateTimeUtils.getElapsedTime(passwordResetToken.getExpiredTime().getTime());
        if(timeDuration > 120000){
            throw new CustomException("Token expired", HttpStatus.BAD_REQUEST, resetPasswordRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByUsername(passwordResetToken.getUserName());
        if (!user.isPresent()) {
            throw new CustomException("User does not exist", HttpStatus.BAD_REQUEST, resetPasswordRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        User userReset = user.get();

        if(bCryptPasswordEncoder.matches(resetPasswordRequestBaseDetail.getDetail().getNewPassword(),userReset.getPassword())){
            throw new CustomException("NewPassword existed", HttpStatus.BAD_REQUEST, resetPasswordRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        userReset.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequestBaseDetail.getDetail().getNewPassword()));
        userRepository.save(userReset);

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(userReset);
        response.setResponseId(resetPasswordRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
