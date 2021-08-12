package com.smartmarket.code.service;


import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.util.EJson;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface BICTransactionService extends BaseService<BICTransaction> {
    public BICTransaction createBICTransactionFromCreateTravel(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode , String bicResultCode,
                                                               String clientIp, String typeTransaction) ;
    public BICTransaction createBICTransactionFromCreateTravelOutbox(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode , String bicResultCode,
                                                               String clientIp, String typeTransaction,String clientId) ;

    public BICTransaction createBICTransactionParameter(String requestId, String orderReference, String orderId,
                                                        String customerName, String phoneNumber, String email,
                                                        String ordPaidMoney, String consumerId, String fromDate,
                                                        String toDate, Date logTimestamp, String resultCode,
                                                        String bicResultCode, String ordDate, String productId,
                                                        String customerAddress , String clientIp,String type,Long destroy) ;

    public BICTransaction createBICTransactionParameterOutbox(String requestId, String orderReference, String orderId,
                                                        String customerName, String phoneNumber, String email,
                                                        String ordPaidMoney, String consumerId, String fromDate,
                                                        String toDate, Date logTimestamp, String resultCode,
                                                        String bicResultCode, String ordDate, String productId,
                                                        String customerAddress , String clientIp,String type,Long destroy,String clientId) ;

    public BICTransaction createBICTransactionFromUpdateTravel(BaseDetail<UpdateTravelInsuranceBICRequest> object,
                                                                       EJson jsonObjectReponseCreate,
                                                                       String resultCode , String bicResultCode,
                                                                       String clientIp, String typeTransaction) ;

    public BICTransaction createBICTransactionFromUpdateTravelOubox(BaseDetail<UpdateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode , String bicResultCode,
                                                               String clientIp, String typeTransaction,String clientId) ;
}
