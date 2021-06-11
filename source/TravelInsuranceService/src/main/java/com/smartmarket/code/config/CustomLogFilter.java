package com.smartmarket.code.config;

import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.SoaObject;
import com.smartmarket.code.service.impl.LogServiceImpl;
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

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String logtimeStamp = formatter.format(date);
        String messageTimestamp = logtimeStamp;

        request = new RequestWrapper(request);
//        System.out.println(request.getMethod());
        try {
            if (request.getMethod().equals("POST")) {
                String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
                JSONObject requestBody = new JSONObject(jsonString);
                String messasgeId = requestBody.getString("requestId");
                String transactionDetail = requestBody.toString();

                long elapsed = System.currentTimeMillis() - startTime;
                String timeDuration = Long.toString(elapsed);
//
                //logRequest vs Client
                SoaObject soaObject = new SoaObject("serviceLog", messasgeId, null, "client", "BIC",
                        messageTimestamp, "travelinsuranceservice", "1", timeDuration,
                        "request", transactionDetail, null, null,
                        null, logtimeStamp, request.getRemoteHost(), logService.getIp());
                logService.createSOALog2(soaObject.getStringObject());
            } else {

            }
        }catch (Exception ex){
            throw new CustomException(ex.getMessage() , HttpStatus.BAD_REQUEST ,"test" ) ;
        }
        chain.doFilter(request, response);

    }
}
