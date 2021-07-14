package com.smartmarket.code.util;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.QueryOpenDataRequest;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SetResponseUtils {
    //Inquery
    public BaseResponse setResponseInquery( BaseDetail<QueryOpenDataRequest> queryOpenDataRequest,BaseResponse response,
                                            Object queryOpenDataResponse){
        response.setDetail(queryOpenDataResponse);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResponseId(queryOpenDataRequest.getRequestId());
        response.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
        response.setResultMessage(ResponseCode.MSG.TRANSACTION_SUCCESSFUL_MSG);
        return response ;
    }

    //CustomException
    public ResponseError setResponseCustomException(ResponseError response, CustomException ex){
        response.setResultCode(ex.getHttpStatusCode());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ex.getErrorMessage());
//        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getHttpStatusDetailCode().toString());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        return response ;
    }

    //APIResponseException
    public ResponseError setResponseAPIResponseException(ResponseError response, APIResponseException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
//        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode().toString());

        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        return response ;
    }

//      APIAccessException
        public ResponseError setResponseAPIAccessException(ResponseError response, APIAccessException ex){
            response.setResultCode(ex.getResultCode());
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ex.getResultMessage());
            response.setDetailErrorCode(HttpStatus.REQUEST_TIMEOUT.toString());
            response.setDetailErrorMessage(ex.getDetailErrorMessage());
            return response ;
        }

//      InvalidInputException
        public ResponseError setResponseInvalidInputException(ResponseError response, InvalidInputException ex){
            response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
//            response.setResponseId(ex.getRequestId());
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage(ex.getMessage());
            return response ;
        }


    //MethodArgumentNotValidException
    public ResponseError setResponseMethodArgumentNotValidException(ResponseError response, Map<String, String> ex, String requestId){
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
//        response.setResponseId(requestId);
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage(ex.toString());
        return response ;
    }

//      HttpMessageNotReadableException
        public ResponseError setResponseHttpMessageNotReadableException(ResponseError response, HttpMessageNotReadableException ex){
            response.setResultCode(ResponseCode.CODE.FORMAT_MESSAGE_ERROR);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.FORMAT_MESSAGE_ERROR_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("Format body is not JSON !");
            return response ;
        }

//      Exception
        public ResponseError setResponseException(ResponseError response, Exception ex){
            response.setResultCode(ResponseCode.CODE.GENERAL_ERROR);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.GENERAL_ERROR_MSG);
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage("An error occurred during the processing of the system!");
            return response ;
        }

    //  ConnectDataBaseException
        public ResponseError setResponseConnectDataBaseException(ResponseError response, ConnectDataBaseException ex){
            response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
            response.setResponseTime(DateTimeUtils.getCurrentDate());
            response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
            response.setDetailErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.toString());
            response.setDetailErrorMessage("An error occurred during the processing of the system!");
            return response ;
        }

//      CustomEntryPoint
        public ResponseError setResponseCustomEntryPoint(ResponseError responseError){
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage("Authorized failed ");
            return responseError ;
        }

}
