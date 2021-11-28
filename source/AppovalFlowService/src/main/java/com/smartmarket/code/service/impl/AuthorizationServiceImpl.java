package com.smartmarket.code.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.model.*;
import com.smartmarket.code.service.*;
import com.smartmarket.code.util.APIUtils;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    AccessTokenService accessTokenService;

    @Autowired
    AccesUserService accesUserService;

    @Autowired
    UrlService urlService;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    CachingServiceImpl cachingService ;

    @Autowired
    ClientService clientService;

    @Autowired
    ClientDetailService clientDetailService;


//    @Override
//    public boolean AuthorUserAccess(Long userId) {
//
//        //get uri request
//        HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        String uriRequest = request.getRequestURI();
//
//        //get user token
//        Map<String, Object> claims = null;
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        claims = JwtUtils.getClaimsMap(authentication);
//        String userName = null;
//
//        //get clientid from claims
//        if (claims != null) {
//            userName = (String) claims.get("user_name");
//        } else {
//            return false;
//        }
//
//        try {
//            //find user by name
//            User userToken =
//                    userRepository.findByUsername(userName).orElseThrow(() -> new CustomException("Vui lòng kiểm tra lại Token",HttpStatus.BAD_REQUEST,null,null,null,null, HttpStatus.BAD_REQUEST));
//
//            //get url request
//            AntPathMatcher matcher = new AntPathMatcher();
//            Set<Url> urlSet = urlService.findUrlByUserIdActive(userToken.getId());
//            Url urlMatched = null;
//            if (urlSet != null) {
//                for (Url url1 : urlSet) {
//
//                    //check url matched yet
//                    String path = url1.getPath();
//                    if (matcher.match(path, uriRequest)) {
//                        urlMatched = url1;
//                        break;
//
//                    }
//                }
//            } else {
//                throw new CustomException("Please check the request again" , HttpStatus.BAD_REQUEST,null,null,null,null, HttpStatus.BAD_REQUEST) ;
//            }
//
//            // get list accessUser
//            Set<AccessUser> accessUserSetCheck = null;
//            if (urlMatched != null && userToken != null && userId != null ) {
//                accessUserSetCheck = accesUserService.checkAccessUser(userToken.getId(), urlMatched.getId(),userId);
//            } else {
//                throw new CustomException("Please check the request again" , HttpStatus.BAD_REQUEST,null,null,null,null, HttpStatus.BAD_REQUEST) ;
//            }
//
//            if(accessUserSetCheck != null && accessUserSetCheck.size() > 0){
//                return true ;
//            }
//
//        }catch (Exception ex){
//            throw new CustomException(ex.getMessage() , HttpStatus.BAD_REQUEST,null,null,null,null, HttpStatus.BAD_REQUEST) ;
//        }
//
//        return false;
//    }


    public boolean validActuator(String[] paths , String urlRequest) {
        AntPathMatcher matcher = new AntPathMatcher();
        if(paths != null){
            for (int i= 0 ;  i < paths.length ; i++ )  {
                String path = paths[i];
                if (matcher.match(path, urlRequest)) {
                    return true;
                }
            }
        }
        return false ;
    }

    public boolean validIp(String[] ipAccessArr , String ipRequest) {
        for (int i =  0 ; i < ipAccessArr.length ; i++ ){
            if(ipAccessArr[0].equalsIgnoreCase("ALL")){
                return true ;
            }
            if(ipRequest.equals(ipAccessArr[i])){
                return  true ;
            }
        }
        return false ;
    }


    //load token from database
