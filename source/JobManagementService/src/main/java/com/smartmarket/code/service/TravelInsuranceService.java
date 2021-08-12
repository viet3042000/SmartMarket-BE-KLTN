package com.smartmarket.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public interface TravelInsuranceService {
    public ResponseEntity<?> createTravelBIC(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException;

    public ResponseEntity<?> getTravelBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest,
                                          HttpServletRequest request,
                                          HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException;

    public ResponseEntity<?> updateTravelBIC(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse responseSelvet)
            throws JsonProcessingException, APIAccessException;


    public ResponseEntity<?> create(BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, String clientIp, String clientId,
                                    Long startTime,String hostName)
            throws JsonProcessingException, APIAccessException, Exception;


    public ResponseEntity<?> update(BaseDetail<UpdateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest,String clientIp,String clientId,
                                    Long startTime,String hostName)
            throws JsonProcessingException, APIAccessException;

    public ResponseEntity<?> get(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest,String clientIp,String clientId,
                                 Long startTime,String hostName)
            throws JsonProcessingException, APIAccessException;

}



