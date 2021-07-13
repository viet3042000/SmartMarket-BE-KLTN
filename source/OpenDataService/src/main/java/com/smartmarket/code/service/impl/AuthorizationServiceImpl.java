package com.smartmarket.code.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.AccessUser;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.model.User;
import com.smartmarket.code.request.entity.UserLoginOpenData;
import com.smartmarket.code.service.*;
import com.smartmarket.code.util.APIUtils;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    AccessTokenService accessTokenService;

    @Autowired
    AccesUserService accesUserService;

    @Autowired
    UserService userService;

    @Autowired
    UrlService urlService;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    CachingServiceImpl cachingService;

    @Override
    public boolean AuthorUserAccess(Long userId) {

        //get uri request
        HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uriRequest = request.getRequestURI();

        //get user token
        Map<String, Object> claims = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        claims = JwtUtils.getClaimsMap(authentication);
        String userName = null;

        //get clientid from claims
        if (claims != null) {
            userName = (String) claims.get("user_name");
        } else {
            return false;
        }

        try {
            //find user by name
            User userToken =
                    userService.findByUsername(userName).orElseThrow(() -> new CustomException("Vui lòng kiểm tra lại Token", HttpStatus.BAD_REQUEST));

            //get url request
            AntPathMatcher matcher = new AntPathMatcher();
            Set<Url> urlSet = urlService.findUrlByUserIdActive(userToken.getUserName());
            Url urlMatched = null;
            if (urlSet != null) {
                for (Url url1 : urlSet) {

                    //check url matched yet
                    String path = url1.getPath();
                    if (matcher.match(path, uriRequest)) {
                        urlMatched = url1;
                        break;

                    }
                }
            } else {
                throw new CustomException("Please check the request again", HttpStatus.BAD_REQUEST);
            }

            // get list accessUser
            Set<AccessUser> accessUserSetCheck = null;
            if (urlMatched != null && userToken != null && userId != null) {
                accessUserSetCheck = accesUserService.checkAccessUser(userToken.getId(), urlMatched.getId(), userId);
            } else {
                throw new CustomException("Please check the request again", HttpStatus.BAD_REQUEST);
            }

            if (accessUserSetCheck != null && accessUserSetCheck.size() > 0) {
                return true;
            }

        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return false;
    }

    public boolean validActuator(String[] paths, String urlRequest) {
        AntPathMatcher matcher = new AntPathMatcher();
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (matcher.match(path, urlRequest)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validIp(String[] ipAccessArr, String ipRequest) {
        for (int i = 0; i < ipAccessArr.length; i++) {
            if (ipAccessArr[0].equalsIgnoreCase("ALL")) {
                return true;
            }
            if (ipRequest.equals(ipAccessArr[i])) {
                return true;
            }
        }
        return false;
    }

    public String getToken() throws JsonProcessingException {
        String token = "" ;
        if (Integer.parseInt(environment.getRequiredProperty("getToken.mode")) == 1){
            token = getTokenFromCache() ;
        }else {
            token = getTokenOncePerRequest() ;
        }
        return token ;
    }


    //load token from Cache
    public String getTokenFromCache() throws JsonProcessingException {

        //SET TIMEOUT
        //set Time out get token api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetToken = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryGetToken.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginOpenData")));
        //Read timeout
        clientHttpRequestFactoryGetToken.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginOpenData")));


        ObjectMapper objectMapper = new ObjectMapper();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);
        String userName = null;
        //get clientid from claims
        if (claims != null) {
            userName = (String) claims.get("user_name");
        } else {
            return null;
        }

        Object obAccessToken = cachingService.getFromCacheString("accesstoken", userName);
        AccessToken accessToken = null;
        if (obAccessToken != null) {
            accessToken = (AccessToken) obAccessToken;
        }

        String token = "";

        //TH da co accessToken trong cache
        if (accessToken != null) {
            token = accessToken.getToken();
            long timeRemain = 0L;
            if (accessToken.getToken() != null
                    && accessToken.getExpireTime() != null
                    && accessToken.getIssueTime() != null) {

                //timeRemain =  accessToken.getExpireTime() -  DateTimeUtils.getCurrenTime();
                Long expireTime = accessToken.getExpireTime();
                Long currentTimeSystem = DateTimeUtils.getCurrenEpochTime();
                timeRemain = expireTime - currentTimeSystem;

            }
            //check time expire access token
            if (timeRemain < (60 * 3)) {

                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
                map.add("username", userName);
                ResponseEntity<String> jsonResultGetToken = apiUtils.postByFormDataURLEncode(environment.getRequiredProperty("api.loginOpenData"), map, clientHttpRequestFactoryGetToken);
                if (jsonResultGetToken.getBody() != null) {
                    //get token from response
                    String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

                    //update token in cache
                    AccessToken accessTokenCreate = accessTokenService.updateCache(userName, tokenUpdate);
                    return tokenUpdate;
                }
            } else {
                return accessToken.getToken();
            }

            //TH chua co accessToken
        } else {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("username", userName);
            ResponseEntity<String> jsonResultGetToken = apiUtils.postByFormDataURLEncode(environment.getRequiredProperty("api.loginOpenData"), map, clientHttpRequestFactoryGetToken);

            String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

            AccessToken accessTokenCreate = accessTokenService.createCache(userName, tokenUpdate);
            return tokenUpdate;
        }
        return token;
    }


    //load token from database
//    public String getTokenFromDatabase() throws JsonProcessingException {

//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);
//        String userName = null;
//        //get clientid from claims
//        if (claims != null) {
//            userName = (String) claims.get("user_name");
//        } else {
//            return null;
//        }
//
//        AccessToken accessToken = accessTokenService.findByUsername(userName) ;
//        String token = "" ;
//
//        if(accessToken != null ){
//            token = accessToken.getToken() ;
//            long timeRemain = 0L ;
//            if(     accessToken.getToken() != null
//                    && accessToken.getExpireTime() != null
//                    && accessToken.getIssueTime() != null ){
//
//                //timeRemain =  accessToken.getExpireTime() -  DateTimeUtils.getCurrenTime();
//                Long expireTime = accessToken.getExpireTime() ;
//                Long currentTimeSystem = DateTimeUtils.getCurrenEpochTime() ;
//                timeRemain =  expireTime -  currentTimeSystem;
//
//            }
//            //check time expire access token
//            if (timeRemain < (60*3)) {
//
//                MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//                map.add("username", userName);
//                ResponseEntity<String> jsonResultGetToken = apiUtils.postByFormDataURLEncode(environment.getRequiredProperty("api.loginOpenData"),map);
//                if(jsonResultGetToken.getBody() != null){
//                    Long timeIssue = 0L;
//                    Long timeExpire = 0L;
//
//                    //get token from response
//                    String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));
//
//                    // create a token object to represent the token that is in use.
//                    Jwt jwt = JwtHelper.decode(token);
//
//                    // jwt.getClaims() will return a JSON object of all the claims in your token
//                    // Convert claims JSON object into a Map so we can get the value of a field
//                        Map<String, Object> claimsService  = new HashMap<>();
//                    try {
//                        claimsService = objectMapper.readValue(jwt.getClaims(), Map.class);
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    if(claims != null ){
//                        timeExpire =Long.valueOf((Integer) claimsService.get("exp"))  ;
//                        timeIssue =Long.valueOf((Integer) claimsService.get("iat")) ;
//                    }
//
//                    //update token in database
//                    accessTokenService.updateTokenByAccessTokenId(timeExpire,timeIssue,tokenUpdate,accessToken.getId()) ;
//                    token = tokenUpdate;
//                }
//            }
//
//        }
//        return token;
//    }

    // load token from BIC
    public String getTokenOncePerRequest() throws JsonProcessingException, APIAccessException {

        String token = "" ;

        //SET TIMEOUT
        //set Time out get token api BIC
        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetToken = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactoryGetToken.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginOpenData")));
        //Read timeout
        clientHttpRequestFactoryGetToken.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginOpenData")));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = null;
        claims = JwtUtils.getClaimsMap(authentication);
        String userName = (String) claims.get("user_name");


        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", userName);
        ResponseEntity<String> jsonResultGetToken = apiUtils.postByFormDataURLEncode(environment.getRequiredProperty("api.loginOpenData"),map,clientHttpRequestFactoryGetToken);
        if(jsonResultGetToken != null && jsonResultGetToken.getBody() != null){
            //get token from response
            token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));

        }
        return token ;
    }

}

//    public static void main(String[] args) throws ParseException {
//        Long current  = DateTimeUtils.getCurrenTime() ;
//        Long test = new Date().getTime() ;
//        System.out.println(current);
//        String dateStr=  "03/06/2021 02:29:06 PM" ;
//        Date date1=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aaa").parse(dateStr);
//        System.out.println("test");
//
//        Long dateTimeSystem = DateTimeUtils.getCurrenTime() ;
//        Long dateTimeZone = DateTimeUtils.getCurrentDateRaw().getTime() ;
//        System.out.println("end");
//    }

//}
