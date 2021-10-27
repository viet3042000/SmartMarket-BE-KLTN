package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface ProductService {
    //admin
    ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateUserRequest> createUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //admin
    ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin
    ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteUserRequest> deleteUserRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //admin+user
    ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllUserRequest> getListUserRequestBaseDetail ,
                                  HttpServletRequest request,
                                  HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;

    //admin+user
    ResponseEntity<?> getDetailProduct(@Valid @RequestBody BaseDetail<GetDetailUserRequest>  getDetailUserRequestBaseDetail,HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException;
}
