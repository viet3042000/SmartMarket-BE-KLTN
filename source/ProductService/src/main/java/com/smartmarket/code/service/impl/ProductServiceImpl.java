package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.*;
import com.smartmarket.code.dao.*;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.*;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entity.StateApproval;
import com.smartmarket.code.request.entity.StepDecision;
import com.smartmarket.code.request.entity.StepFlow;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.response.DetailProductResponse;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductProviderRepository productProviderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    ProductApprovalFlowRepository productApprovalFlowRepository;

    @Autowired
    OutboxRepository outboxRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;


    //Admin(kltn)+ Provider
    public ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        String userName = createProductRequestBaseDetail.getDetail().getUserName();
        String productProviderName = createProductRequestBaseDetail.getDetail().getProductProvider();
        ProductProvider productProvider = productProviderRepository.findByProductProviderName(productProviderName).orElse(null);
        if(productProvider == null){
            throw new CustomException("productProviderName doesn't exist", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Long productProviderId = productProvider.getId();
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String productName = createProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product != null){
            throw new CustomException("productName existed", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        //check flow of provider
        //if flow of product doesn't exist --> exception
        ApprovalFlow approvalFlow =  approvalFlowRepository.findApprovalFlowOfProduct(productName, productProviderId,"createProduct").orElse(null);
        if(approvalFlow ==null){
            throw new CustomException("approvalFlow doesn't exist", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        Product newProduct = new Product();
        newProduct.setProductName(productName);
        newProduct.setType(createProductRequestBaseDetail.getDetail().getType());
        newProduct.setDesc(createProductRequestBaseDetail.getDetail().getDesc());
        newProduct.setPrice(createProductRequestBaseDetail.getDetail().getPrice());

        if(approvalFlow.getStepDetail() != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(approvalFlow.getStepDetail(), StepFlow[].class));
            StepFlow step1 = stepFlows.get(0);
            StateApproval stateApproval = new StateApproval();
            stateApproval.setStateName("PendingApprove");
            stateApproval.setRoleName(step1.getRoleName());
            newProduct.setState(new JSONObject(stateApproval).toString());
        }else{
            StateApproval stateApproval = new StateApproval();
            stateApproval.setStateName("Completed");
            newProduct.setState(new JSONObject(stateApproval).toString());
        }
        newProduct.setCreatedLogtimestamp(new Date());
        newProduct.setProductProvider(productProviderName);
        productRepository.save(newProduct);

        ProductApprovalFlow productApprovalFlow = new ProductApprovalFlow();
        productApprovalFlow.setProductId(newProduct.getId());
        productApprovalFlow.setCreatedLogtimestamp(new Date());
        productApprovalFlow.setFlowName("createProduct");
        productApprovalFlow.setStepDetail(approvalFlow.getStepDetail());
        productApprovalFlow.setNumberOfSteps(approvalFlow.getNumberOfSteps());
        productApprovalFlowRepository.save(productApprovalFlow);

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
        String userName = updateProductRequestBaseDetail.getDetail().getUserName();
        String productName = updateProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String productProviderName = product.getProductProvider();
        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        //eliminate null value from request body
        JSONObject detail = new JSONObject(updateProductRequestBaseDetail.getDetail());
        Map<String, Object> keyPairs = new HashMap<>();
        getKeyPairUtil.getKeyPair(detail, keyPairs);

        for (String k : keyPairs.keySet()) {
            if (k.equals("newProductName")) {
                //check newProductName existed
                String newProductName =(String) keyPairs.get(k);
                if(newProductName.equals(productName)){
                    throw new CustomException("newProductName is equal with oldProductName", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }

                Product p = productRepository.findByProductName(newProductName).orElse(null);
                if(p != null){
                    throw new CustomException("newProductName existed", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }
                product.setProductName(newProductName);
            }
            if (k.equals("type")) {
                product.setType((String) keyPairs.get(k));
            }
            if (k.equals("desc")) {
                product.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("price")) {
                product.setPrice((String) keyPairs.get(k));
            }
        }
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
        String userName =deleteProductRequestBaseDetail.getDetail().getUserName();
        String productName = deleteProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String productProviderName = product.getProductProvider();
        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
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

        String userName =queryProductRequestBaseDetail.getDetail().getUserName();
        String productName = queryProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String productProviderName = product.getProductProvider();
        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
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

        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findByUserName(userName).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userName in claim doesn't exist in userProductProvider table", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
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


    //Provider-i
    public ResponseEntity<?> approvePendingProduct(@Valid @RequestBody BaseDetail<ApprovePendingProductRequest> approvePendingProductRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        //get user token
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
//        String userName = (String) claims.get("user_name");
//        User user = userRepository.findByUsername(userName).orElse(null);
//        if(user == null){
//            throw new CustomException("UserName doesn't exist in orderService", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//        }

        Long productId = approvePendingProductRequest.getDetail().getProductId();
        Product product = productRepository.findByProductId(productId).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        String productProviderName = product.getProductProvider();

        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
//        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
//        if(userProductProvider == null){
//            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
//        }

        String currentState = product.getState();
        Gson g = new Gson();
        StateApproval stateApproval = g.fromJson(currentState, StateApproval.class);
        if("Completed".equals(stateApproval.getStateName())){
            throw new CustomException("Product was completed", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

//        ArrayList<String> roles = authorizationService.getRoles();
//        if(!roles.contains(stateApproval.getRoleName())){
//            throw new CustomException("Role isn't accepted with current approval step ", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
//        }

        String flowName = approvePendingProductRequest.getDetail().getFlowName();

//        int currentStep = 0;
//        SagaState sagaState = sagaStateRepository.findById(approvePendingProductRequest.getRequestId()).orElse(null);
//        if(sagaState != null){
//            ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findProductApprovalFlow(flowName,productId).orElse(null);
//            ObjectMapper mapper = new ObjectMapper();
//            List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));
//            for(StepFlow stepFlow : stepFlows){
//                if(stateApproval.getRoleName().equals(stepFlow.getRoleName())){
//                    currentStep = stepFlow.getStepNumber();
//                }
//            }
//            sagaState.setCurrentStep(Integer.toString(currentStep));
////            newSagaState.setPayload();
//            sagaStateRepository.save(sagaState);
//        }else {
//            //create sage
//            SagaState newSagaState = new SagaState();
//            newSagaState.setId(approvePendingProductRequest.getRequestId());
//            newSagaState.setCurrentStep("");
//            newSagaState.setStepState("");
//            newSagaState.setAggregateId(productId.toString());
//            newSagaState.setType(SagaStateType.APPROVE_CREATED_PRODUCT);
//            newSagaState.setStatus(SagaStateStatus.STARTED);
//            newSagaState.setCreatedLogtimestamp(new Date());
////            newSagaState.setPayload();
//            sagaStateRepository.save(newSagaState);
//        }


        SagaState newSagaState = new SagaState();
        newSagaState.setId(approvePendingProductRequest.getRequestId());
        newSagaState.setCurrentStep("");
        newSagaState.setStepState("");
        newSagaState.setAggregateId(productId.toString());
        newSagaState.setType(SagaStateType.APPROVE_CREATED_PRODUCT);
        newSagaState.setStatus(SagaStateStatus.STARTED);
        newSagaState.setCreatedLogtimestamp(new Date());
//            newSagaState.setPayload();
        sagaStateRepository.save(newSagaState);

        //create outbox
        Outbox outBox = new Outbox();
        outBox.setAggregateId(productId.toString());
        outBox.setCreatedLogtimestamp(new Date());
        outBox.setAggregateType("Product");
        outBox.setType(OutboxType.APPROVE_CREATED_PRODUCT);

        StepDecision stepDecision = new StepDecision();
        stepDecision.setRequestId(approvePendingProductRequest.getRequestId());
        stepDecision.setProductId(productId);
        stepDecision.setRoleName(stateApproval.getRoleName());
        stepDecision.setDecision(approvePendingProductRequest.getDetail().getDecision());
        stepDecision.setFlowName(flowName);

        ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findProductApprovalFlow(flowName,productId).orElse(null);
        ObjectMapper mapper = new ObjectMapper();
        List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));
        for(StepFlow stepFlow : stepFlows){
            if(stateApproval.getRoleName().equals(stepFlow.getRoleName())){
                stepDecision.setCurrentStepNumber(stepFlow.getStepNumber());
            }
        }
        outBox.setPayload(new JSONObject(stepDecision).toString());
        outboxRepository.save(outBox);

        BaseResponse response = new BaseResponse();
        response.setDetail(product);
        response.setResponseId(approvePendingProductRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //Admin
//    public ResponseEntity<?> getListByState(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail,
//                                            HttpServletRequest request,
//                                            HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
//        int totalPage = 0 ;
//        BaseResponseGetAll response = new BaseResponseGetAll();
//        ObjectMapper mapper = new ObjectMapper();
//        String hostName = request.getRemoteHost();
//
//        int page =  queryAllProductRequestBaseDetail.getDetail().getPage()  ;
//        int size =  queryAllProductRequestBaseDetail.getDetail().getSize()   ;
//        Pageable pageable = PageRequest.of(page-1, size);
//
//        //find by user name and type
//        Page<Product> allProducts =
//                productRepository.getAllByState(queryAllProductRequestBaseDetail.getDetail().getState(),pageable);
//
//        //get time log
//        String logTimestamp = DateTimeUtils.getCurrentDate();
//        String messageTimestamp = logTimestamp;
//        int status = responseSelvet.getStatus();
//        String responseStatus = Integer.toString(status);
//        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
//
//        if(!allProducts.isEmpty()) {
//            totalPage = (int) Math.ceil((double) allProducts.getTotalElements()/size);
//
//            //set response data to client
//            response.setResponseId(queryAllProductRequestBaseDetail.getRequestId());
//            response.setDetail(allProducts.getContent());
//            response.setPage(page);
//            response.setTotalPage(totalPage);
//            response.setTotal(allProducts.getTotalElements());
//
//            response.setResponseTime(DateTimeUtils.getCurrentDate());
//            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//            response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
//
//            String responseBody = mapper.writeValueAsString(response);
//            JSONObject transactionDetailResponse = new JSONObject(responseBody);
//
//            //calculate time duration
//            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);
//
//            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductRequestBaseDetail.getRequestId(), queryAllProductRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
//                    messageTimestamp, "productservice", "1", timeDurationResponse,
//                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
//                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
//            logService.createSOALog2(soaObject);
//
//        }else{
//            //set response data to client
//            response.setResponseId(queryAllProductRequestBaseDetail.getRequestId());
//            response.setResponseTime(DateTimeUtils.getCurrentDate());
//            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//            response.setResultMessage("This provider has no product");
//
//            String responseBody = mapper.writeValueAsString(response);
//            JSONObject transactionDetailResponse = new JSONObject(responseBody);
//
//            //calculate time duration
//            String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTime);
//
//            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductRequestBaseDetail.getRequestId(), queryAllProductRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
//                    messageTimestamp, "productservice", "1", timeDurationResponse,
//                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
//                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
//            logService.createSOALog2(soaObject);
//        }
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
