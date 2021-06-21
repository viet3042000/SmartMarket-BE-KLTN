package com.smartmarket.code.exception;

import com.google.common.base.Throwables;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.SetResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestControllerHandleException {

    private Logger logger = LoggerFactory.getLogger(RestControllerHandleException.class);

    @Autowired
    LogServiceImpl logService;

    @Autowired
    BICTransactionService bicTransactionService ;

    @Autowired
    HostConstants hostConstants ;

    @Autowired
    SetResponseUtils setResponseUtils;

    @Autowired
    BICTransactionExceptionService bicTransactionExceptionService ;

    //Lỗi do nghiệp vụ. VD: sai tên trường.VD: OrderId--> Order
    //                      thiếu trường ID/refCode lúc get.
    //                      tạo trùng
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex , HttpServletRequest request, HttpServletResponse responseSelvet) throws IOException{
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response, ex);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);

        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");
        String transactionDetail = requestBody.toString();


        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request, ResponseCode.CODE.ERROR_IN_BACKEND, ex.getHttpStatus().toString());
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", null,requestId, requestTime,"response", "response",transactionDetail,
                logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "Client",null,
                messageTimestamp, "travelinsuranceservice ", "1", timeDuration,
                "response", response.toString(), responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }


    //Lỗi kỹ thuật khi gọi --> BIC. VD: sai IP (chưa kết nối được vs BIC)
    @ExceptionHandler({APIResponseException.class})
    public ResponseEntity<?> handleAPIException(APIResponseException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //add BICTransaction
        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.ERROR_IN_BACKEND , ex.getDetailErrorCode().toString()) ;
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, ex.getDetailErrorCode());
    }


    //Lỗi kỹ thuật khi gọi --> BIC. VD: BIC trả ra bị timeout --> không nhận được response từ BIC
    @ExceptionHandler(APIAccessException.class)
    public ResponseEntity<?> handleAPITimeOutException(APIAccessException ex , HttpServletRequest request, HttpServletResponse responseSelvet) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //add BICTransaction
        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.SOA_TIMEOUT_BACKEND , HttpStatus.REQUEST_TIMEOUT.toString()) ;
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"responseException",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getErrorDetail(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);

    }


    //Lỗi input ko đúng yêu cầu nghiệp vụ
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //add BICTransaction
        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.INVALID_INPUT_DATA , HttpStatus.BAD_REQUEST.toString()) ;
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp ;

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.put(fieldName, errorMessage);
        });

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,errors,requestId);

        //add BICTransaction
        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.INVALID_INPUT_DATA , HttpStatus.BAD_REQUEST.toString()) ;
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Trường hợp sai format json request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> globalHttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",null,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),messageTimestamp);
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",null, null, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    //jump into in first step if doesn't match with each exception above
    // Hiện tại đều chỉ bắt các lỗi do hệ thống.
    //VD: thừa dấu 1 phẩy ở cuối. (sai format body request)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> globalExceptionHandler(Exception ex,HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //add BICTransaction
        try {
            //add BICTransaction
            bicTransactionExceptionService.createBICTransactionFromRequest(request , ResponseCode.CODE.GENERAL_ERROR , HttpStatus.BAD_REQUEST.toString()) ;
        }catch(Exception e){
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);


            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                            request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                            Throwables.getStackTraceAsString(e),logService.getIp(),messageTimestamp);
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                    messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                    "response", response.toString(), null, response.getResultCode(),
                    response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ConnectDataBaseException.class)
    public ResponseEntity<?> globalJDBCConnectionExceptionHandler(ConnectDataBaseException ex,HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ReponseError response = new ReponseError();
        response = setResponseUtils.setResponse(response,ex);

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",null,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        Throwables.getStackTraceAsString(ex),logService.getIp(),messageTimestamp);
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",null, null, "BIC", "client",null,
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
