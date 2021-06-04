package com.smartmarket.code.config;

import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.dao.UrlRepository;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.model.entitylog.SoaObject;
import com.smartmarket.code.service.impl.CachingServiceImpl;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.JwtUtils;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class CustomAuthorizeRequestFilter extends OncePerRequestFilter {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UrlRepository urlRepository;

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

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

        try {

            String cururl = request.getRequestURI().toLowerCase();
            String contextPath = request.getContextPath().toLowerCase();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //declare
            String url = "";
            Map<String, Object> claims = null;
            Set<Url> urlSet = null;
            claims = JwtUtils.getClaimsMap(authentication);
            String clientId = null;

            //get clientid from claims
            if (claims != null) {
                clientId = (String) claims.get("client_id");
            } else {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Không tìm thấy client id trong token");
                return;
            }

            //find client by clientName
            Optional<Client> client = clientRepository.findByclientName(clientId);
            if (client.isPresent() == true) {
                urlSet = urlRepository.findUrlByClientIdActive(client.get().getId());

                //check url access
                if (urlSet == null) {
                    httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Không tìm thấy api được phép kết nối của client id trong dữ liệu");
                    return;
                }

            } else {
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Không tìm thấy client id trong dữ liệu");
                return;
            }


            AntPathMatcher matcher = new AntPathMatcher();
            String method = request.getMethod();

            // verify endpoint
            String URLRequest = request.getRequestURI();
            boolean verifyEndpoint = false;

            if (client.isPresent() == true && urlSet != null) {
                for (Url url1 : urlSet) {

                    String path = url1.getPath();
                    if (matcher.match(path, URLRequest)) {
                        verifyEndpoint = true;
                        break;

                    }
                }
            }

            if (verifyEndpoint == false) {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Client id không có quyền truy cập api");
                return;
            }

        } catch (CustomException ex) {
            httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
            return;
        }

        request = new RequestWrapper(request);
//        ResponseWrapper responseCopier = new ResponseWrapper(response);

        String jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        JSONObject requestBody = new JSONObject(jsonString);
        String MESSASGEID = requestBody.getString("requestId");
        String TRANSACTIONDETAIL = requestBody.toString();

        long elapsed = System.currentTimeMillis() - startTime;
        String TIMEDURATION = Long.toString(elapsed);

        //logRequest vs Client
        SoaObject soaObject = new SoaObject(MESSASGEID, null, "Client", "BIC",
                MESSAGETIMESTAMP, request.getRequestURI(), "1", TIMEDURATION,
                "request", TRANSACTIONDETAIL, null, null,
                null, LOGTIMESTAMP, request.getRemoteHost(),logService.getIp());
        logService.createSOALog2(soaObject.getStringObject());

        chain.doFilter(request, response);

//        chain.doFilter(request, responseCopier);
//        byte[] body = responseCopier.getCopy();
//        String stringBody = new String(body, response.getCharacterEncoding());
//        JSONObject responseBody = new JSONObject(stringBody);
    }
}