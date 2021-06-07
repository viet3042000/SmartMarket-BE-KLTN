package com.smartmarket.code.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.util.DateTimeUtils;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class FailureEvents {
    private ObjectMapper objectMapper = new ObjectMapper();


    //    @EventListener
//    public void onFailure(AuthenticationFailureEvent failure) {
//        if (badCredentials.getAuthentication() instanceof BearerTokenAuthenticationToken) {
//            // ... handle
//        }
//    }
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        // ...
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent failures) {
//        if (badCredentials.getAuthentication() instanceof BearerTokenAuthenticationToken) {
//            // ... handle
//        }
//        HttpServletResponse response;
//        ReponseError responseError = new ReponseError();
//        responseError.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
//        responseError.setResponseTime(DateTimeUtils.getCurrentDate());
//        return new ResponseEntity<>(responseError, HttpStatus.OK);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

        try {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unauthorized");
//            Map<String, Object> data = new HashMap<>();
//            data.put(
//                    "timestamp",
//                    Calendar.getInstance().getTime());
//            data.put(
//                    "message",
//                    failures.getException().getMessage());

            ReponseError responseError = new ReponseError();
            responseError.setResultCode(ResponseCode.CODE.AUTHORIZED_FAILED);
            responseError.setResponseTime(DateTimeUtils.getCurrentDate());
            responseError.setResultMessage(ResponseCode.MSG.AUTHORIZED_FAILED_MSG);
            responseError.setDetailErrorCode(HttpStatus.UNAUTHORIZED.toString());
            responseError.setDetailErrorMessage(failures.getException().getMessage());
            response.addHeader("WWW-Authenticate", failures.getException().getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(responseError));
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
