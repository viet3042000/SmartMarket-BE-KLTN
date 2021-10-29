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

public interface ProductService {

    //Admin(kltn)+ Provider
    ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws JsonProcessingException, APIAccessException, ParseException;

    //Admin(kltn)+ Provider
    ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateProductRequest> updateProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws Exception;

    //Admin(kltn)+ Provider
    ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteProductRequest> deleteProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws Exception;

    //Admin + Provider
    ResponseEntity<?> getProduct(@Valid @RequestBody BaseDetail<QueryProductRequest> queryProductRequestBaseDetail,
                                 HttpServletRequest request, HttpServletResponse responseSelvet)
                                 throws JsonProcessingException;

    //Admin + Provider
    ResponseEntity<?> getListProductOfProvider(@Valid @RequestBody BaseDetail<QueryAllProductOfProviderRequest> queryAllProductOfProviderRequestBaseDetail ,
                                               HttpServletRequest request,
                                               HttpServletResponse responseSelvet)
                                               throws JsonProcessingException, APIAccessException;

    //Admin
    ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail ,
                                     HttpServletRequest request,
                                     HttpServletResponse responseSelvet)
                                     throws JsonProcessingException, APIAccessException;

    //Admin
    ResponseEntity<?> approvePendingProduct(@Valid @RequestBody BaseDetail<ApprovePendingProductRequest> approvePendingProductRequest,
                                            HttpServletRequest request, HttpServletResponse responseSelvet)
                                            throws JsonProcessingException, APIAccessException;

}
