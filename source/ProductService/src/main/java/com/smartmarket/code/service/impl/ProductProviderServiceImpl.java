package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.ProductProviderRepository;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.response.DetailProductTypeResponse;
import com.smartmarket.code.service.ProductProviderService;
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
import java.text.ParseException;
import java.util.*;

@Service
public class ProductProviderServiceImpl implements ProductProviderService {

    @Autowired
    ProductProviderRepository productProviderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    ConfigurableEnvironment environment;


    //Admin
    public ResponseEntity<?> createProductProvider(@Valid @RequestBody BaseDetail<CreateProductProviderRequest> createProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException{
        String productProviderName = createProductTypeRequestBaseDetail.getDetail().getProductProviderName();
        ProductProvider productProvider = productProviderRepository.findByProductProviderName(productProviderName).orElse(null);
        if(productProvider != null){
            throw new CustomException("productProviderName existed", HttpStatus.BAD_REQUEST, createProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        ProductProvider newProductProvider = new ProductProvider();
        newProductProvider.setProductProviderName(productProviderName);
        newProductProvider.setDesc(createProductTypeRequestBaseDetail.getDetail().getDesc());
        newProductProvider.setCreatedLogtimestamp(new Date());
        productProviderRepository.save(newProductProvider);

        BaseResponse response = new BaseResponse();
        response.setDetail(newProductProvider);
        response.setResponseId(createProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> updateProductProvider(@Valid @RequestBody BaseDetail<UpdateProductProviderRequest> updateProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        String productProviderName = updateProductTypeRequestBaseDetail.getDetail().getProductProviderName();
        ProductProvider productProvider = productProviderRepository.findByProductProviderName(productProviderName).orElse(null);
        if(productProvider == null){
            throw new CustomException("productProviderName does not exist", HttpStatus.BAD_REQUEST, updateProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        //eliminate null value from request body
        JSONObject detail = new JSONObject(updateProductTypeRequestBaseDetail.getDetail());
        Map<String, Object> keyPairs = new HashMap<>();
        getKeyPairUtil.getKeyPair(detail, keyPairs);

        for (String k : keyPairs.keySet()) {
            if (k.equals("newProductProviderName")) {
                String newProductProviderName = (String) keyPairs.get(k);
                if(newProductProviderName.equals(productProviderName)){
                    throw new CustomException("newProductProviderName is equal with oldProductProviderName", HttpStatus.BAD_REQUEST, updateProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }

                //check newProductProviderName existed
                ProductProvider p = productProviderRepository.findByProductProviderName(newProductProviderName).orElse(null);
                if(p!=null){
                    throw new CustomException("newProductProviderName existed", HttpStatus.BAD_REQUEST, updateProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }

                productProvider.setProductProviderName(newProductProviderName);
                //update newProductProvdierName in all product has equal productProviderName
                List<Product> listProducts = productRepository.findByProductProvider(productProviderName);
                if(!listProducts.isEmpty()) {
                    for (Product product : listProducts) {
                        product.setProductProvider(newProductProviderName);
                        productRepository.save(product);
                    }
                }
            }
            if (k.equals("desc")) {
                productProvider.setDesc((String) keyPairs.get(k));
            }
        }
        productProviderRepository.save(productProvider);

        BaseResponse response = new BaseResponse();
        response.setDetail(productProvider);
        response.setResponseId(updateProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> deleteProductProvider(@Valid @RequestBody BaseDetail<DeleteProductTypeRequest> deleteProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        String productProviderName = deleteProductTypeRequestBaseDetail.getDetail().getProductProviderName();
        ProductProvider productProvider = productProviderRepository.findByProductProviderName(productProviderName).orElse(null);
        if(productProvider == null){
            throw new CustomException("productProviderName does not exist", HttpStatus.BAD_REQUEST, deleteProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        Long id = productProvider.getId();
        productProviderRepository.delete(productProvider);

        //delete product
        productRepository.deleteByProductProvider(productProviderName);

        BaseResponse response = new BaseResponse();
        response.setDetail(productProvider);
        response.setResponseId(deleteProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> getProductProvider(@Valid @RequestBody BaseDetail<QueryProductTypeRequest> queryProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException{
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        String productProviderName = queryProductTypeRequestBaseDetail.getDetail().getProductProviderName();
        ProductProvider productProvider = productProviderRepository.findByProductProviderName(productProviderName).orElse(null);
        if(productProvider == null){
            throw new CustomException("productProviderName does not exist", HttpStatus.BAD_REQUEST, queryProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        DetailProductTypeResponse detailProductTypeResponse = new DetailProductTypeResponse() ;
        detailProductTypeResponse.setId(productProvider.getId());
        detailProductTypeResponse.setProductTypeName(productProviderName);
        detailProductTypeResponse.setDesc(productProvider.getDesc());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(detailProductTypeResponse);
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
                messageTimestamp, "productservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), Utils.getClientIp(request), operationName);
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> getListProductProvider(@Valid @RequestBody BaseDetail<QueryAllProductTypeRequest> queryAllProductTypeRequestBaseDetail ,
                                                    HttpServletRequest request,
                                                    HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        String hostName = request.getRemoteHost();

        int page =  queryAllProductTypeRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllProductTypeRequestBaseDetail.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

        //find by user name and type
        Page<ProductProvider> allProductTypes =
                productProviderRepository.getAll(pageable);

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        if(!allProductTypes.isEmpty()) {
            int totalPage = (int) Math.ceil((double) allProductTypes.getTotalElements()/size);

            //set response data to client
            response.setResponseId(queryAllProductTypeRequestBaseDetail.getRequestId());
            response.setDetail(allProductTypes.getContent());
            response.setPage(page);
            response.setTotalPage(totalPage);
            response.setTotal(allProductTypes.getTotalElements());

            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductTypeRequestBaseDetail.getRequestId(), queryAllProductTypeRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

        }else{
            //set response data to client
            response.setResponseId(queryAllProductTypeRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("This provider has no product");

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductTypeRequestBaseDetail.getRequestId(), queryAllProductTypeRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
