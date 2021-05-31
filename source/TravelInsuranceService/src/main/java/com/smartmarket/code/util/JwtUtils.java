package com.smartmarket.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

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

    public static String getTokenFromResponse(JSONObject jsonObject){
        JSONObject data = null ;
        String token = null ;
        if(jsonObject != null ){
            data =  jsonObject.getJSONObject("data") ;
            token =  data.getString("token") ;
        }
        return token ;
    }


}
