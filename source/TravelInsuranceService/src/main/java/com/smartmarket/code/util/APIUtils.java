/**
 *
 */
package com.smartmarket.code.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APIResponseException;
import com.smartmarket.code.exception.APITimeOutRequestException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.HandleResponseException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

/**
 * @author HopNX
 *
 */
@Component
public class APIUtils {
    private Logger LOGGER = LoggerFactory.getLogger(APIUtils.class);

//    @Test(timeout = 1)
    public ResponseEntity<String> postDataByApiBody(String url, EJson headerParam, String body, String token , String requestId) throws APITimeOutRequestException {
        ResponseEntity<String> result = null;
        try {
//            HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//            httpRequestFactory.setConnectionRequestTimeout(1000);
//            httpRequestFactory.setConnectTimeout(1000);
//            httpRequestFactory.setReadTimeout(1000);
//            RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

            SimpleClientHttpRequestFactory clientHttpRequestFactory
                    = new SimpleClientHttpRequestFactory();
            //Connect timeout
            clientHttpRequestFactory.setConnectTimeout(10000);
            //Read timeout
            clientHttpRequestFactory.setReadTimeout(10000);

            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);


            String bodyrequest = body;
//            ResponseEntity<String> result = null;

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
            if(result.getStatusCode() != HttpStatus.OK ){
                throw new CustomException("",result.getStatusCode());
            }

            return result;
        }

        catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException) {
                throw new APITimeOutRequestException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage());
            }
        }
        catch (HttpClientErrorException e) {
            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND, ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG, e.getStatusCode().toString(), e.getResponseBodyAsString());
        }
        return result;
    }


    public ResponseEntity<String> getApiWithParam(String url, Map<String, Object> param, Map<String, Object> pathVariable, String token , String requestId) throws APITimeOutRequestException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> result = null ;
        try {

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
            if(pathVariable != null ){
                result = restTemplate.exchange(builder.buildAndExpand(pathVariable).toUri(), HttpMethod.GET, entity,
                        String.class);

            }else{
                result = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, entity,
                        String.class);
            }

            if (result == null) {
                throw new CustomException("Not found request body!", HttpStatus.BAD_REQUEST ,requestId);
            }
            if (result.getStatusCode() != HttpStatus.OK) {
                throw new CustomException("An error occurred during API call!", result.getStatusCode(),requestId);
            }

        }
        catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException) {
                throw new APITimeOutRequestException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage());
            }
        }
        catch (HttpClientErrorException e){
            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getStatusCode().toString(),e.getResponseBodyAsString());
        }

        return result;
    }

    public ResponseEntity<String> putDataByApiBody(String ID, String url, EJson headerParam, String body, String token,String requestId) throws APITimeOutRequestException {
        RestTemplate restTemplate = new RestTemplate();
//        ObjectMapper mapper = new ObjectMapper();
        String bodyrequest = body;
        ResponseEntity<String> result = null;
        if (ID == null){
            throw new CustomException("Không tìm thấy orderId trong bản tin request!", HttpStatus.BAD_REQUEST,requestId);
        }
        try {

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

            url = url +"/"+ ID;
            result = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (result == null) {
                throw new CustomException("Not found request body!", HttpStatus.BAD_REQUEST);
            }

        }
        catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException) {
                throw new APITimeOutRequestException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage());
            }
        }
        catch (HttpClientErrorException e){
            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getStatusCode().toString(),e.getResponseBodyAsString());
        }

        return result;
    }
}