//    public String getTokenFromDatabase() throws JsonProcessingException {
//
//        AccessToken accessToken = accessTokenService.findByUsername("bic-dsvn@bic.vn") ;
//        String token = "" ;
//        if(accessToken != null ){
//            token = accessToken.getToken() ;
//            long timeRemain = 0L ;
//            if(     accessToken.getToken() != null
//                    && accessToken.getExpireTime() != null
//                    && accessToken.getIssueTime() != null ){
//
//                //timeRemain =  accessToken.getExpireTime() -  DateTimeUtils.getCurrenTime();
//                Long expireTime = accessToken.getExpireTime() ;
//                Long currentTimeSystem = DateTimeUtils.getCurrentTimeRaw() ;
//                timeRemain =  expireTime -  currentTimeSystem;
//
//            }
//            //check time expire access token
//            if (timeRemain < (1000*60*3)) {
//
//                //post get token
//                UserLoginBIC userLoginBIC = new UserLoginBIC();
//                userLoginBIC.setUsername("bic-dsvn@bic.vn");
//                userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
//                userLoginBIC.setDomainname("vetautructuyen.com.vn");
//
//                ObjectMapper mapper = new ObjectMapper();
//
//                String requestToken = mapper.writeValueAsString(userLoginBIC);
//                ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null, null);
//                if(jsonResultGetToken.getBody() != null){
//                    //get token from response
//                    String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));
//                    Long timeIssue = JwtUtils.getDateIssuetoLong(new JSONObject(jsonResultGetToken.getBody()));
//                    Long timeExpire = JwtUtils.getDateExpiretoLong(new JSONObject(jsonResultGetToken.getBody()));
//
//                    //update token in database
//                    accessTokenService.updateTokenByAccessTokenId(timeExpire,timeIssue,tokenUpdate,accessToken.getId()) ;
//                    token = tokenUpdate;
//                }
//            }
//
//        }
//        return token ;
//    }


//    public String getToken() throws JsonProcessingException {
//        String token = "" ;
//        if (Integer.parseInt(environment.getRequiredProperty("getTokenBIC.mode")) == 1){
//            token = getTokenFromCache() ;
//        }else {
//            token = getTokenOncePerRequest() ;
//        }
//        return token ;
//    }
//
//
//        //load token from cache
//    public String getTokenFromCache() throws JsonProcessingException {
//
//        //SET TIMEOUT
//        //set Time out get token api BIC
//        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetToken = new SimpleClientHttpRequestFactory();
//        //Connect timeout
//        clientHttpRequestFactoryGetToken.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginTravelBIC")));
//        //Read timeout
//        clientHttpRequestFactoryGetToken.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginTravelBIC")));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> claims = null;
//        claims = JwtUtils.getClaimsMap(authentication);
//        String clientId = null;
//
//        //get clientid from claims
//        if (claims != null) {
//            clientId = (String) claims.get("client_id");
//        }
//
//        Optional<ClientDetail> clientDetail = clientDetailService.findByclientIdName(clientId);
//
//        Object obAccessToken = null ;
//        if (clientDetail.isPresent() == true) {
//            obAccessToken = cachingService.getFromCacheString("accesstoken", clientDetail.get().getUserNameBic());
//        }
//
//        AccessToken accessToken = null;
//        if (obAccessToken != null) {
//            accessToken = (AccessToken) obAccessToken;
//        }
//
//        String token = "" ;
//        if(accessToken != null ){
//            token = accessToken.getToken() ;
//            long timeRemain = 0L ;
//            if(     accessToken.getToken() != null
//                    && accessToken.getExpireTime() != null
//                    && accessToken.getIssueTime() != null ){
//
//                //timeRemain =  accessToken.getExpireTime() -  DateTimeUtils.getCurrenTime();
//                Long expireTime = accessToken.getExpireTime() ;
//                Long currentTimeSystem = DateTimeUtils.getCurrenTime() ;
//                timeRemain =  expireTime -  currentTimeSystem;
//
//            }
//            //check time expire access token
//            if (timeRemain < (1000*60*3)) {
//
//                //post get token
//                UserLoginBIC userLoginBIC = new UserLoginBIC();
//                userLoginBIC.setUsername(clientDetail.get().getUserNameBic());
//                userLoginBIC.setPassword(clientDetail.get().getPasswordBic());
//                userLoginBIC.setDomainname(clientDetail.get().getDomainNameBic());
//
//                ObjectMapper mapper = new ObjectMapper();
//
//                String requestToken = mapper.writeValueAsString(userLoginBIC);
//                ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(environment.getRequiredProperty("api.loginTravelBIC"), null, requestToken, null, null,clientHttpRequestFactoryGetToken);
//                if(jsonResultGetToken.getBody() != null){
//                    //get token from response
//                    String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));
//                    Long timeIssue = JwtUtils.getDateIssuetoLong(new JSONObject(jsonResultGetToken.getBody()));
//                    Long timeExpire = JwtUtils.getDateExpiretoLong(new JSONObject(jsonResultGetToken.getBody()));
//
//                    //update token in database
//                    AccessToken accessTokenCreate = accessTokenService.updateCache(clientDetail.get().getUserNameBic(), tokenUpdate,timeIssue,timeExpire);
//                    return tokenUpdate;
//                }
//            }else {
//                return accessToken.getToken();
//            }
//
//        }else {
//            //post get token
//            UserLoginBIC userLoginBIC = new UserLoginBIC();
//            userLoginBIC.setUsername(clientDetail.get().getUserNameBic());
//            userLoginBIC.setPassword(clientDetail.get().getPasswordBic());
//            userLoginBIC.setDomainname(clientDetail.get().getDomainNameBic());
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            String requestToken = mapper.writeValueAsString(userLoginBIC);
//            ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(environment.getRequiredProperty("api.loginTravelBIC"), null, requestToken, null, null,clientHttpRequestFactoryGetToken);
//            if(jsonResultGetToken.getBody() != null){
//                //get token from response
//                String tokenUpdate = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));
//                Long timeIssue = JwtUtils.getDateIssuetoLong(new JSONObject(jsonResultGetToken.getBody()));
//                Long timeExpire = JwtUtils.getDateExpiretoLong(new JSONObject(jsonResultGetToken.getBody()));
//
//                //update token in database
//                AccessToken accessTokenCreate = accessTokenService.createCache(clientDetail.get().getUserNameBic(), tokenUpdate,timeIssue,timeExpire);
//                return tokenUpdate;
//            }
//        }
//        return token ;
//    }

    // load token from BIC
