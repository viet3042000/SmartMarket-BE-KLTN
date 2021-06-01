package com.smartmarket.code.exception;

import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerHandleException {

    private Logger logger = LoggerFactory.getLogger(RestControllerHandleException.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex){

        BaseResponse response = new BaseResponse();
        response.setResultCode(ex);
        response.setData(ex.getMessage());

        logger.error();


        return new ResponseEntity<>(response, ex.getHttpStatus());

    }


}
