package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface UserService {

    //user
    ResponseEntity<?> registerUser(@Valid @RequestBody BaseDetail<CreateUserRequest> createUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //user
    ResponseEntity<?> updateUser(@Valid @RequestBody BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin
    ResponseEntity<?> deleteUser(@Valid @RequestBody BaseDetail<DeleteUserRequest> deleteUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin
    ResponseEntity<?> getListUser(@Valid @RequestBody BaseDetail<QueryAllUserRequest> getListUserRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //user + admin
    ResponseEntity<?> getDetailUser(@Valid @RequestBody BaseDetail<GetDetailUserRequest>  getDetailUserRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException;

    //user
    ResponseEntity<?> changePassword(@Valid @RequestBody BaseDetail<UpdatePasswordRequest> updatePasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //user
    ResponseEntity<?> forgotpassword(@Valid @RequestBody BaseDetail<ForgotPasswordRequest> forgotPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //user
    ResponseEntity<?> resetpassword(@Valid @RequestBody BaseDetail<ResetPasswordRequest> resetPasswordRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

}
