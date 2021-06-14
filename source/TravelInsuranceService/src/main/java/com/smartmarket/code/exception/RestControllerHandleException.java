package com.smartmarket.code.exception;

import com.google.gson.Gson;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.service.BICTransactionService;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.AntPathMatcher;
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

    //Lỗi do nghiệp vụ. VD: sai tên trường.VD: OrderId--> Order
    //                      thiếu trường ID/refCode lúc get.
    //                      tạo trùng
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex , HttpServletRequest request, HttpServletResponse responseSelvet) throws IOException {
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

        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response", "response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "Client",
                messageTimestamp, "travelinsuranceservice ", "1", timeDuration,
                "response", response.toString(), responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        //add BICTransaction
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String detailObject = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);
            String orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference();
            String orderId =  "-1";
            String customerName="DSVN";
            String phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
            String email  = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
            String ordPaidMoney =createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
            String consumerId = "DSVN";
            String fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate();
            String toDate = createTravelInsuranceBICRequest.getTrv().getToDate();
            String resultCode =ResponseCode.CODE.ERROR_IN_BACKEND;
            String bicResultCode = ex.getHttpStatus().toString();
            String ordDate= createTravelInsuranceBICRequest.getOrders().getOrdDate() ;
            String productId =createTravelInsuranceBICRequest.getOrders().getProductId();
            String customerAddress ="default";

            bicTransactionService.createBICTransactionParameter(messasgeId,orderReference,orderId,customerName,
                                                                phoneNumber,email,ordPaidMoney,consumerId,
                                                                fromDate,toDate,new Date(),resultCode,bicResultCode,
                                                                ordDate,productId,customerAddress) ;
        }


        return new ResponseEntity<>(response, ex.getHttpStatus());
    }


    //Lỗi kỹ thuật khi gọi --> BIC. VD: sai IP
    @ExceptionHandler({APIResponseException.class})
    public ResponseEntity<?> handleAPIException(APIResponseException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
//        response.setResultCode(ex.getResultCode());
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
//        response.setResultMessage(ex.getResultMessage());
        response.setResponseId(ex.getResponseId());
        response.setDetailErrorCode(ex.getDetailErrorCode());
        response.setDetailErrorMessage(ex.getDetailErrorMessage());
        response.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response","response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());


        //add BICTransaction
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String detailObject = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));

            //convert jsonobject to CreateTravelInsuranceBICRequest
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);

            String orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference();
            String orderId =  "-1";
            String customerName="DSVN";
            String phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
            String email  = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
            String ordPaidMoney =createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
            String consumerId = "DSVN";
            String fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate();
            String toDate = createTravelInsuranceBICRequest.getTrv().getToDate();
            String resultCode =ResponseCode.CODE.ERROR_IN_BACKEND;
            String bicResultCode = ex.getDetailErrorCode();
            String ordDate= createTravelInsuranceBICRequest.getOrders().getOrdDate() ;
            String productId =createTravelInsuranceBICRequest.getOrders().getProductId();
            String customerAddress ="default";

            bicTransactionService.createBICTransactionParameter(messasgeId,orderReference,orderId,customerName,
                    phoneNumber,email,ordPaidMoney,consumerId,
                    fromDate,toDate,new Date(),resultCode,bicResultCode,
                    ordDate,productId,customerAddress) ;
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Lỗi kỹ thuật khi gọi --> BIC. VD: BIC trả ra bị timeout
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<?> handleAPITimeOutException(APITimeOutRequestException ex , HttpServletRequest request,HttpServletResponse responseSelvet) throws IOException {
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
        int status = responseSelvet.getStatus();
        String responseStatus = Integer.toString(status);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","responseException",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs BIC
        TargetObject tarObject = new TargetObject("targetLog", messasgeId,"BIC", "response","response",
                transactionDetail, logTimestamp, messageTimestamp, timeDuration);
        logService.createTargetLog(tarObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), responseStatus, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        //add BICTransaction
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String detailObject = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));

            //convert jsonobject to CreateTravelInsuranceBICRequest
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);

            String orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference();
            String orderId =  "-1";
            String customerName="DSVN";
            String phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
            String email  = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
            String ordPaidMoney =createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
            String consumerId = "DSVN";
            String fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate();
            String toDate = createTravelInsuranceBICRequest.getTrv().getToDate();
            String resultCode =ResponseCode.CODE.ERROR_IN_BACKEND;
            String bicResultCode = HttpStatus.REQUEST_TIMEOUT.toString();
            String ordDate= createTravelInsuranceBICRequest.getOrders().getOrdDate() ;
            String productId =createTravelInsuranceBICRequest.getOrders().getProductId();
            String customerAddress ="default";

            bicTransactionService.createBICTransactionParameter(messasgeId,orderReference,orderId,customerName,
                    phoneNumber,email,ordPaidMoney,consumerId,
                    fromDate,toDate,new Date(),resultCode,bicResultCode,
                    ordDate,productId,customerAddress) ;
        }

        return new ResponseEntity<>(response, HttpStatus.REQUEST_TIMEOUT);

    }

    //Lỗi input ko đúng yêu cầu nghiệp vụ
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException ex , HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
        response.setResponseId(ex.getRequestId());
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage(ex.getMessage());

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        //add BICTransaction
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String detailObject = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));

            //convert jsonobject to CreateTravelInsuranceBICRequest
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);

            String orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? "-1" :createTravelInsuranceBICRequest.getOrders().getOrderReference()  ;
            String orderId =  "-1";
            String customerName="DSVN";
            String phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
            String email  = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
            String ordPaidMoney =createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
            String consumerId = "DSVN";
            String fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate();
            String toDate = createTravelInsuranceBICRequest.getTrv().getToDate();
            String resultCode =ResponseCode.CODE.ERROR_IN_BACKEND;
            String bicResultCode = HttpStatus.REQUEST_TIMEOUT.toString();
            String ordDate= createTravelInsuranceBICRequest.getOrders().getOrdDate() ;
            String productId =createTravelInsuranceBICRequest.getOrders().getProductId();
            String customerAddress ="default";

            bicTransactionService.createBICTransactionParameter(messasgeId,orderReference,orderId,customerName,
                    phoneNumber,email,ordPaidMoney,consumerId,
                    fromDate,toDate,new Date(),resultCode,bicResultCode,
                    ordDate,productId,customerAddress) ;
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    //Trường hợp sai format json request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> globalHttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,HttpServletRequest request, WebRequest webRequest) throws IOException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.INVALID_INPUT_DATA);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.INVALID_INPUT_DATA_MSG);
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage("Body request sai format json!");

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",null,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),messageTimestamp);
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",null, null, "BIC", "client",
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

        ReponseError response = new ReponseError();
        response.setResultCode(ResponseCode.CODE.FORMAT_MESSAGE_ERROR);
        response.setResponseTime(DateTimeUtils.getCurrentDate());
        response.setResultMessage(ResponseCode.MSG.FORMAT_MESSAGE_ERROR_MSG);
        response.setDetailErrorCode(HttpStatus.BAD_REQUEST.toString());
        response.setDetailErrorMessage("Lỗi xảy ra trong quá trình xử lý của hệ thống ");

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String messasgeId = requestBody.getString("requestId");
        String transactionDetail = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String timeDuration = Long.toString(elapsed);

