package com.smartmarket.code.service;


import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface BICTransactionExceptionService {
    //createTravelBIC+updateTravelBIC
    BICTransaction createBICTransactionFromRequest(HttpServletRequest request,String resultCode,String bicResultCode);
    PendingBICTransaction createPendingBICTransactionFromRequest(HttpServletRequest request,String type) ;

    //createOrderOutbox
    BICTransaction createBICTransactionFromRequestCreate(BaseDetail<CreateTravelInsuranceBICRequest> requestCreateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId);
    PendingBICTransaction createPendingBICTransactionFromRequestCreate(BaseDetail<CreateTravelInsuranceBICRequest> requestCreateTravelInsuranceBICRequest, String type);

    //updateOrderOutbox
    BICTransaction createBICTransactionFromRequestUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> requestUpdateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId);
    PendingBICTransaction createPendingBICTransactionFromRequestUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> requestUpdateTravelInsuranceBICRequest,String type);
}
