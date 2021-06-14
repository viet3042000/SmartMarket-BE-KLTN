package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharConversionException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    LogServiceImpl logService;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logTimestamp = formatter.format(date);
        String messageTimestamp = logTimestamp;


//        httpServletResponse
//                .addHeader("message", "Luke, I am your father!");

//        httpServletResponse
//                .sendError(HttpStatus.UNAUTHORIZED.value());

//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
        HttpServletResponse response = httpServletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

        try {
            ReponseError responseError = new ReponseError();
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage(e.getMessage());
            response.addHeader("WWW-Authenticate", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE+ ";"+ "charset=UTF-8" );
//            response.setContentType("text/html; charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(responseError));

            httpServletRequest = new RequestWrapper(httpServletRequest);
            String jsonString = IOUtils.readInputStreamToString(httpServletRequest.getInputStream());
            JSONObject requestBody = new JSONObject(jsonString);
            String messasgeId = requestBody.getString("requestId");

            int status = httpServletResponse.getStatus();
            String responseStatus = Integer.toString(status);

            long elapsed = System.currentTimeMillis() - startTime;
            String timeDuration = Long.toString(elapsed);

            //logException
            ServiceExceptionObject soaExceptionObject =
                    new ServiceExceptionObject("serviceLog","response",messasgeId,null,
                            messageTimestamp, "travelinsuranceservice", httpServletRequest.getRequestURI(),"1",
                            httpServletRequest.getRemoteHost(), responseError.getResultMessage(),responseError.getResultCode(),
                            responseError.getDetailErrorMessage(),logService.getIp(),requestBody.getString("requestTime"));
            logService.createSOALogException(soaExceptionObject.getStringObject());

            //logResponse vs Client
            ServiceObject soaObject = new ServiceObject("serviceLog",messasgeId, null, "BIC", "Client",
                    messageTimestamp, "travelinsuranceservice ", "1", timeDuration,
                    "response", response.toString(), responseStatus, responseError.getResultCode(),
                    responseError.getResultMessage(), logTimestamp, httpServletRequest.getRemoteHost(),logService.getIp());
            logService.createSOALog2(soaObject.getStringObject());

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}