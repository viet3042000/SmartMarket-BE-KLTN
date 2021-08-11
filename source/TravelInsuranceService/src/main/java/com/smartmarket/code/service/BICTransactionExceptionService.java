package com.smartmarket.code.service;


import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

public interface BICTransactionExceptionService {
    BICTransaction createBICTransactionFromRequest(HttpServletRequest request,String resultCode,String bicResultCode);
    BICTransaction createBICTransactionFromRequestCreate(BaseDetail<CreateTravelInsuranceBICRequest> requestCreateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId);
    BICTransaction createBICTransactionFromRequestUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> requestUpdateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId);
}
