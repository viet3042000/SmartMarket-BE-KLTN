package com.smartmarket.code.exception;

import com.smartmarket.code.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerHandleException {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex){

        Response response = new Response();
        response.setCode(ex.getHttpStatus().value());
        response.setData(ex.getMessage());

        return new ResponseEntity<>(response, ex.getHttpStatus());

    }


}
