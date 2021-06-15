package com.smartmarket.code.service;


import com.smartmarket.code.model.BICTransaction;

import javax.servlet.http.HttpServletRequest;

public interface BICTransactionExceptionService {
    BICTransaction createBICTransactionFromRequest(HttpServletRequest request,String resultCode, String bicResultCode) ;
}
