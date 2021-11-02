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

public interface ProductTypeService {
    //Admin(kltn)
    ResponseEntity<?> createProductType(@Valid @RequestBody BaseDetail<CreateProductTypeRequest> createProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException;

    //Admin(kltn)
    ResponseEntity<?> updateProductType(@Valid @RequestBody BaseDetail<UpdateProductTypeRequest> updateProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //Admin(kltn)
    ResponseEntity<?> deleteProductType(@Valid @RequestBody BaseDetail<DeleteProductTypeRequest> deleteProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception;

    //Admin(kltn)
    ResponseEntity<?> getProductType(@Valid @RequestBody BaseDetail<QueryProductTypeRequest> queryProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException;

    //Admin(kltn)
    ResponseEntity<?> getListProductType(@Valid @RequestBody BaseDetail<QueryAllProductTypeRequest> queryAllProductTypeRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException;
}
