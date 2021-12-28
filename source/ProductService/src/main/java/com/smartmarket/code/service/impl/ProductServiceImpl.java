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
import com.smartmarket.code.request.entity.CurrentStepSaga;
import com.smartmarket.code.request.entity.StepDetail;
import com.smartmarket.code.request.entity.StepFlow;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.BaseResponseGetAll;
import com.smartmarket.code.response.DetailProductResponse;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.ProductService;
import com.smartmarket.code.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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

    @PersistenceContext
    EntityManager entityManager;


    //Provider-i
    public ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

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
        ApprovalFlow approvalFlow =  approvalFlowRepository.findApprovalFlow(productProviderName,"createProduct").orElse(null);
        if(approvalFlow ==null){
            throw new CustomException("approvalFlow doesn't exist", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        ObjectMapper mapper = new ObjectMapper();
        List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(approvalFlow.getStepDetail(), StepFlow[].class));

        Product newProduct = new Product();
        newProduct.setProductName(productName);
        newProduct.setType(createProductRequestBaseDetail.getDetail().getType());
        newProduct.setDesc(createProductRequestBaseDetail.getDetail().getDesc());
        newProduct.setPrice(createProductRequestBaseDetail.getDetail().getPrice());

        if(approvalFlow.getStepDetail() != null) {
            newProduct.setState(ProductState.PENDING);
        }else{
            newProduct.setState(ProductState.APPROVED);
        }
        newProduct.setCreatedLogtimestamp(new Date());
        newProduct.setProductProvider(productProviderName);
        newProduct.setCurrentSagaId(createProductRequestBaseDetail.getRequestId());
        productRepository.save(newProduct);

        ProductApprovalFlow productApprovalFlow = new ProductApprovalFlow();
        productApprovalFlow.setProductId(newProduct.getId());
        productApprovalFlow.setCreatedLogtimestamp(new Date());
        productApprovalFlow.setFlowName("createProduct");
        productApprovalFlow.setStepDetail(approvalFlow.getStepDetail());
        productApprovalFlowRepository.save(productApprovalFlow);

        SagaState newSagaState = new SagaState();
        newSagaState.setId(createProductRequestBaseDetail.getRequestId());
        newSagaState.setCurrentStep("");
        newSagaState.setStepState("");
        newSagaState.setAggregateId(newProduct.getId().toString());
        newSagaState.setType(SagaType.APPROVE_CREATED_PRODUCT);
        newSagaState.setStatus(SagaStatus.STARTED);
        newSagaState.setCreatedLogtimestamp(new Date());
//            newSagaState.setPayload();
        sagaStateRepository.save(newSagaState);

        //create outbox
        Outbox outBox = new Outbox();
        outBox.setAggregateId(newProduct.getId().toString());
        outBox.setCreatedLogtimestamp(new Date());
        outBox.setAggregateType("Product");
        outBox.setType(OutboxType.WAITING_APPROVE);

        StepDetail stepDetail = new StepDetail();
        stepDetail.setRequestId(createProductRequestBaseDetail.getRequestId());
        stepDetail.setFlowName("createProduct");
        stepDetail.setStepName(stepFlows.get(0).getStepName());
        stepDetail.setRoleName(stepFlows.get(0).getRoleName());
        stepDetail.setStepNumber(stepFlows.get(0).getStepNumber());//index i --> stepNumber = i+1
        outBox.setPayload(new JSONObject(stepDetail).toString());
        outboxRepository.save(outBox);

        BaseResponse response = new BaseResponse();
        response.setDetail(newProduct);
        response.setResponseId(createProductRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Provider-i
//    @Transactional//begin transaction from begin of function and commit at the end of function by default
    public ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateProductRequest> updateProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

        String productName = updateProductRequestBaseDetail.getDetail().getProductName();

//        productRepository.beginTransaction();//begin transaction from here

        Product product = productRepository.findByProductName(productName).orElse(null);
//        Product product = productRepository.findAndLock(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        if( ProductState.PENDING.equals(product.getState()) || ProductState.DISAPPROVED.equals(product.getState()) ){
            throw new CustomException("Can't update this product", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

//       PESSIMISTIC_WRITE prevent other transactions from reading, updating or deleting the data.
//        entityManager.lock(product , LockModeType.PESSIMISTIC_WRITE);//= for update in sql
//        productRepository.commit();//commit transaction from here

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

    //Provider-i
    public ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteProductRequest> deleteProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception{
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

        String productName = deleteProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        if( ProductState.PENDING.equals(product.getState()) ){
            throw new CustomException("Can't delete this product", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String productProviderName = product.getProductProvider();
        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        productRepository.delete(product);
//        sagaStateRepository.deleteByProductId(Long.toString(product.getId()));
        productApprovalFlowRepository.deleteByProductId(product.getId());

        BaseResponse response = new BaseResponse();
        response.setDetail(product);
        response.setResponseId(deleteProductRequestBaseDetail.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Provider-i
    public ResponseEntity<?> getProduct(@Valid @RequestBody BaseDetail<QueryProductRequest>  queryProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException{
        //get time log
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;
        ObjectMapper mapper = new ObjectMapper();

        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

        String productName = queryProductRequestBaseDetail.getDetail().getProductName();
        Product product = productRepository.findByProductName(productName).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        if(ProductState.PENDING.equals(product.getState())){
            throw new CustomException("Can't get this product", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
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


//    //Provider
//    public ResponseEntity<?> getListProductOfProvider(@Valid @RequestBody BaseDetail<QueryAllProductOfProviderRequest> queryAllProductOfProviderRequestBaseDetail ,
//                                                      HttpServletRequest request,
//                                                      HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
//        //get user token
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
//        String userName = (String) claims.get("user_name");
//        User user = userRepository.findByUsername(userName).orElse(null);
//        if(user == null){
//            throw new CustomException("UserName doesn't exist in productService", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//        }
//
//        //check for user Provider
//        UserProductProvider userProductProvider = userProductProviderRepository.findByUserName(userName).orElse(null);
//        if(userProductProvider == null){
//            throw new CustomException("userName in claim doesn't exist in userProductProvider table", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
//        }
//
//        int totalPage = 0 ;
//        BaseResponseGetAll response = new BaseResponseGetAll();
//        ObjectMapper mapper = new ObjectMapper();
//        String hostName = request.getRemoteHost();
//
//        int page =  queryAllProductOfProviderRequestBaseDetail.getDetail().getPage()  ;
//        int size =  queryAllProductOfProviderRequestBaseDetail.getDetail().getSize()   ;
//        Pageable pageable = PageRequest.of(page-1, size);
//
//        //find by user name and type
//        Page<Product> allOrders =
//                productRepository.findByUserName(userName,pageable);
//
//        //get time log
//        String logTimestamp = DateTimeUtils.getCurrentDate();
//        String messageTimestamp = logTimestamp;
//        int status = responseSelvet.getStatus();
//        String responseStatus = Integer.toString(status);
//        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);
//
//        if(!allOrders.isEmpty()) {
//            totalPage = (int) Math.ceil((double) allOrders.getTotalElements()/size);
//
//            //set response data to client
//            response.setResponseId(queryAllProductOfProviderRequestBaseDetail.getRequestId());
//            response.setDetail(allOrders.getContent());
//            response.setPage(page);
//            response.setTotalPage(totalPage);
//            response.setTotal(allOrders.getTotalElements());
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
//            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductOfProviderRequestBaseDetail.getRequestId(), queryAllProductOfProviderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
//                    messageTimestamp, "productservice", "1", timeDurationResponse,
//                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
//                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
//            logService.createSOALog2(soaObject);
//
//        }else{
//            //set response data to client
//            response.setResponseId(queryAllProductOfProviderRequestBaseDetail.getRequestId());
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
//            ServiceObject soaObject = new ServiceObject("serviceLog", queryAllProductOfProviderRequestBaseDetail.getRequestId(), queryAllProductOfProviderRequestBaseDetail.getRequestTime(), null, "smartMarket", "client",
//                    messageTimestamp, "productservice", "1", timeDurationResponse,
//                    "response", transactionDetailResponse, responseStatus, response.getResultCode(),
//                    response.getResultMessage(), logTimestamp, hostName, Utils.getClientIp(request),"getAllOrder");
//            logService.createSOALog2(soaObject);
//        }
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    //Admin
    public ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail,
                                            HttpServletRequest request,
                                            HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
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
            int totalPage = (int) Math.ceil((double) allProducts.getTotalElements()/size);

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

        Long productId = approvePendingProductRequest.getDetail().getProductId();
        Product product = productRepository.findByProductId(productId).orElse(null);
        if(product == null){
            throw new CustomException("productName does not exist", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }
        String productProviderName = product.getProductProvider();

        Long productProviderId = productProviderRepository.getId(productProviderName);
        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findUser(userName,productProviderId).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        String currentState = product.getState();
        if(!ProductState.PENDING.equals(currentState)){
            throw new CustomException("Product's state isn't pending", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        SagaState sagaState = sagaStateRepository.findById(product.getCurrentSagaId()).orElse(null);
        Gson gson = new Gson();
        CurrentStepSaga currentStepSaga = gson.fromJson(sagaState.getCurrentStep(), CurrentStepSaga.class);
        int currentStep = currentStepSaga.getCurrentStep();//=index of stepFlows +1

        ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findProductApprovalFlow(approvePendingProductRequest.getDetail().getFlowName(),productId).orElse(null);
        ObjectMapper mapper = new ObjectMapper();
        List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));
        StepFlow stepFlow = stepFlows.get(currentStep-1);

        ArrayList<String> roles = authorizationService.getRoles();
        if(!roles.contains(stepFlow.getRoleName())){
            throw new CustomException("Role isn't accepted with current approval step ", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        //create outbox
        Outbox outBox = new Outbox();
        outBox.setAggregateId(productId.toString());
        outBox.setCreatedLogtimestamp(new Date());
        outBox.setAggregateType("Product");
        outBox.setType(OutboxType.APPROVE_CREATED_PRODUCT);

        StepDetail stepDetail = new StepDetail();
        stepDetail.setRequestId(product.getCurrentSagaId());
        stepDetail.setRoleName(stepFlow.getRoleName());
        stepDetail.setDecision(approvePendingProductRequest.getDetail().getDecision());
        stepDetail.setFlowName(approvePendingProductRequest.getDetail().getFlowName());
        stepDetail.setStepNumber(stepFlow.getStepNumber());
        outBox.setPayload(new JSONObject(stepDetail).toString());
        outboxRepository.save(outBox);

        BaseResponse response = new BaseResponse();
        response.setDetail(product);
        response.setResponseId(approvePendingProductRequest.getRequestId());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //Provider-i
    public ResponseEntity<?> getListPendingProduct(@Valid @RequestBody BaseDetail <QueryPendingProductRequest> queryPendingProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException{
        //get user token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);//claims != null because request passed through CustomAuthorizeRequestFilter
        String userName = (String) claims.get("user_name");

        //check for user Provider
        UserProductProvider userProductProvider = userProductProviderRepository.findByUserName(userName).orElse(null);
        if(userProductProvider == null){
            throw new CustomException("userProductProvider of username in claim doesn't exist", HttpStatus.BAD_REQUEST, queryPendingProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
        }

        ProductProvider productProvider = productProviderRepository.findByProductProviderId(userProductProvider.getProductProviderId()).orElse(null);
        List<Product> productList = productRepository.findPendingProduct(productProvider.getProductProviderName());
        BaseResponseGetAll response = new BaseResponseGetAll();
        if(productList.isEmpty()){
            response.setResponseId(queryPendingProductRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("No pending approval product with this username");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        ArrayList<String> roles = authorizationService.getRoles();
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
        for(Product product:productList){
            //product pending --> only 1 saga current
            SagaState sagaState = sagaStateRepository.findByAggregateId(Long.toString(product.getId())).orElse(null);
            CurrentStepSaga currentStepSaga = gson.fromJson(sagaState.getCurrentStep(), CurrentStepSaga.class);
            int currentStep = currentStepSaga.getCurrentStep();

            ProductApprovalFlow productApprovalFlow = productApprovalFlowRepository.findByProductId(product.getId()).orElse(null);
            List<StepFlow> stepFlows = Arrays.asList(mapper.readValue(productApprovalFlow.getStepDetail(), StepFlow[].class));
            StepFlow stepFlow = stepFlows.get(currentStep-1);
            if(!roles.contains(stepFlow.getRoleName())){
                productList.remove(product);
            }
        }

        int page =  queryPendingProductRequestBaseDetail.getDetail().getPage()  ;
        int size =  queryPendingProductRequestBaseDetail.getDetail().getSize()   ;
        if(!productList.isEmpty()) {
            int totalPage = (int) Math.ceil((double) productList.size() / size);
            response.setPage(page);
            response.setTotalPage(totalPage);

            List allPendingProduct = PagingUtil.getPageLimit(productList, page, size);
            if(allPendingProduct!=null) {
                response.setTotal(productList.stream().count());
                response.setDetail(allPendingProduct);
                response.setResponseId(queryPendingProductRequestBaseDetail.getRequestId());
                response.setResponseTime(DateTimeUtils.getCurrentDate());
                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
            }else {
                response.setTotal(0L);
                response.setResponseId(queryPendingProductRequestBaseDetail.getRequestId());
                response.setResponseTime(DateTimeUtils.getCurrentDate());
                response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                response.setResultMessage("No pending approval product");
            }
        }else {
            response.setResponseId(queryPendingProductRequestBaseDetail.getRequestId());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
            response.setResultMessage("No pending approval product with this username");
        }

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
