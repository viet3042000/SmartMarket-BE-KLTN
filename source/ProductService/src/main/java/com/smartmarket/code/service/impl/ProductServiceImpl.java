package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.response.DetailProductResponse;
import com.smartmarket.code.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    ConfigurableEnvironment environment;


    //Admin(kltn)+ Provider
    public ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        User user = userRepository.findByUsername(createProductRequestBaseDetail.getDetail().getUserName()).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in productService", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String productName = createProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product != null){
            throw new CustomException("productName has already existed", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Product newProduct = new Product();
        newProduct.setProductName(productName);
        newProduct.setProductProvider(createProductRequestBaseDetail.getDetail().getProductProvider());
        newProduct.setType(createProductRequestBaseDetail.getDetail().getType());
        newProduct.setDesc(createProductRequestBaseDetail.getDetail().getDesc());
        newProduct.setPrice(createProductRequestBaseDetail.getDetail().getPrice());

//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        String stringCreateAt = formatter.format(date);
//        Date createAt = formatter.parse(stringCreateAt);
//        newProduct.setCreatedLogtimestamp(createAt);
        newProduct.setCreatedLogtimestamp(new Date());
        productRepository.save(newProduct);

        BaseResponse response = new BaseResponse();
        response.setDetail(newProduct);
        response.setResponseId(createProductRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin(kltn)+ Provider
    public ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateProductRequest> updateProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        User user = userRepository.findByUsername(updateProductRequestBaseDetail.getDetail().getUserName()).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in productService", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String productName = updateProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        product.setProductProvider(updateProductRequestBaseDetail.getDetail().getProductProvider());
        product.setType(updateProductRequestBaseDetail.getDetail().getType());
        product.setDesc(updateProductRequestBaseDetail.getDetail().getDesc());
        product.setPrice(updateProductRequestBaseDetail.getDetail().getPrice());
        productRepository.save(product);

        BaseResponse response = new BaseResponse();
        response.setDetail(product);
        response.setResponseId(updateProductRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin(kltn)+ Provider
    public ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteProductRequest> deleteProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        User user = userRepository.findByUsername(deleteProductRequestBaseDetail.getDetail().getUserName()).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in productService", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String productName = deleteProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        productRepository.delete(product);

        BaseResponse response = new BaseResponse();
        response.setDetail(product);
        response.setResponseId(deleteProductRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Admin + Provider
    public ResponseEntity<?> getProduct(@Valid @RequestBody BaseDetail<QueryProductRequest>  queryProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException{
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        User user = userRepository.findByUsername(queryProductRequestBaseDetail.getDetail().getUserName()).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in productService", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        String productName = queryProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        DetailProductResponse detailProductResponse = new DetailProductResponse() ;
        detailProductResponse.setId(product.getId());
        detailProductResponse.setProductName(productName);
        detailProductResponse.setProductProvider(product.getProductProvider());
        detailProductResponse.setType(product.getType());
        detailProductResponse.setPrice(product.getPrice());
        detailProductResponse.setDesc(product.getDesc());

        //set response data to client
        BaseResponse response = new BaseResponse();
        response.setDetail(detailProductResponse);
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


    //Admin + Provider
    public ResponseEntity<?> getListProductOfProvider(@Valid @RequestBody BaseDetail<QueryAllProductOfProviderRequest> queryAllProductOfProviderRequestBaseDetail ,
                                                      HttpServletRequest request,
                                                      HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        String userName = queryAllProductOfProviderRequestBaseDetail.getDetail().getUserName();
        User user = userRepository.findByUsername(userName).orElse(null);
        if(user == null){
            throw new CustomException("UserName does not exist in productService", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
        }

        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        String hostName = request.getRemoteHost();

        int page =  queryAllProductOfProviderRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllProductOfProviderRequestBaseDetail.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

        //find by user name and type
        Page<Product> allOrders =
                productRepository.findByUserName(userName,pageable);

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        if(!allOrders.isEmpty()) {
            totalPage = (int) Math.ceil((double) allOrders.getTotalElements()/size);

            //set response data to client
            response.setResponseId(queryAllProductOfProviderRequestBaseDetail.getRequestId());
            response.setDetail(allOrders.getContent());
            response.setPage(page);
            response.setTotalPage(totalPage);
            response.setTotal(allOrders.getTotalElements());

            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductOfProviderRequestBaseDetail.getRequestId(), queryAllProductOfProviderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

        }else{
            //set response data to client
            response.setResponseId(queryAllProductOfProviderRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("This provider has no product");

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductOfProviderRequestBaseDetail.getRequestId(), queryAllProductOfProviderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //Admin
    public ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail,
                                            HttpServletRequest request,
                                            HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        int totalPage = 0 ;
        BaseResponseGetAll response = new BaseResponseGetAll();
        ObjectMapper mapper = new ObjectMapper();
        String hostName = request.getRemoteHost();

        int page =  queryAllProductRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryAllProductRequestBaseDetail.getDetail().getSize()   ;
        Pageable pageable = PageRequest.of(page-1, size);

        //find by user name and type
        Page<Product> allProducts =
                productRepository.getAll(pageable);

        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        if(!allProducts.isEmpty()) {
            totalPage = (int) Math.ceil((double) allProducts.getTotalElements()/size);

            //set response data to client
            response.setResponseId(queryAllProductRequestBaseDetail.getRequestId());
            response.setDetail(allProducts.getContent());
            response.setPage(page);
            response.setTotalPage(totalPage);
            response.setTotal(allProducts.getTotalElements());

            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductRequestBaseDetail.getRequestId(), queryAllProductRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);

        }else{
            //set response data to client
            response.setResponseId(queryAllProductRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("This provider has no product");

            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //calculate time duration
            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);

            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductRequestBaseDetail.getRequestId(), queryAllProductRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
                    messageTimestamp, "productservice", "1", timeDurationResponse,
                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
            logService.createSOALog2(soaObject);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
