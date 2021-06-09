package com.smartmarket.code.exception;

import com.smartmarket.code.model.entitylog.SoaExceptionObject;
import com.smartmarket.code.model.entitylog.SoaObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestControllerAdvice
public class RestControllerHandleException {

    private Logger logger = LoggerFactory.getLogger(RestControllerHandleException.class);

    @Autowired
    LogServiceImpl logService;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getHttpStatus().toString());
        response.setDetailErrorMessage(ex.getMessage());

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        SoaExceptionObject soaExceptionObject =
                new SoaExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response", "response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        SoaObject soaObject = new SoaObject("serviceLog",messasgeId, null, "BIC", "Client",
                messageTimestamp, "travelinsuranceservice ", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, ex.getHttpStatus());

    }

    @ExceptionHandler(APIResponseException.class)
    public ResponseEntity<?> handleAPIException(APIResponseException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ex.getResultCode());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ex.getResultMessage());
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        SoaExceptionObject soaExceptionObject =
                new SoaExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response","response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        SoaObject soaObject = new SoaObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<?> handleAPITimeOutException(APITimeOutRequestException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.SOA_TIMEOUT_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG);
        response.setDetailErrorCode(HttpStatus.REQUEST_TIMEOUT.toString());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        SoaExceptionObject soaExceptionObject =
                new SoaExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response","response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        SoaObject soaObject = new SoaObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);

    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleException(APIResponseException ex , HttpServletRequest request){
//
//        ReponseError response = new ReponseError();
//        response.setResultCode(ex.getResultCode());
//        response.setResponseTime(DateTimeUtils.getCurrentDate());
//        response.setResultMessage(ex.getResultMessage());
//        response.setResponseId(ex.getResponseId());
//        response.setDetailErrorCode(ex.getDetailErrorCode());
//        response.setDetailErrorMessage(ex.getDetailErrorMessage());
//
//
//        if (ex instanceof TimeoutException){
//
//        }
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//
//    }


//    @ExceptionHandler(HandleResponseException.class)
//    public ResponseEntity<?> handleResponseException(HandleResponseException ex){
//
//        BaseResponse response = new BaseResponse();
////        response.setResultCode(Integer.parseInt(ResponseCode. ));
//        response.setResponseTime(DateTimeUtils.getCurrentDate());
//
//
//        return new ResponseEntity<>(response, ex.getHttpStatus());
//
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> globalExceptionHandler(Exception ex,HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
        response.setDetailErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.setDetailErrorMessage("Lỗi xảy ra trong quá trình xử lý của hệ thống ");

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        SoaExceptionObject soaExceptionObject =
                new SoaExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog",messasgeId,"BIC", "response","response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        SoaObject soaObject = new SoaObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
