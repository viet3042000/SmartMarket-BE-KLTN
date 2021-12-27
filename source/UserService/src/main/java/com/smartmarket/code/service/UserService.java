package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entity.DeleteUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface UserService {

    //admin
    ResponseEntity<?> createProviderAdminUser(@Valid @RequestBody BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //provider_admin
    ResponseEntity<?> createProviderUser(@Valid @RequestBody BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //consumer
    ResponseEntity<?> registerUser(@Valid @RequestBody BaseDetail<CreateUserRequest> createUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //consumer+admin
    ResponseEntity<?> updateUser(@Valid @RequestBody BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin
    ResponseEntity<?> deleteUser(@Valid @RequestBody BaseDetail <DeleteUserRequest> deleteUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin
    ResponseEntity<?> getListUser(@Valid @RequestBody BaseDetail<QueryAllUserRequest> getListUserRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //consumer + admin
    ResponseEntity<?> getDetailUser(@Valid @RequestBody BaseRequest  getDetailUserRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException;

    //consumer+admin
    ResponseEntity<?> changePassword(@Valid @RequestBody BaseDetail<UpdatePasswordRequest> updatePasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //consumer
    ResponseEntity<?> forgotpassword(@Valid @RequestBody BaseDetail<ForgotPasswordRequest> forgotPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //consumer
    ResponseEntity<?> resetpassword(@Valid @RequestBody BaseDetail<ResetPasswordRequest> resetPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

}