//    public String getTokenOncePerRequest() throws JsonProcessingException, APIAccessException {
//
//        String token = "" ;
//
//        //SET TIMEOUT
//        //set Time out get token api BIC
//        SimpleClientHttpRequestFactory clientHttpRequestFactoryGetToken = new SimpleClientHttpRequestFactory();
//        //Connect timeout
//        clientHttpRequestFactoryGetToken.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginTravelBIC")));
//        //Read timeout
//        clientHttpRequestFactoryGetToken.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.loginTravelBIC")));
//
//        //post get token
//        UserLoginBIC userLoginBIC = new UserLoginBIC();
//        userLoginBIC.setUsername(environment.getRequiredProperty("account.DSVN.username"));
//        userLoginBIC.setPassword(environment.getRequiredProperty("account.DSVN.password"));
//        userLoginBIC.setDomainname(environment.getRequiredProperty("account.DSVN.domainName"));
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        String requestToken = mapper.writeValueAsString(userLoginBIC);
//        ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(environment.getRequiredProperty("api.loginTravelBIC"), null, requestToken, null, null,clientHttpRequestFactoryGetToken);
//        if(jsonResultGetToken != null && jsonResultGetToken.getBody() != null){
//            //get token from response
//            token = JwtUtils.getTokenFromResponse(new JSONObject(jsonResultGetToken.getBody()));
//
//        }
//        return token ;
//    }

    public ArrayList<String> getRoles(){
        Map<String, Object> claims = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nameOfAuthentication = authentication.getClass().getName();
        if(nameOfAuthentication.contains("KeycloakAuthenticationToken")) {
            claims = JwtUtils.getClaimsMapFromKeycloakAuthenticationToken(authentication);
        }else {
            claims = JwtUtils.getClaimsMap(authentication);
        }
        ArrayList<String> roles = (ArrayList<String>) claims.get("roles");

        return roles;
    }

}
