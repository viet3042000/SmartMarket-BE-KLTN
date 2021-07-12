package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.StartTimeBean;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

        //set start time
        request.setAttribute("startTime"  , DateTimeUtils.getCurrenTime());
        Long startTime = DateTimeUtils.getStartTimeFromRequest(request);

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
                    JSONObject requestBodyJson = new JSONObject(jsonString);
                    EJson requestBody = new EJson(jsonString);
                    String requestId = requestBody.getString("requestId");
                    String requestTime = requestBody.getString("requestTime");
                    String stringRequestBody = requestBodyJson.toString();
                    JSONObject transactionDetail = new JSONObject(stringRequestBody);

                    //logRequest vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", requestId, requestTime,null, "client", "smartMarket",
                            messageTimestamp, "opendataservice", "1", null,
                            "request", transactionDetail, null, null,
                            null, logtimeStamp, request.getRemoteHost(), logService.getIp());
                    logService.createSOALog2(soaObject);
                }else {
                    String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);
                    JSONObject transactionDetail = new JSONObject();
                    transactionDetail.put("transactionDetail","Format request body is not true ");

                    //logRequest vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", null,null, null, "client", "smartMarket",
                            messageTimestamp, "opendataservice", "1", null,
                            "request", transactionDetail, null, null,
                            null, logtimeStamp, request.getRemoteHost(), logService.getIp());
                    logService.createSOALog2(soaObject);
                }

            } else {

            }
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST, "test");
        }

        chain.doFilter(request, response);
    }
}
