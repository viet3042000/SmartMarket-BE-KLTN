/**
 *
 */
package com.smartmarket.code.util;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APIResponseException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author HopNX
 *
 */
@Component
public class APIUtils {
    private Logger LOGGER = LoggerFactory.getLogger(APIUtils.class);

    public ResponseEntity<String> postDataByApiBody(String url, EJson headerParam, String body, String token, String requestId) throws APIAccessException {
        ResponseEntity<String> result = null;


        //set Time out
//            SimpleClientHttpRequestFactory clientHttpRequestFactory
//                    = new SimpleClientHttpRequestFactory();
//            //Connect timeout
//            clientHttpRequestFactory.setConnectTimeout(10000);
//            //Read timeout
//            clientHttpRequestFactory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate();

        String bodyrequest = body;

        HttpHeaders headers = new HttpHeaders();

        if (!StringUtils.isEmpty(token)) {
            headers.add("Authorization", "Bearer " + token);
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        if (headerParam != null) {
            Iterator<String> keys = headerParam.jsonObject().keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                headers.add(key, headerParam.getString(key));
            }
        }
        HttpEntity<String> entity = new HttpEntity<String>(bodyrequest, headers);

        result = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        //catch truong hop chua goi dc sang BIC
//        catch (ResourceAccessException e){
//            if(e.getCause() instanceof ConnectException) {
//                throw new APIAccessException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//            }
//            throw new APIAccessException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//        }
//        //catch truong hop goi dc sang BIC nhưng loi
//        catch (HttpClientErrorException e){
//            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getStatusCode(),e.getResponseBodyAsString());
//        }

        return result;
    }

    public ResponseEntity<String> postByFormDataURLEncode(String url, MultiValueMap<String, String> map,SimpleClientHttpRequestFactory clientHttpRequestFactory) throws APIAccessException {
        ResponseEntity<String> result = null;


        //set Time out
//            SimpleClientHttpRequestFactory clientHttpRequestFactory
//                    = new SimpleClientHttpRequestFactory();
//            //Connect timeout
//            clientHttpRequestFactory.setConnectTimeout(10000);
//            //Read timeout
//            clientHttpRequestFactory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        result = restTemplate.postForEntity( url, request , String.class );

        return result;
    }


    public ResponseEntity<String> getApiWithParam(String url, Map<String, Object> param, Map<String, Object> pathVariable, String token, String requestId,SimpleClientHttpRequestFactory clientHttpRequestFactory) throws APIAccessException {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> result = null;

//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (param != null) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);
        if (pathVariable != null) {
            result = restTemplate.exchange(builder.buildAndExpand(pathVariable).toUri(), HttpMethod.GET, entity,
                    String.class);

        } else {
            URI uri = builder.build().toUri() ;
            result = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, entity,
                    String.class);
        }

        //catch truong hop chua goi dc sang BIC
//        catch (ResourceAccessException e){
//            if(e.getCause() instanceof ConnectException) {
//                throw new APIAccessException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//            }
//            throw new APIAccessException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//        }
//        //catch truong hop goi dc sang BIC nhưng loi
//        catch (HttpClientErrorException e){
//            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getStatusCode(),e.getResponseBodyAsString());
//        }

        return result;
    }

    public ResponseEntity<String> putDataByApiBody(String ID, String url, EJson headerParam, String body, String token, String requestId) throws APIAccessException {
        RestTemplate restTemplate = new RestTemplate();
        String bodyrequest = body;
        ResponseEntity<String> result = null;
        if (ID == null) {
            throw new CustomException("Không tìm thấy orderId trong bản tin request!", HttpStatus.BAD_REQUEST, requestId);
        }

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

        url = url + "/" + ID;
        result = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

//        }
//        //catch truong hop chua goi dc sang BIC
//        catch (ResourceAccessException e){
//            if(e.getCause() instanceof ConnectException) {
//                throw new APIAccessException(requestId, ResponseCode.CODE.SOA_TIMEOUT_BACKEND,ResponseCode.MSG.SOA_TIMEOUT_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//            }
//            throw new APIAccessException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getMessage(), Throwables.getStackTraceAsString(e));
//        }
//        //catch truong hop goi dc sang BIC nhưng loi
//        catch (HttpClientErrorException e){
//            throw new APIResponseException(requestId, ResponseCode.CODE.ERROR_WHEN_CALL_TO_BACKEND,ResponseCode.MSG.ERROR_WHEN_CALL_TO_BACKEND_MSG,e.getStatusCode(),e.getResponseBodyAsString());
//        }
        return result;
    }
}
