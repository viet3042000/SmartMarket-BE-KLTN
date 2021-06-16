package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomLogFilter extends OncePerRequestFilter {

    @Autowired
    LogServiceImpl logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logtimeStamp = formatter.format(date);
        String messageTimestamp = logtimeStamp;

        request = new RequestWrapper(request);
        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());

        try {
            if (request.getMethod().equals("POST")) {
                if(Utils.isJSONValid(jsonString)){
                    JSONObject requestBody = new JSONObject(jsonString);
                    String messasgeId = requestBody.getString("requestId");
                    String transactionDetail = requestBody.toString();

                    String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);
                    //
                    //logRequest vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", messasgeId, null, "client", "BIC",
                            messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                            "request", transactionDetail, null, null,
                            null, logtimeStamp, request.getRemoteHost(), logService.getIp());
                    logService.createSOALog2(soaObject.getStringObject());
                }else {
                    String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                    ServiceObject soaObject = new ServiceObject("serviceLog", null, null, "client", "BIC",
                            messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                            "request", jsonString, null, null,
                            null, logtimeStamp, request.getRemoteHost(), logService.getIp());
                    logService.createSOALog2(soaObject.getStringObject());
                }

            } else {

            }
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST, "test");
        }

        chain.doFilter(request, response);
    }
}
