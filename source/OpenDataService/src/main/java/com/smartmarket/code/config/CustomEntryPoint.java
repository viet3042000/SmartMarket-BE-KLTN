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

        try {
            ResponseError responseError = new ResponseError();
            responseError =  setResponseUtils.setResponseCustomEntryPoint(responseError) ;
            response.addHeader("WWW-Authenticate", "Authorized failed ");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";"+ "charset=UTF-8" );
            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(responseError));
            ObjectMapper mapper = new ObjectMapper();
            String responseBody = mapper.writeValueAsString(response);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            httpServletRequest = new RequestWrapper(httpServletRequest);
            String jsonString = IOUtils.readInputStreamToString(httpServletRequest.getInputStream());
            if(Utils.isJSONValid(jsonString)){
                JSONObject requestBody = new JSONObject(jsonString);
                String requestId = requestBody.getString("requestId");
                String requestTime = requestBody.getString("requestTime");

                //logException
                ServiceExceptionObject soaExceptionObject =
                        new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",requestId,requestTime,null,
                                messageTimestamp, "opendataservice", httpServletRequest.getRequestURI(),"1",
                                httpServletRequest.getRemoteHost(), responseError.getResultMessage(),responseError.getResultCode(),
                                responseError.getDetailErrorMessage(),logService.getIp());
                logService.createSOALogException(soaExceptionObject);

                //get time duration
                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog",requestId, requestTime, null, "smartMarket", "client",
                        messageTimestamp, "opendataservice ", "1", timeDuration,
                        "response", transactionDetailResponse, responseStatus, responseError.getResultCode(),
                        responseError.getResultMessage(), logTimestamp, httpServletRequest.getRemoteHost(),logService.getIp());
                logService.createSOALog2(soaObject);
            }
        }
        catch (IOException ex) {

            //set response to client
            ResponseError res = new ResponseError();
            res.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            res.setResponseTime(DateTimeUtils.getCurrentDate());
            res.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            res.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            res.setDetailErrorMessage("Token is wrong");

            ObjectMapper mapper = new ObjectMapper();
            String responseBody = mapper.writeValueAsString(res);
            JSONObject transactionDetailResponse = new JSONObject(responseBody);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",null,null,null,
                            messageTimestamp, "opendataservice", httpServletRequest.getRequestURI(),"1",
                            httpServletRequest.getRemoteHost(), res.getResultMessage(),res.getResultCode(),
                            Throwables.getStackTraceAsString(ex),logService.getIp());
            logService.createSOALogException(soaExceptionObject);

            //get time duration
            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",null, null, null, "smartMarket", "client",
                    messageTimestamp, "opendataservice ", "1", timeDuration,
                    "response", transactionDetailResponse, responseStatus, res.getResultCode(),
                    res.getResultMessage(), logTimestamp, httpServletRequest.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject);
        }
    }
}