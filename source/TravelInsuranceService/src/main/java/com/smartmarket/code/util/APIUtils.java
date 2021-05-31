/**
 *
 */
package com.smartmarket.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.smartmarket.code.exception.CustomException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author HopNX
 *
 */
@Component
public class APIUtils {
    private Logger LOGGER = LoggerFactory.getLogger(APIUtils.class);


//    public JSONObject callAPIWithToken(String host, String path, HttpMethod httpMethod, Map<String, Object> params,
//                                       String token) {
//
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Authorization", token);
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//            String url = host + path;
//            HttpEntity<String> entity;
//
//            if (httpMethod == HttpMethod.GET) {
//                StringBuilder parameter = new StringBuilder("?");
//                if (params != null) {
//                    for (Entry<String, Object> ele : params.entrySet()) {
//                        parameter.append(ele.getKey()).append("=").append(ele.getValue().toString()).append("&");
//                    }
//                }
//
//                url = url + parameter.substring(0, parameter.length() - 1);
//                entity = new HttpEntity<>("", headers);
//            } else {
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                ObjectMapper objMapper = new ObjectMapper();
//                String JSONStr = objMapper.writeValueAsString(params);
//                entity = new HttpEntity<>(JSONStr, headers);
//            }
//            ResponseEntity<String> result = restTemplate.exchange(url, httpMethod, entity, String.class);
//            return new JSONObject(result.getBody());
//        } catch (Exception e) {
//            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

//    public JSONObject postAPILogin( String host, String path, HttpMethod httpMethod, Map<String, Object> params) {
//
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//            String url = host + path;
//            HttpEntity<String> entity;
//
//            if (httpMethod == HttpMethod.GET) {
//                StringBuilder parameter = new StringBuilder("?");
//                if (params != null) {
//                    for (Entry<String, Object> ele : params.entrySet()) {
//                        parameter.append(ele.getKey()).append("=").append(ele.getValue().toString()).append("&");
//                    }
//                }
//
//                url = url + parameter.substring(0, parameter.length() - 1);
//                entity = new HttpEntity<>("", headers);
//            } else {
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                ObjectMapper objMapper = new ObjectMapper();
//                String JSONStr = objMapper.writeValueAsString(params);
//                entity = new HttpEntity<>(JSONStr, headers);
//            }
//            ResponseEntity<String> result = restTemplate.exchange(url, httpMethod, entity, String.class);
//            return new JSONObject(result.getBody());
//        } catch (Exception e) {
//            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }


    public ResponseEntity<String> postDataByApiBody(String url, EJson headerParam, String body, String token) {
        RestTemplate restTemplate = new RestTemplate();
//        ObjectMapper mapper = new ObjectMapper();
        String bodyrequest = body;
        ResponseEntity<String> result = null;
        try {
//            if (body != null) {
//                bodyrequest = mapper.writeValueAsString(body);
//            } else {
//                throw new CustomException("Không tìm thấy request body!", HttpStatus.BAD_REQUEST);
//            }
            HttpHeaders headers = new HttpHeaders();

            if (!StringUtils.isEmpty(token)) {
                headers.add("Authorization", "Bearer " + token);
            }

            headers.setContentType(MediaType.APPLICATION_JSON);
            if (headerParam != null) {
                Iterator<String> keys = headerParam.jsonObject().keySet().iterator();
                // headers.add("session", UUID.randomUUID().toString());
                while (keys.hasNext()) {
                    String key = keys.next();
                    headers.add(key, headerParam.getString(key));
                }
            }
            HttpEntity<String> entity = new HttpEntity<String>(bodyrequest, headers);
            result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (result == null) {
                throw new CustomException("Not found request body!", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return result;
    }


    public ResponseEntity<String> getApiWithParam(String url, Map<String, Object> param, Map<String, Object> pathVariable, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> result = null ;

        if (!StringUtils.isEmpty(token)) {
            headers.add("Authorization", "Bearer " + token);
        }

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (param != null) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());

            }
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);

//        System.out.println(builder.buildAndExpand(pathVariable).toUri());
        if(pathVariable != null ){
            result = restTemplate.exchange(builder.buildAndExpand(pathVariable).toUri(), HttpMethod.GET, entity,
                    String.class);

        }else{
            result = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, entity,
                    String.class);
        }

        if (result == null) {
            throw new CustomException("Not found request body!", HttpStatus.BAD_REQUEST);
        }
        if (result.getStatusCode() != HttpStatus.OK) {
            throw new CustomException("An error occurred during API call!", result.getStatusCode());
        }
        return result;
    }
}
