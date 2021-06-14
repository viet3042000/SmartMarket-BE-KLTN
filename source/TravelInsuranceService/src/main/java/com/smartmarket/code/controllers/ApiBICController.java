package com.smartmarket.code.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.exception.APITimeOutRequestException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.exception.InvalidInputException;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.response.CreateTravelInsuranceBICResponse;
import com.smartmarket.code.response.DataCreateBIC;
import com.smartmarket.code.response.ReponseError;
import com.smartmarket.code.service.AuthorizationService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.TravelInsuranceService;
import com.smartmarket.code.service.impl.LogServiceImpl;
import com.smartmarket.code.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;


//@RefreshScope
@RestController
@RequestMapping("/insurance/travel-insurance-service/v1/")
public class ApiBICController {

    @Autowired
    TravelInsuranceService travelInsuranceService ;

    //    @PreAuthorize("@authorizationServiceImpl.AuthorUserAccess(#userid.userId)")
    @PostMapping(value = "/create-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createTravelBIC(@RequestBody(required = true) BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {

       return travelInsuranceService.createTravelBIC(createTravelInsuranceBICRequest,request,responseSelvet);
    }


    @PostMapping(value = "/inquire-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getTravelBIC(@RequestBody BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {
        return travelInsuranceService.getTravelBIC(queryTravelInsuranceBICRequest,request,responseSelvet) ;
    }

    @PostMapping(value = "/change-bic-travel-insurance", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updateTravelBIC(@RequestBody BaseDetail<CreateTravelInsuranceBICRequest> updateTravelInsuranceBICRequest, HttpServletRequest request, HttpServletResponse responseSelvet) throws JsonProcessingException, APITimeOutRequestException {
        return travelInsuranceService.updateTravelBIC(updateTravelInsuranceBICRequest,request,responseSelvet) ;
    }

}
