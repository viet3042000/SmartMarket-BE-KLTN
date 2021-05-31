package com.smartmarket.code.service.impl;


import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.AccessUser;
import com.smartmarket.code.model.Url;
import com.smartmarket.code.model.User;
import com.smartmarket.code.service.AccesUserService;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.UrlService;
import com.smartmarket.code.service.UserService;
import com.smartmarket.code.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    AccesUserService accesUserService;

    @Autowired
    UserService userService;

    @Autowired
    UrlService urlService;

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
//        Set<AccessUser> accessUserSet = null;
//        if (urlMatched != null && userToken != null) {
//            accessUserSet = accesUserService.findAccessUserByUserIdAndUserUrlId(userToken.getId(), urlMatched.getId());
//        } else {
//            return false;
//        }
//
//        //verify user accessUser
            //        boolean containAccessUser=  accessUserSet.stream().anyMatch(u -> u.getId().equals(userToken.getId())) ;

//        boolean verifyUserAccess = false;
//        if (accessUserSet != null) {
//            for (AccessUser accessUser : accessUserSet) {
//                Long idUserToken = userToken.getId();
//                if (accessUser.getUserId().equals(userId) == true) {
//                    verifyUserAccess = true;
//                    break;
//                }
//            }
//        }

//        if (verifyUserAccess == true) {
//            return true;
//        }

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


}
