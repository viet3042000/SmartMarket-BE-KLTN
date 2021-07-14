package com.smartmarket.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.ClientRepository;
import com.smartmarket.code.dao.UrlRepository;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.response.ResponseError;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.ClientService;
import com.smartmarket.code.service.UrlService;
import com.smartmarket.code.service.UserService;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.SetResponseUtils;
import com.smartmarket.code.util.Utils;
import org.hibernate.exception.JDBCConnectionException;
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
import java.util.*;

@Component
public class CustomAuthorizeRequestFilter extends OncePerRequestFilter {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    AuthorizationService authorizationService ;

    @Autowired
    SetResponseUtils setResponseUtils;

    @Autowired
    ClientService clientService ;

    @Autowired
    UrlService urlService ;

    @Autowired
    UserService userService ;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long startTime = DateTimeUtils.getStartTimeFromRequest(request);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");

        String[] pathActuator = {"/actuator/**","/opendata-service/v1/refreshcacheandconfig/**"} ;

        try {

            // verify endpoint
            AntPathMatcher matcher = new AntPathMatcher();
            String method = request.getMethod();
            String URLRequest = request.getRequestURI();
            String ipRequest = Utils.getClientIp(request);
            String contextPath = request.getContextPath().toLowerCase();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!authorizationService.validActuator(pathActuator, URLRequest)) {
                //declare
                Map<String, Object> claims = null;
                Set<Url> urlSetByClient = null;
                Set<Url> urlSetByUserName = null;
                claims = JwtUtils.getClaimsMap(authentication);
                String clientId = null;
                String userName = null;

                //get clientid from claims
                if (claims != null) {
                    clientId = (String) claims.get("client_id");
                } else {
                    httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Not found client id in token");
                    return;
                }

                //get clientid from claims
                if (claims != null) {
                    userName = (String) claims.get("user_name");
                } else {
                    httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Not found user name in token");
                    return;
                }

                //find client by clientName
                Optional<Client> client = clientService.findByclientName(clientId);
                if (client.isPresent() == true) {
                    urlSetByClient = urlService.findUrlByClientId(client.get().getClientId());

                    //check url access
                    if (urlSetByClient == null) {
                        httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Client id allowed api not found in database");
                        return;
                    }

                } else {
                    httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Client id not found in database");
                    return;
                }

                //find user by userName
                Optional<User> user = userService.findByUsername(userName);
                if (user.isPresent() == true) {
                    urlSetByUserName = urlService.findUrlByUserIdActive(user.get().getUserName());

                    //check url access
                    if (urlSetByUserName == null) {
                        httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "user name allowed api not found in database");
                        return;
                    }

                } else {
                    httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "user name not found in database");
                    return;
                }


                //verify ip
                boolean verifyIp = false;
                String[] ipAccessArr = Utils.getArrayIP(client.get().getIpAccess());
                for (int i = 0; i < ipAccessArr.length; i++) {
                    if (ipAccessArr[0].equalsIgnoreCase("ALL")) {
                        verifyIp = true;
                        break;
                    }
                    if (ipRequest.equals(ipAccessArr[i])) {
                        verifyIp = true;
                        break;
                    }
                }

                if (verifyIp == false) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "IP client id is not allowed to access API");
                    return;
                }

                boolean verifyEndpointByClient = false;

                if (client.isPresent() == true && urlSetByClient != null) {
                    for (Url url1 : urlSetByClient) {

                        String path = url1.getPath();
                        if (matcher.match(path, URLRequest)) {
                            verifyEndpointByClient = true;
                            break;

                        }
                    }
                }
                if (verifyEndpointByClient == false) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Client id is not allowed to access API");
                    return;
                }

                boolean verifyEndpointByUser = false;
                if (user.isPresent() == true && urlSetByUserName != null) {
                    for (Url url1 : urlSetByUserName) {

                        String path = url1.getPath();
                        if (matcher.match(path, URLRequest)) {
                            verifyEndpointByUser = true;
                            break;

                        }
                    }
                }

                if (verifyEndpointByUser == false) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "User name is not allowed to access API");
                    return;
                }

            }

        }catch (Exception ex){
            if (ex.getCause() instanceof JDBCConnectionException) {

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String logTimestamp = formatter.format(date);
                String messageTimestamp = logTimestamp;

                //set response to client
                ResponseError res = new ResponseError();
                res.setResultCode(ResponseCode.CODE.ERROR_IN_BACKEND);
                res.setResponseTime(DateTimeUtils.getCurrentDate());
                res.setResultMessage(ResponseCode.MSG.ERROR_IN_BACKEND_MSG);
                res.setDetailErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.toString());
                res.setDetailErrorMessage("An error occurred during the processing of the system!");

                ObjectMapper mapper = new ObjectMapper();
                String responseBody = mapper.writeValueAsString(res);
                JSONObject transactionDetailResponse = new JSONObject(responseBody);

                //logException
                ServiceExceptionObject soaExceptionObject =
                        new ServiceExceptionObject("serviceLog","response",null,null,null,
                                messageTimestamp, "opendataservice", request.getRequestURI(),"1",
                                request.getRemoteHost(), res.getResultMessage(),res.getResultCode(),
                                Throwables.getStackTraceAsString(ex),logService.getIp());
                logService.createSOALogException(soaExceptionObject);

                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog",null, null, "OpenData", "smartMarket","client",
                        messageTimestamp, "opendataservice", "1", timeDuration,
                        "response", transactionDetailResponse, null, res.getResultCode(),
                        res.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
                logService.createSOALog2(soaObject);
            }else {

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String logTimestamp = formatter.format(date);
                String messageTimestamp = logTimestamp;

                //set response to client
                ResponseError res = new ResponseError();
                res = setResponseUtils.setResponseException(res, ex);
                ObjectMapper mapper = new ObjectMapper();
                String responseBody = mapper.writeValueAsString(res);
                JSONObject transactionDetailResponse = new JSONObject(responseBody);

                //logException
                ServiceExceptionObject soaExceptionObject =
                        new ServiceExceptionObject("serviceLog","response",null,null,null,
                                messageTimestamp, "opendataservice", request.getRequestURI(),"1",
                                request.getRemoteHost(), res.getResultMessage(),res.getResultCode(),
                                Throwables.getStackTraceAsString(ex),logService.getIp());
                logService.createSOALogException(soaExceptionObject);

                String timeDuration = DateTimeUtils.getElapsedTimeStr(startTime);

                //logResponse vs Client
                ServiceObject soaObject = new ServiceObject("serviceLog",null, null, "OpenData", "smartMarket", "client",
                        messageTimestamp, "opendataservice", "1", timeDuration,
                        "response", transactionDetailResponse, null, res.getResultCode(),
                        res.getResultMessage(), logTimestamp, request.getRemoteHost(),logService.getIp());
                logService.createSOALog2(soaObject);
            }
            httpServletResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
            return;
        }
        chain.doFilter(request, response);

    }
}