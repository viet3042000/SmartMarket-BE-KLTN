package com.smartmarket.code.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.HostConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class Utils {


    @Autowired
    HostConstants hostConstants;

    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    public static String[] getArrayIP(String ipStr) {
        String[] arrIP = {};

        if (ipStr != null) {
            arrIP = ipStr.split("##");
        }
        return arrIP;
    }


    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;

    }

    public static boolean isJSONValid(String jsonInString ) {
        try {
            if(jsonInString.equalsIgnoreCase("")){
                return false ;
            }
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }



//    public String getNameService(String URLRequest ){
//        AntPathMatcher matcher = new AntPathMatcher();
//
//        String nameService  =  "" ;
//        if (matcher.match(hostConstants.URL_CREATE, URLRequest)) {
//            nameService = "createBICTravelInsurance" ;
//        }
//        else if ( matcher.match(hostConstants.URL_UPDATE, URLRequest)){
//            nameService = "updateBICTravelInsurance" ;
//        }
//        else if ( matcher.match(hostConstants.URL_UPDATE, URLRequest)){
//            nameService = "updateBICTravelInsurance" ;
//        }
//
//    }


}

