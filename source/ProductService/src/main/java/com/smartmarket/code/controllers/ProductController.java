package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.ProductService;
import org.apache.kafka.common.protocol.types.Field;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/product/product-service/v1/")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    AuthorizationService authorizationService;


    //Provider-i
    @PostMapping(value = "/create-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createProduct(@Valid @RequestBody BaseDetail<CreateProductRequest> createProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException, ParseException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.createProduct(createProductRequestBaseDetail, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {

            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(createProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(createProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(createProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), createProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

    }

    //Provider-i
    @PostMapping(value = "/update-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateProduct(@Valid @RequestBody BaseDetail<UpdateProductRequest> updateProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.updateProduct(updateProductRequestBaseDetail, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, updateProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            if(ex instanceof NullPointerException){
                throw new NullPointerException(ex.getMessage());
            }

            //catch truong hop chua goi dc sang BIC
            else if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(updateProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(updateProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(updateProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), updateProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

    }

    // Provider-i
    @PostMapping(value = "/delete-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> deleteProduct(@Valid @RequestBody BaseDetail<DeleteProductRequest> deleteProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws Exception {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.deleteProduct(deleteProductRequestBaseDetail, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, deleteProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {

            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(deleteProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(deleteProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(deleteProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), deleteProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), deleteProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

    }


    //Provider-i
    @RequestMapping(value = "/get-product")
    public ResponseEntity<?> getProduct(@Valid @RequestBody BaseDetail<QueryProductRequest> queryProductRequestBaseDetail, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.getProduct(queryProductRequestBaseDetail, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {

            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    //Provider
//    @PostMapping(value = "/get-list-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<?> getListProductOfProvider(@Valid @RequestBody BaseDetail<QueryAllProductOfProviderRequest> queryAllProductOfProviderRequestBaseDetail ,
//                                                      HttpServletRequest request,
//                                                      HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
//        try{
//            ArrayList<String> roles = authorizationService.getRoles();
//            if(roles != null) {
//                if (roles.contains("ADMIN")||roles.contains("PROVIDER")) {
//                    return productService.getListProductOfProvider(queryAllProductOfProviderRequestBaseDetail, request, responseSelvet);
//                }else {
//                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//                }
//            }else {
//                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryAllProductOfProviderRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//            }
//        }catch (Exception ex) {
//            //catch truong hop chua goi dc sang BIC
//            if (ex instanceof ResourceAccessException) {
//                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
//                if (resourceAccessException.getCause() instanceof ConnectException) {
//                    throw new APIAccessException(queryAllProductOfProviderRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
//                } else {
//                    throw new APIAccessException(queryAllProductOfProviderRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
//                }
//            }
//
//            //catch truong hop goi dc sang BIC nhưng loi
//            else if (ex instanceof HttpClientErrorException) {
//                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
//                throw new APIResponseException(queryAllProductOfProviderRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
//            }
//
//            //catch invalid input exception
//            else if (ex instanceof InvalidInputException) {
//                throw new InvalidInputException(ex.getMessage(), queryAllProductOfProviderRequestBaseDetail.getRequestId());
//            }
//
//            //catch truong hop loi kết nối database
//            else if (ex.getCause() instanceof JDBCConnectionException) {
//                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            } else if (ex instanceof CustomException) {
//                CustomException customException = (CustomException) ex;
//                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryAllProductOfProviderRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
//            } else {
//                throw ex;
//            }
//        }
//
//    }


    //Admin
    @PostMapping(value = "/get-all-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListProduct(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail ,
                                            HttpServletRequest request,
                                            HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("ADMIN")) {
                    return productService.getListProduct(queryAllProductRequestBaseDetail, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryAllProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryAllProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryAllProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryAllProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }

    }


    //Provider i
    @PostMapping(value = "/approve-pending-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> approvePendingProduct(@Valid @RequestBody BaseDetail<ApprovePendingProductRequest> approvePendingProductRequest ,
                                            HttpServletRequest request,
                                            HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.approvePendingProduct(approvePendingProductRequest, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, approvePendingProductRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(approvePendingProductRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(approvePendingProductRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(approvePendingProductRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), approvePendingProductRequest.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), approvePendingProductRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }

    //Provider i
    @PostMapping(value = "/get-pending-product", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListPendingProduct(@Valid @RequestBody BaseDetail<QueryPendingProductRequest> queryPendingProductRequestBaseDetail ,
                                                   HttpServletRequest request,
                                                   HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            ArrayList<String> listRoles = new ArrayList<>(Arrays.asList("PROVIDER","PROVIDER1", "PROVIDER2", "PROVIDER3", "PROVIDER4","PROVIDER5"));
            if(roles != null) {
                for(int i =0; i<listRoles.size();i++) {
                    String role = listRoles.get(i);
                    if (!roles.contains(role)) {
                        if(i == listRoles.size()-1) {
                            throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryPendingProductRequestBaseDetail.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        break;
                    }
                }
                return productService.getListPendingProduct(queryPendingProductRequestBaseDetail, request, responseSelvet);
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryPendingProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            //catch truong hop chua goi dc sang BIC
            if (ex instanceof ResourceAccessException) {
                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                if (resourceAccessException.getCause() instanceof ConnectException) {
                    throw new APIAccessException(queryPendingProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                } else {
                    throw new APIAccessException(queryPendingProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                }
            }

            //catch truong hop goi dc sang BIC nhưng loi
            else if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                throw new APIResponseException(queryPendingProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
            }

            //catch invalid input exception
            else if (ex instanceof InvalidInputException) {
                throw new InvalidInputException(ex.getMessage(), queryPendingProductRequestBaseDetail.getRequestId());
            }

            //catch truong hop loi kết nối database
            else if (ex.getCause() instanceof JDBCConnectionException) {
                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (ex instanceof CustomException) {
                CustomException customException = (CustomException) ex;
                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryPendingProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
            } else {
                throw ex;
            }
        }
    }


    //Admin
//    @PostMapping(value = "/get-all-product-by-state", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    ResponseEntity<?> getListByState(@Valid @RequestBody BaseDetail<QueryAllProductRequest> queryAllProductRequestBaseDetail,
//                                            HttpServletRequest request, HttpServletResponse responseSelvet)
//            throws JsonProcessingException, APIAccessException {
//        try{
//            ArrayList<String> roles = authorizationService.getRoles();
//            if(roles != null) {
//                if (roles.contains("ADMIN")) {
//                    return productService.getListByState(queryAllProductRequestBaseDetail, request, responseSelvet);
//                }else {
//                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryAllProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//                }
//            }else {
//                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryAllProductRequestBaseDetail.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
//            }
//        }catch (Exception ex) {
//            //catch truong hop chua goi dc sang BIC
//            if (ex instanceof ResourceAccessException) {
//                ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
//                if (resourceAccessException.getCause() instanceof ConnectException) {
//                    throw new APIAccessException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
//                } else {
//                    throw new APIAccessException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
//                }
//            }
//
//            //catch truong hop goi dc sang BIC nhưng loi
//            else if (ex instanceof HttpClientErrorException) {
//                HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
//                throw new APIResponseException(queryAllProductRequestBaseDetail.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
//            }
//
//            //catch invalid input exception
//            else if (ex instanceof InvalidInputException) {
//                throw new InvalidInputException(ex.getMessage(), queryAllProductRequestBaseDetail.getRequestId());
//            }
//
//            //catch truong hop loi kết nối database
//            else if (ex.getCause() instanceof JDBCConnectionException) {
//                throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            } else if (ex instanceof CustomException) {
//                CustomException customException = (CustomException) ex;
//                throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryAllProductRequestBaseDetail.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode(), customException.getErrorMessage(), customException.getHttpStatusHeader());
//            } else {
//                throw ex;
//            }
//        }
//
//    }

}
