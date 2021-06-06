package com.smartmarket.code.config;

import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.model.entitylog.SoaObject;
import com.smartmarket.code.service.impl.LogServiceImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
public class CustomLogFilter extends OncePerRequestFilter {

    @Autowired
    LogServiceImpl logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String LOGTIMESTAMP = formatter.format(date);
        String MESSAGETIMESTAMP = LOGTIMESTAMP;

        request = new RequestWrapper(request);
//        ResponseWrapper responseCopier = new ResponseWrapper(response);

        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String MESSASGEID = requestBody.getString("requestId");
        String TRANSACTIONDETAIL = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String TIMEDURATION = Long.toString(elapsed);
//
        //logRequest vs Client
        SoaObject soaObject = new SoaObject(MESSASGEID, null, "Client", "BIC",
                MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                "request", TRANSACTIONDETAIL, null, null,
                null, LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        chain.doFilter(request, response);

    }
}