//        if(ex.getCause() instanceof SocketTimeoutException) {
//            //logRequest vs BIC
//            TargetObject tarObjectRequest = new TargetObject("TargetLog", messasgeId, "BIC", "request", "request",
//                    transactionDetail, logTimestamp, messageTimestamp, null);
//            logService.createTargetLog(tarObjectRequest.getStringObject());
//        }

        //logException
        ServiceExceptionObject soaExceptionObject =
                new ServiceExceptionObject("serviceLog","response",messasgeId,null,
                        messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
                        request.getRemoteHost(), response.getResultMessage(),response.getResultCode(),
                        ex.getMessage(),logService.getIp(),requestBody.getString("requestTime"));
        logService.createSOALogException(soaExceptionObject.getStringObject());

        //logResponse vs Client
        ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "client",
                messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                "response", response.toString(), null, response.getResultCode(),
                response.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        //add BICTransaction
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String detailObject = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));

            //convert jsonobject to CreateTravelInsuranceBICRequest
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);

            String orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference();
            String orderId =  "-1";
            String customerName="DSVN";
            String phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
            String email  = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
            String ordPaidMoney =createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
            String consumerId = "DSVN";
            String fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate();
            String toDate = createTravelInsuranceBICRequest.getTrv().getToDate();
            String resultCode =ResponseCode.CODE.ERROR_IN_BACKEND;
            String bicResultCode = HttpStatus.REQUEST_TIMEOUT.toString();
            String ordDate= createTravelInsuranceBICRequest.getOrders().getOrdDate() ;
            String productId =createTravelInsuranceBICRequest.getOrders().getProductId();
            String customerAddress ="default";

            bicTransactionService.createBICTransactionParameter(messasgeId,orderReference,orderId,customerName,
                    phoneNumber,email,ordPaidMoney,consumerId,
                    fromDate,toDate,new Date(),resultCode,bicResultCode,
                    ordDate,productId,customerAddress) ;
        }


        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
