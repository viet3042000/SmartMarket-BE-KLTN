package com.smartmarket.code.exception;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestControllerHandleException {

    private Logger logger = LoggerFactory.getLogger(RestControllerHandleException.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex , HttpServletRequest request){

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getHttpStatus().toString());
        response.setDetailErrorMessage(ex.getMessage());

        return new ResponseEntity<>(response, ex.getHttpStatus());

    }

    @ExceptionHandler(APIResponseException.class)
    public ResponseEntity<?> handleAPIException(APIResponseException ex , HttpServletRequest request){

        ReponseError response = new ReponseError();
        response.setResultCode(ex.getResultCode());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ex.getResultMessage());
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HandleResponseException.class)
    public ResponseEntity<?> handleResponseException(HandleResponseException ex){

        BaseResponse response = new BaseResponse();
//        response.setResultCode(Integer.parseInt(ResponseCode. ));
        response.setResponseTime(DateTimeUtils.getCurrentDate());


        return new ResponseEntity<>(response, ex.getHttpStatus());

    }


}
