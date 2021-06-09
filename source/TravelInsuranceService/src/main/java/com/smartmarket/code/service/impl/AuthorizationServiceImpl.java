package com.smartmarket.code.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.AccessUser;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.model.User;
import com.smartmarket.code.request.entity.UserLoginBIC;
import com.smartmarket.code.service.*;
import com.smartmarket.code.util.APIUtils;
import com.smartmarket.code.util.DateTimeUtils;
import com.smartmarket.code.util.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    userService.findByUsername(userName).orElseThrow(() -> new CustomException("Vui lòng kiểm tra lại Token",HttpStatus.BAD_REQUEST));

            //get url request
            AntPathMatcher matcher = new AntPathMatcher();
            Set<Url> urlSet = urlService.findUrlByUserIdActive(userToken.getId());
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
                throw new CustomException("Please check the request again" , HttpStatus.BAD_REQUEST) ;
            }

            // get list accessUser
            Set<AccessUser> accessUserSetCheck = null;
            if (urlMatched != null && userToken != null && userId != null ) {
                accessUserSetCheck = accesUserService.checkAccessUser(userToken.getId(), urlMatched.getId(),userId);
            } else {
                throw new CustomException("Please check the request again" , HttpStatus.BAD_REQUEST) ;
            }

            if(accessUserSetCheck != null && accessUserSetCheck.size() > 0){
                return true ;
            }

        }catch (Exception ex){
            throw new CustomException(ex.getMessage() , HttpStatus.BAD_REQUEST) ;
        }

        return false;
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

    // load token from BIC
    public String getTokenFromDatabase() throws JsonProcessingException {

        String token = "" ;

                //post get token
                UserLoginBIC userLoginBIC = new UserLoginBIC();
                userLoginBIC.setUsername("bic-dsvn@bic.vn");
                userLoginBIC.setPassword("vWKqgmocYrQOqrWoVXkQ");
                userLoginBIC.setDomainname("vetautructuyen.com.vn");

                ObjectMapper mapper = new ObjectMapper();

                String requestToken = mapper.writeValueAsString(userLoginBIC);
                ResponseEntity<String> jsonResultGetToken = apiUtils.postDataByApiBody(HostConstants.INTERCOMMUNICATION_RESTFUL_API.BIC_HOST_LOGIN, null, requestToken, null, null);
                if(jsonResultGetToken.getBody() != null){
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
