package com.smartmarket.code.util;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.*;
import com.smartmarket.code.response.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SetResponseUtils {
    //CustomException
    public ResponseError setResponseCustomException(ResponseError response, CustomException ex){
        response.setResultCode(ex.getHttpStatusCode());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ex.getErrorMessage());
        response.setResponseId(ex.getResponseId());
        if(ex.getHttpStatusDetailCode() != HttpStatus.OK){
            response.setDetailErrorCode(ex.getHttpStatusDetailCode().toString());
        }else {
            response.setDetailErrorCode("");
        }

        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        return response ;
    }

    //APIResponseException
    public ResponseError setResponseAPIResponseException(ResponseError response, APIResponseException ex){
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
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
            response.setResponseId(ex.getRequestId());
            response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
            response.setDetailErrorMessage(ex.getMessage());
            return response ;
        }


    //MethodArgumentNotValidException
    public ResponseError setResponseMethodArgumentNotValidException(ResponseError response, Map<String, String> ex, String requestId){
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
        response.setResponseId(requestId);
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
    //      HttpMessageNotReadableException
    public ResponseError setResponseInvalidFormatJsonException(ResponseError response, InvalidFormatException ex){
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage(ex.getMessage());
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
        public ResponseError setResponseCustomEntryPoint(ResponseError responseError,String requestId){
            responseError.setResponseId(requestId);
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage("Authorized failed ");
            return responseError ;
        }

        //OrderService
        public ResponseError setResponseErrorOrderService(String orderId, String message, String requestId, String requestTime){
            ResponseError responseError = new ResponseError();
            responseError.setOrderId(orderId);
            responseError.setResponseId(requestId);
            responseError.setResponseTime(requestTime);
            responseError.setResultCode(ResponseCode.CODE.TRANSACTION_REFUSED);
            responseError.setResultMessage(message);
            return responseError;
        }

}
