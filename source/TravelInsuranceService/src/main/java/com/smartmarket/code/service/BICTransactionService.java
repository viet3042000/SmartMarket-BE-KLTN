package com.smartmarket.code.service;


import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import org.springframework.http.ResponseEntity;

public interface BICTransactionService extends BaseService<BICTransaction> {
    public BICTransaction createBICTransactionFromCreateorUpdateTravel(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                                       ResponseEntity<String> jsonResultCreateBIC,
                                                                       String resultCode , String bicResultCode) ;
}
