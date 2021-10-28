package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.ProductTypeRepository;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductType;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.response.DetailProductResponse;
import com.smartmarket.code.response.DetailProductTypeResponse;
import com.smartmarket.code.service.ProductTypeService;
import com.smartmarket.code.util.DateTimeUtils;
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
import java.util.Date;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    @Autowired
    ProductTypeRepository productTypeRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    ConfigurableEnvironment environment;


    //Admin
    public ResponseEntity<?> createProductType(@Valid @RequestBody BaseDetail<CreateProductTypeRequest> createProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException{
        String productTypeName = createProductTypeRequestBaseDetail.getDetail().getProductTypeName();
        ProductType productType = productTypeRepository.findByProductTypeName(productTypeName).orElse(null);
        if(productType != null){
            throw new CustomException("productTypeName has already existed", HttpStatus.BAD_REQUEST, createProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        ProductType newProductType = new ProductType();
        newProductType.setProductTypeName(productTypeName);
        newProductType.setDesc(createProductTypeRequestBaseDetail.getDetail().getDesc());
        newProductType.setCreatedLogtimestamp(new Date());
        productTypeRepository.save(newProductType);

        BaseResponse response = new BaseResponse();
        response.setDetail(newProductType);
        response.setResponseId(createProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> updateProductType(@Valid @RequestBody BaseDetail<UpdateProductTypeRequest> updateProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        String productTypeName = updateProductTypeRequestBaseDetail.getDetail().getProductTypeName();
        ProductType productType = productTypeRepository.findByProductTypeName(productTypeName).orElse(null);
        if(productType == null){
            throw new CustomException("productTypeName does not exist", HttpStatus.BAD_REQUEST, updateProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        productType.setDesc(updateProductTypeRequestBaseDetail.getDetail().getDesc());
        productTypeRepository.save(productType);

        BaseResponse response = new BaseResponse();
        response.setDetail(productType);
        response.setResponseId(updateProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> deleteProductType(@Valid @RequestBody BaseDetail<DeleteProductTypeRequest> deleteProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        String productTypeName = deleteProductTypeRequestBaseDetail.getDetail().getProductTypeName();
        ProductType productType = productTypeRepository.findByProductTypeName(productTypeName).orElse(null);
        if(productType == null){
            throw new CustomException("productTypeName does not exist", HttpStatus.BAD_REQUEST, deleteProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        productTypeRepository.delete(productType);

        BaseResponse response = new BaseResponse();
        response.setDetail(productType);
        response.setResponseId(deleteProductTypeRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin
    public ResponseEntity<?> getProductType(@Valid @RequestBody BaseDetail<QueryProductTypeRequest> queryProductTypeRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException{
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        String productTypeName = queryProductTypeRequestBaseDetail.getDetail().getProductTypeName();
        ProductType productType = productTypeRepository.findByProductTypeName(productTypeName).orElse(null);
        if(productType == null){
            throw new CustomException("productTypeName does not exist", HttpStatus.BAD_REQUEST, queryProductTypeRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        DetailProductTypeResponse detailProductTypeResponse = new DetailProductTypeResponse() ;
        detailProductTypeResponse.setId(productType.getId());
        detailProductTypeResponse.setProductTypeName(productTypeName);
        detailProductTypeResponse.setDesc(productType.getDesc());

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
    public ResponseEntity<?> getListProductType(@Valid @RequestBody BaseDetail<QueryAllProductTypeRequest> queryAllProductTypeRequestBaseDetail ,
                                         HttpServletRequest request,
                                         HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        String hostName = request.getRemoteHost();

        int page =  queryAllProductTypeRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllProductTypeRequestBaseDetail.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

        //find by user name and type
        Page<ProductType> allProductTypes =
                productTypeRepository.getAll(pageable);

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        if(!allProductTypes.isEmpty()) {
            totalPage = (int) Math.ceil((double) allProductTypes.getTotalElements()/size);

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
