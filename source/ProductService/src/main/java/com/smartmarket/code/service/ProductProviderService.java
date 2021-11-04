package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;

public interface ProductProviderService {
    //Admin
    ResponseEntity<?> createProductProvider(@Valid @RequestBody BaseDetail<CreateProductTypeRequest> createProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException;

    //Admin
    ResponseEntity<?> updateProductProvider(@Valid @RequestBody BaseDetail<UpdateProductTypeRequest> updateProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //Admin
    ResponseEntity<?> deleteProductProvider(@Valid @RequestBody BaseDetail<DeleteProductTypeRequest> deleteProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //Admin
    ResponseEntity<?> getProductProvider(@Valid @RequestBody BaseDetail<QueryProductTypeRequest> queryProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException;

    //Admin
    ResponseEntity<?> getListProductProvider(@Valid @RequestBody BaseDetail<QueryAllProductTypeRequest> queryAllProductTypeRequestBaseDetail ,
                                             HttpServletRequest request,
                                             HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;
}
