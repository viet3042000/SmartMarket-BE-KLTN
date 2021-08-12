package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.response.ResponseError;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.SetResponseUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    LogServiceImpl logService;

    @Autowired
    SetResponseUtils setResponseUtils ;

    @Autowired
    ConfigurableEnvironment environment;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        long startTime = DateTimeUtils.getStartTimeFromRequest(httpServletRequest);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;
        int status = httpServletResponse.getStatus();
        String responseStatus = Integer.toString(status);

        HttpServletResponse response = httpServletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

        httpServletRequest = new RequestWrapper(httpServletRequest);
        String jsonString = IOUtils.readInputStreamToString(httpServletRequest.getInputStream());

        String requestURL = httpServletRequest.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf("v1" + "/") + 3, requestURL.length());

        try {
            ResponseError responseError = new ResponseError();
            response.addHeader("WWW-Authenticate", "Authorized failed ");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";"+ "charset=UTF-8" );

            ObjectMapper mapper = new ObjectMapper();

            if(Utils.isJSONValid(jsonString)){
                JSONObject requestBody = new JSONObject(jsonString);
                String requestId = requestBody.getString("requestId");
                String requestTime = requestBody.getString("requestTime");
                responseError =  setResponseUtils.setResponseCustomEntryPoint(responseError,requestId) ;

                String responseBody = mapper.writeValueAsString(responseError);
                JSONObject transactionDetailResponse = new JSONObject(responseBody);

                //logException
                ServiceExceptionObject soaExceptionObject =
                        new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,null,
                                messageTimestamp, "orderservice", httpServletRequest.getRequestURI(),"1",
                                httpServletRequest.getRemoteHost(), responseError.getResultMessage(),responseError.getResultCode(),
                                responseError.getDetailErrorMessage(),Utils.getClientIp(httpServletRequest));
                logService.createSOALogException(soaExceptionObject);

                //get time duration
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, null, "smartMarket", "client",
                        messageTimestamp, "orderservice ", "1", timeDuration,
                        "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                        responseError.getResultMessage(), logTimestamp, httpServletRequest.getRemoteHost(),Utils.getClientIp(httpServletRequest),operationName);
                logService.createSOALog2(soaObject);

                response.getOutputStream()
                        .println(objectMapper.writeValueAsString(responseError));
            }else{
                response.getOutputStream()
                        .println(objectMapper.writeValueAsString(responseError));
            }

        }
        catch (Exception ex) {


            String requestId = "cannot get requestId";
            if(Utils.isJSONValid(jsonString)){
                JSONObject requestBody = new JSONObject(jsonString);
                 requestId = requestBody.getString("requestId");
            }

            //set response to client
            ResponseError res = new ResponseError();
            res.setResponseId(requestId);
            res.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            res.setResponseTime(DateTimeUtils.getCurrentDate());
            res.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            res.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            res.setDetailErrorMessage("Authorized failed");

            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(res));

            ObjectMapper mapper = new ObjectMapper();
            String responseBody = mapper.writeValueAsString(res);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",null,null,null,
                            messageTimestamp, "orderservice", httpServletRequest.getRequestURI(),"1",
                            httpServletRequest.getRemoteHost(), res.getResultMessage(),res.getResultCode(),
                            Throwables.getStackTraceAsString(ex),Utils.getClientIp(httpServletRequest));
            logService.createSOALogException(soaExceptionObject);

            //get time duration
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",null, null, null, "smartMarket", "client",
                    messageTimestamp, "orderservice ", "1", timeDuration,
                    "response", transactionDetailResponse, responseStatus, res.getResultCode(),
                    res.getResultMessage(), logTimestamp, httpServletRequest.getRemoteHost(),Utils.getClientIp(httpServletRequest),operationName);
            logService.createSOALog2(soaObject);
        }
    }
}