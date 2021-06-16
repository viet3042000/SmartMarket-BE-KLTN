package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.service.BICTransactionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BICTransactionServiceImp implements BICTransactionService {

    @Autowired
    private BICTransactionRepository bicTransactionRepository;

    @Autowired
    private FieldsConstants fieldsConstants;

    @Override
    public BICTransaction create(BICTransaction object) {
        BICTransaction bicTransaction = new BICTransaction();
        return bicTransactionRepository.save(object);
    }

    @Override
    public BICTransaction update(BICTransaction object) {
        return null;
    }

    @Override
    public BICTransaction delete(Long id) {
        return null;
    }

    @Override
    public BICTransaction getDetail(Long id) {
        return null;
    }


    @Override
    public BICTransaction createBICTransactionFromCreateorUpdateTravel(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                                       JSONObject jsonObjectReponseCreate,
                                                                       String resultCode, String bicResultCode) {
        BICTransaction bicTransaction = new BICTransaction();

        //check
        if (object != null && object.getDetail() != null) {
            Long orderIdResponse = null;
            JSONObject jsonObjectData = jsonObjectReponseCreate.getJSONObject("data");
            if (jsonObjectReponseCreate != null && jsonObjectData.getString("type").equalsIgnoreCase("200") ) {
                orderIdResponse = jsonObjectReponseCreate.getLong("orderId");
            }else {
                orderIdResponse = -1L ;
            }
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId("DSVN");
            bicTransaction.setCustomerName("DSVN");
            bicTransaction.setCustomerAddress("default");
            bicTransaction.setEmail(object.getDetail().getOrders().getOrdBillEmail());
            bicTransaction.setFromDate(object.getDetail().getTrv().getFromDate());
            bicTransaction.setResultCode(resultCode);
            bicTransaction.setLogTimestamp(new Date());
            bicTransaction.setOrderId(orderIdResponse == null ? "-1" : String.valueOf(orderIdResponse));
            bicTransaction.setOrderReference(object.getDetail().getOrders().getOrderReference());
            bicTransaction.setOrdPaidMoney(String.valueOf(object.getDetail().getOrders().getOrdPaidMoney()));
            bicTransaction.setPhoneNumber(object.getDetail().getOrders().getOrdBillMobile());
            bicTransaction.setRequestId(object.getRequestId());
            bicTransaction.setToDate(object.getDetail().getTrv().getToDate());
            bicTransaction.setOrdDate(object.getDetail().getOrders().getOrdDate());
            bicTransaction.setProductId(fieldsConstants.createOrderProductId);
        }

        return bicTransactionRepository.save(bicTransaction);
    }

    @Override
    public BICTransaction createBICTransactionParameter(String requestId,   String orderReference, String orderId,
                                                        String customerName, String phoneNumber, String email,
                                                        String ordPaidMoney, String consumerId, String fromDate,
                                                        String toDate, Date logTimestamp, String resultCode,
                                                        String bicResultCode, String ordDate, String productId,
                                                        String customerAddress) {
        BICTransaction bicTransaction = new BICTransaction();

        bicTransaction.setOrderId(orderId);
        bicTransaction.setBicResultCode(bicResultCode);
        bicTransaction.setConsumerId("DSVN");
        bicTransaction.setCustomerName("DSVN");
        bicTransaction.setCustomerAddress("default");
        bicTransaction.setEmail(email);
        bicTransaction.setFromDate(fromDate);
        bicTransaction.setResultCode(resultCode);
        bicTransaction.setLogTimestamp(new Date());
        bicTransaction.setOrderReference(orderReference);
        bicTransaction.setOrdPaidMoney(ordPaidMoney);
        bicTransaction.setPhoneNumber(phoneNumber);
        bicTransaction.setRequestId(requestId);
        bicTransaction.setToDate(toDate);
        bicTransaction.setOrdDate(ordDate);
        bicTransaction.setProductId(fieldsConstants.createOrderProductId);


        return bicTransactionRepository.save(bicTransaction);
    }



}
