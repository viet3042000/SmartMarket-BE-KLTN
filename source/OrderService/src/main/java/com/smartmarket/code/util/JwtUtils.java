package com.smartmarket.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    public static Map<String, Object> getClaimsMap(Authentication authentication){
        Map<String, Object> claims = null;
        if( authentication != null ){
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map =
                    objectMapper.convertValue(authentication.getPrincipal(), Map.class);

            // create a token object to represent the token that is in use.
            Jwt jwt = JwtHelper.decode((String) map.get("tokenValue"));

            // jwt.getClaims() will return a JSON object of all the claims in your token
            // Convert claims JSON object into a Map so we can get the value of a field
            try {
                claims = objectMapper.readValue(jwt.getClaims(), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return claims ;


        }
        return claims ;
    }


    public static Map<String, Object> getClaimsMapFromKeycloakAuthenticationToken(Authentication authentication){
        Map<String, Object> claims = null;
        if( authentication != null ){
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            Map<String, Object> map =
                    objectMapper.convertValue(authentication.getPrincipal(), Map.class);

            Map<String, Object> mapIdToken = (Map<String, Object>) map.get("keycloakSecurityContext");

            // create a token object to represent the token that is in use.
            Jwt jwt = JwtHelper.decode((String) mapIdToken.get("tokenString"));
            // jwt.getClaims() will return a JSON object of all the claims in your token
            // Convert claims JSON object into a Map so we can get the value of a field
            try {
                claims = objectMapper.readValue(jwt.getClaims(), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return claims ;
    }


    public static String getTokenFromResponse(JSONObject jsonObject){
        JSONObject data = null ;
        String token = null ;
        if(jsonObject != null ){
            data =  jsonObject.getJSONObject("data") ;
            token =  data.getString("token") ;
        }
        return token ;
    }

    public static Long getDateIssuetoLong(JSONObject jsonObject) {
        JSONObject data = null ;
        String dateIssue = null ;
        Long dateIssueLong = 0L ;
        if(jsonObject != null ){
            data =  jsonObject.getJSONObject("data") ;
            dateIssue =  data.getString("issued") ;
            Date date1= DateTimeUtils.getStringToDate(dateIssue);
            dateIssueLong = date1.getTime() ;
        }
        return dateIssueLong ;
    }


    public static Long getDateExpiretoLong(JSONObject jsonObject){
        JSONObject data = null ;
        String dateIssue = null ;
        Long dateExpireLong = 0L ;
        if(jsonObject != null ){
            data =  jsonObject.getJSONObject("data") ;
            dateIssue =  data.getString("expireOn") ;
            Date date1= DateTimeUtils.getStringToDate(dateIssue);
            dateExpireLong = date1.getTime() ;
        }
        return dateExpireLong ;
    }

    public static String getClientId(){
        //get clientid from claims
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> claims = JwtUtils.getClaimsMap(authentication);
        String clientId = null;

        if (claims != null) {
            clientId = (String) claims.get("client_id");
        }

        return  clientId ;
    }

}
