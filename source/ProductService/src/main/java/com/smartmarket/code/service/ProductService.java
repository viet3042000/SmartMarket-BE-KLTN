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

    //Provider-i
    ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws JsonProcessingException, APIAccessException, ParseException;

    //Provider-i
    ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateProductRequest> updateProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws Exception;

    //Provider-i
    ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteProductRequest> deleteProductRequestBaseDetail,
                                    HttpServletRequest request, HttpServletResponse responseSelvet)
                                    throws Exception;

    //Provider-i
    ResponseEntity<?> getProduct(@Valid @RequestBody BaseDetail<QueryProductRequest> queryProductRequestBaseDetail,
                                 HttpServletRequest request, HttpServletResponse responseSelvet)
                                 throws JsonProcessingException;

    //Provider+adminProvider
//    ResponseEntity<?> getListProductOfProvider(@Valid @RequestBody BaseDetail<QueryAllProductOfProviderRequest> queryAllProductOfProviderRequestBaseDetail ,
//                                               HttpServletRequest request,
//                                               HttpServletResponse responseSelvet)
//                                               throws JsonProcessingException, APIAccessException;

    //Admin
    ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail ,
                                     HttpServletRequest request,
                                     HttpServletResponse responseSelvet)
                                     throws JsonProcessingException, APIAccessException;

    //Provider-i
    ResponseEntity<?> approvePendingProduct(@Valid @RequestBody BaseDetail<ApprovePendingProductRequest> approvePendingProductRequest,
                                            HttpServletRequest request, HttpServletResponse responseSelvet)
                                            throws JsonProcessingException, APIAccessException;

    //Provider-i
    ResponseEntity<?> getListPendingProduct(@Valid @RequestBody BaseDetail<QueryPendingProductRequest> queryPendingProductRequestBaseDetail,
                                            HttpServletRequest request, HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException;

    //Admin
//    ResponseEntity<?> getListByState(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail,
//                                     HttpServletRequest request, HttpServletResponse responseSelvet)
//            throws JsonProcessingException, APIAccessException;

}
