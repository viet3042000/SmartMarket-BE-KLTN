package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.TravelInsuranceService;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.ConnectException;
import java.util.ArrayList;


//@RefreshScope
@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class ApiBICController {

//    @Autowired
//    private StartTimeBean startTimeBeanProvider;

    @Autowired
    TravelInsuranceService travelInsuranceService ;

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    BICTransactionExceptionService bicTransactionExceptionService;

//@PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
    @PostMapping(value = "/create-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createTravelBIC(@Valid @RequestBody(required = true) BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return travelInsuranceService.createTravelBIC(createTravelInsuranceBICRequest, request, responseSelvet);
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, createTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, createTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        bicTransactionExceptionService.createPendingBICTransactionFromRequest(request,"createTravelInsuranceBIC");

                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        bicTransactionExceptionService.createPendingBICTransactionFromRequest(request,"createTravelInsuranceBIC");
                        throw new APIAccessException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, httpClientErrorException.getStatusCode().toString());
                    throw new APIResponseException(createTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.INVALID_INPUT_DATA, HttpStatus.BAD_REQUEST.toString());
                    throw new InvalidInputException(ex.getMessage(), createTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, customException.getHttpStatusCode().toString());
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), createTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                }else {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.GENERAL_ERROR , HttpStatus.BAD_REQUEST.toString()) ;
                    throw ex ;
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }


    @PostMapping(value = "/inquire-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getTravelBIC(@Valid @RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try {
            ArrayList<String> roles = authorizationService.getRoles();
            if (roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return travelInsuranceService.getTravelBIC(queryTravelInsuranceBICRequest, request, responseSelvet);
                } else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, queryTravelInsuranceBICRequest.getRequestId(), null, null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {
            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        throw new APIAccessException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    throw new APIResponseException(queryTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    throw new InvalidInputException(ex.getMessage(), queryTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), queryTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                } else {
                    throw ex ;
                }

            } catch (JDBCConnectionException jdbcConnect) {
                if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }else {
                    throw ex ;
                }
            }
        }
    }

    @PostMapping(value = "/change-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateTravelBIC(@Valid @RequestBody BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APIAccessException {
        try{
            ArrayList<String> roles = authorizationService.getRoles();
            if(roles != null) {
                if (roles.contains("CUSTOMER")) {
                    return travelInsuranceService.updateTravelBIC(updateTravelInsuranceBICRequest,request,responseSelvet) ;
                }else {
                    throw new CustomException("Roles of this user is not accepted", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
                }
            }else {
                throw new CustomException("Roles is Null", HttpStatus.BAD_REQUEST, updateTravelInsuranceBICRequest.getRequestId(),null,null, null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex) {

            try {
                //catch truong hop chua goi dc sang BIC
                if (ex instanceof ResourceAccessException) {
                    ResourceAccessException resourceAccessException = (ResourceAccessException) ex;
                    if (resourceAccessException.getCause() instanceof ConnectException) {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.SOA_TIMEOUT_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        bicTransactionExceptionService.createPendingBICTransactionFromRequest(request,"updateTravelInsuranceBIC");
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.SOA_TIMEOUT_BACKEND, ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    } else {
                        bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, HttpStatus.REQUEST_TIMEOUT.toString());
                        bicTransactionExceptionService.createPendingBICTransactionFromRequest(request,"updateTravelInsuranceBIC");
                        throw new APIAccessException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, resourceAccessException.getMessage(), Throwables.getStackTraceAsString(resourceAccessException));
                    }
                }

                //catch truong hop goi dc sang BIC nhưng loi
                else if (ex instanceof HttpClientErrorException) {
                    HttpClientErrorException httpClientErrorException = (HttpClientErrorException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, httpClientErrorException.getStatusCode().toString());
                    throw new APIResponseException(updateTravelInsuranceBICRequest.getRequestId(), ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, httpClientErrorException.getStatusCode(), httpClientErrorException.getResponseBodyAsString());
                }

                //catch invalid input exception
                else if (ex instanceof InvalidInputException) {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.INVALID_INPUT_DATA, HttpStatus.BAD_REQUEST.toString());
                    throw new InvalidInputException(ex.getMessage(), updateTravelInsuranceBICRequest.getRequestId());
                }

                //catch truong hop loi kết nối database
                else if (ex.getCause() instanceof JDBCConnectionException) {
                    throw new ConnectDataBaseException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else if (ex instanceof CustomException){
                    CustomException customException = (CustomException) ex;
                    bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, customException.getHttpStatusCode());
                    throw new CustomException(customException.getDetailErrorMessage(), customException.getHttpStatusDetailCode(), updateTravelInsuranceBICRequest.getRequestId(), customException.getResponseBIC(), customException.getHttpStatusCode() , customException.getErrorMessage(),customException.getHttpStatusHeader());
                }
                else {
                    bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.GENERAL_ERROR , HttpStatus.BAD_REQUEST.toString()) ;
                    throw ex ;
                }
            } catch (JDBCConnectionException jdbcConnect) {
                throw new ConnectDataBaseException(jdbcConnect.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
