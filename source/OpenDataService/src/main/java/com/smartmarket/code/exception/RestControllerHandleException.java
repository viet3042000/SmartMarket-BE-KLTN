package com.smartmarket.code.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.response.ResponseError;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.SetResponseUtils;
import org.json.JSONObject;
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
    HostConstants hostConstants;

    @Autowired
    SetResponseUtils setResponseUtils;

    //Lỗi do nghiệp vụ. VD: sai tên trường.VD: OrderId--> Order
    //                      thiếu trường ID/refCode lúc get.
    //                      tạo trùng
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex, HttpServletRequest request, HttpServletResponse responseSelvet) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseCustomException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);

        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        String requestURL = request.getRequestURL().toString();
        String targetService = requestURL.substring(requestURL.indexOf("v1/") + 3, requestURL.length());

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        //calculate time duration
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice ", "1", timeDurationResponse,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, ex.getHttpStatusDetailCode());
    }


    //Lỗi kỹ thuật khi gọi --> OpenData. VD: sai IP (chưa kết nối được vs OpenData)
    @ExceptionHandler({APIResponseException.class})
    public ResponseEntity<?> handleAPIException(APIResponseException ex, HttpServletRequest request) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseAPIResponseException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog", "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        //calculate time duration
        String timeDurationResponse = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDurationResponse,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, ex.getDetailErrorCode());
    }


    //Lỗi kỹ thuật khi gọi --> OpenData. VD: OpenData trả ra bị timeout --> không nhận được response từ OpenData
    @ExceptionHandler(APIAccessException.class)
    public ResponseEntity<?> handleAPITimeOutException(APIAccessException ex, HttpServletRequest request, HttpServletResponse responseSelvet) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseAPIAccessException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);


        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);

    }


    //Lỗi input ko đúng yêu cầu nghiệp vụ
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException ex, HttpServletRequest request) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);
        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseInvalidInputException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        String logTimestamp = DateTimeUtils.getCurrentDate();
        String messageTimestamp = logTimestamp;

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

        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseMethodArgumentNotValidException(response, errors, requestId);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Trường hợp sai format json request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> globalHttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex, HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseHttpMessageNotReadableException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);


        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", null, null, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", null, null, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // loi connect DB
    @ExceptionHandler(ConnectDataBaseException.class)
    public ResponseEntity<?> globalJDBCConnectionExceptionHandler(ConnectDataBaseException ex, HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseConnectDataBaseException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", null, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp(), messageTimestamp);
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = Long.toString(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", null, null, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    //jump into in first step if doesn't match with each exception above
    // Hiện tại đều chỉ bắt các lỗi do hệ thống.
    //VD: thừa dấu 1 phẩy ở cuối. (sai format body request)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTimeLogFilter = DateTimeUtils.getStartTimeFromRequest(request);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        //set response to client
        ResponseError response = new ResponseError();
        response = setResponseUtils.setResponseException(response, ex);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(response);
        JSONObject transactionDetailResponse = new JSONObject(responseBody);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        EJson requestBody = new EJson(jsonString);
        String requestId = requestBody.getString("requestId");
        String requestTime = requestBody.getString("requestTime");


        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject(Constant.EXCEPTION_LOG, "response", requestId, requestTime, null,
                        messageTimestamp, "opendataservice", request.getRequestURI(), "1",
                        request.getRemoteHost(), response.getResultMessage(), response.getResultCode(),
                        Throwables.getStackTraceAsString(ex), logService.getIp());
        logService.createSOALogException(soaExceptionObject);

        String timeDuration = DateTimeUtils.getElapsedTimeStr(startTimeLogFilter);

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime, "OpenData", "smartMarket", "client",
                messageTimestamp, "opendataservice", "1", timeDuration,
                "response", transactionDetailResponse, null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(), logService.getIp());
        logService.createSOALog2(soaObject);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
