package com.smartmarket.code.exception;

import org.springframework.http.HttpStatus;
import java.sql.SQLException;


public class ConnectDataBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private HttpStatus httpStatus;

    public ConnectDataBaseException(String message){
        super(message);
    }

    public ConnectDataBaseException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
