package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.dao.AccessTokenRepository;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.service.AccessTokenService;
import com.smartmarket.code.service.BICTransactionService;
import net.bytebuddy.implementation.bytecode.constant.FieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
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
                                                                       ResponseEntity<String> jsonResultCreateBIC,
                                                                       String resultCode , String bicResultCode) {
        BICTransaction bicTransaction=  new BICTransaction() ;

        //check
        if (object !=  null && object.getDetail() != null ){
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId("DSVN");
            bicTransaction.setCustomerName("DSVN");
            bicTransaction.setCustomerAddress("default");
            bicTransaction.setEmail(object.getDetail().getOrders().getOrdBillEmail());
            bicTransaction.setFromDate(object.getDetail().getTrv().getFromDate());
            bicTransaction.setResultCode(resultCode);
            bicTransaction.setLogTimestamp(new Date());
            bicTransaction.setOrderId(String.valueOf(object.getDetail().getOrders().getOrderId()));
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
}
