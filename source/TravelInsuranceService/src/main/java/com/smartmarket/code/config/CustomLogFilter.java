package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.response.ResponseError;
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
import org.springframework.core.env.ConfigurableEnvironment;
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

    @Autowired
    ConfigurableEnvironment environment;


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

        String requestURL = request.getRequestURL().toString();
        String operationName = requestURL.substring(requestURL.indexOf(environment.getRequiredProperty("version") + "/") + 3, requestURL.length());


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
                            messageTimestamp, "travelinsuranceservice", "1", null,
                            "request", transactionDetail, null, null,
                            null, logtimeStamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);
                }else {
                    String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);
                    JSONObject transactionDetail = new JSONObject();
                    transactionDetail.put("transactionDetail","Format request body is not true");

                    //logRequest vs Client
                    ServiceObject soaObject = new ServiceObject("serviceLog", null,null, null, "client", "smartMarket",
                            messageTimestamp, "travelinsuranceservice", "1", null,
                            "request", transactionDetail, null, null,
                            null, logtimeStamp, request.getRemoteHost(), Utils.getClientIp(request),operationName);
                    logService.createSOALog2(soaObject);
                }

            } else {

            }
        } catch (Exception ex) {
            throw new CustomException("Error in custom log filter", HttpStatus.BAD_REQUEST, null,null, ResponseCode.CODE.ERROR_IN_BACKEND, ResponseCode.MSG.ERROR_IN_BACKEND_MSG, HttpStatus.BAD_REQUEST);
//            String logTimestamp = formatter.format(date);
//
//            int status = response.getStatus();
//            String responseStatus = Integer.toString(status);
//
//            //logException
//            ServiceExceptionObject soaExceptionObject =
//                    new ServiceExceptionObject(Constant.EXCEPTION_LOG,"response",null,null,null,
//                            messageTimestamp, "travelinsuranceservice", request.getRequestURI(),"1",
//                            request.getRemoteHost(), null,null,
//                            Throwables.getStackTraceAsString(ex),logService.getIp());
//            logService.createSOALogException(soaExceptionObject);
//
//            //get time duration
//            String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);
//            JSONObject transactionDetail = new JSONObject();
//            transactionDetail.put("transactionDetail","Exception in CustomLogFilter");
//
//            //logResponse vs Client
//            ServiceObject soaObject = new ServiceObject("serviceLog",null, null, null, "smartMarket", "client",
//                    messageTimestamp, "travelinsuranceservice ", "1", timeDuration,
//                    "response", transactionDetail, responseStatus, null,
//                    null, logTimestamp, request.getRemoteHost(),logService.getIp());
//            logService.createSOALog2(soaObject);
        }

        chain.doFilter(request, response);
    }
}
