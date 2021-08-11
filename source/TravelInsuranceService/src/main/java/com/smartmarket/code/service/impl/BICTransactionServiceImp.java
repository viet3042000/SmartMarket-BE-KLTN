package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.ClientService;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BICTransactionServiceImp implements BICTransactionService {

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    private BICTransactionRepository bicTransactionRepository;

    @Autowired
    private FieldsConstants fieldsConstants;

    @Autowired
    ClientService clientService ;

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
    public BICTransaction createBICTransactionFromCreateTravel(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode, String bicResultCode ,
                                                               String clientIp, String typeTransaction) {
        BICTransaction bicTransaction = new BICTransaction();

        String clientId = JwtUtils.getClientId() ;
        Optional<Client> client = clientService.findByclientName(clientId);


        //check
        if (object != null && object.getDetail() != null) {
            Long orderIdResponse = null;
            EJson jsonObjectData = jsonObjectReponseCreate.getJSONObject("data");
            if (jsonObjectData != null && jsonObjectData.getString("type").equalsIgnoreCase("200") ) {
                orderIdResponse = jsonObjectReponseCreate.getLong("orderId");
            }else {
                orderIdResponse = -1L ;
            }
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId(client.get().getConsumerId());
            bicTransaction.setCustomerName(object.getDetail().getOrders().getOrdBillFirstName());
            bicTransaction.setCustomerAddress((object.getDetail().getOrders().getOrdBillStreet1() == null || object.getDetail().getOrders().getOrdBillStreet1().equals("") == true) ? "no address" : object.getDetail().getOrders().getOrdBillStreet1());
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
            bicTransaction.setType(typeTransaction);
            bicTransaction.setClientIp(clientIp);
            bicTransaction.setProductId(environment.getRequiredProperty("createTravelBIC.DSVN.order.productId"));
            bicTransaction.setDestroy(0L);
        }

        return bicTransactionRepository.save(bicTransaction);
    }

    @Override
    public BICTransaction createBICTransactionFromCreateTravelOutbox(BaseDetail<CreateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode, String bicResultCode ,
                                                               String clientIp, String typeTransaction,String clientId) {
        BICTransaction bicTransaction = new BICTransaction();

        Optional<Client> client = clientService.findByclientName(clientId);


        //check
        if (object != null && object.getDetail() != null) {
            Long orderIdResponse = null;
            EJson jsonObjectData = jsonObjectReponseCreate.getJSONObject("data");
            if (jsonObjectData != null && jsonObjectData.getString("type").equalsIgnoreCase("200") ) {
                orderIdResponse = jsonObjectReponseCreate.getLong("orderId");
            }else {
                orderIdResponse = -1L ;
            }
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId(client.get().getConsumerId());
            bicTransaction.setCustomerName(object.getDetail().getOrders().getOrdBillFirstName());
            bicTransaction.setCustomerAddress((object.getDetail().getOrders().getOrdBillStreet1() == null || object.getDetail().getOrders().getOrdBillStreet1().equals("") == true) ? "no address" : object.getDetail().getOrders().getOrdBillStreet1());
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
            bicTransaction.setType(typeTransaction);
            bicTransaction.setClientIp(clientIp);
            bicTransaction.setProductId(environment.getRequiredProperty("createTravelBIC.DSVN.order.productId"));
            bicTransaction.setDestroy(0L);
        }

        return bicTransactionRepository.save(bicTransaction);
    }

    @Override
    public BICTransaction createBICTransactionParameter(String requestId,   String orderReference, String orderId,
                                                        String customerName, String phoneNumber, String email,
                                                        String ordPaidMoney, String consumerId, String fromDate,
                                                        String toDate, Date logTimestamp, String resultCode,
                                                        String bicResultCode, String ordDate, String productId,
                                                        String customerAddress , String clientIp,String type, Long destroy) {
        BICTransaction bicTransaction = new BICTransaction();


        String clientId = JwtUtils.getClientId() ;
        Optional<Client> client = clientService.findByclientName(clientId);

        bicTransaction.setOrderId(orderId);
        bicTransaction.setBicResultCode(bicResultCode);
        bicTransaction.setConsumerId(client.get().getConsumerId());
        bicTransaction.setCustomerName(customerName);
        bicTransaction.setCustomerAddress(customerAddress);
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
        bicTransaction.setClientIp(clientIp);
        bicTransaction.setType(type);
        bicTransaction.setProductId(environment.getRequiredProperty("createTravelBIC.DSVN.order.productId"));
        bicTransaction.setDestroy(destroy);

        return bicTransactionRepository.save(bicTransaction);
    }

    @Override
    public BICTransaction createBICTransactionParameterOutbox(String requestId,   String orderReference, String orderId,
                                                        String customerName, String phoneNumber, String email,
                                                        String ordPaidMoney, String consumerId, String fromDate,
                                                        String toDate, Date logTimestamp, String resultCode,
                                                        String bicResultCode, String ordDate, String productId,
                                                        String customerAddress , String clientIp,String type, Long destroy,String clientId) {
        BICTransaction bicTransaction = new BICTransaction();

        Optional<Client> client = clientService.findByclientName(clientId);

        bicTransaction.setOrderId(orderId);
        bicTransaction.setBicResultCode(bicResultCode);
        bicTransaction.setConsumerId(client.get().getConsumerId());
        bicTransaction.setCustomerName(customerName);
        bicTransaction.setCustomerAddress(customerAddress);
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
        bicTransaction.setClientIp(clientIp);
        bicTransaction.setType(type);
        bicTransaction.setProductId(environment.getRequiredProperty("createTravelBIC.DSVN.order.productId"));
        bicTransaction.setDestroy(destroy);

        return bicTransactionRepository.save(bicTransaction);
    }


    @Override
    public BICTransaction createBICTransactionFromUpdateTravel(BaseDetail<UpdateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode, String bicResultCode ,
                                                               String clientIp, String typeTransaction) {
        BICTransaction bicTransaction = new BICTransaction();
        String clientId = JwtUtils.getClientId() ;
        Optional<Client> client = clientService.findByclientName(clientId);

        BigDecimal  ordPaidMoneyNegative = null ;
        String ordPaidMoney = null ;

        //check
        if (object != null && object.getDetail() != null) {
            BigDecimal zeroDecimal = new BigDecimal("0");
            Optional<BICTransaction> bicTransactionSuccessByOrderID = bicTransactionRepository.findBICTransactionSuccessByOrderID(object.getDetail().getOrders().getOrderId()) ;

            if(bicTransactionSuccessByOrderID.isPresent()){
                BigDecimal bicTransactionSuccessByOrderIDPaidMoney = new BigDecimal(bicTransactionSuccessByOrderID.get().getOrdPaidMoney()) ;
                ordPaidMoneyNegative = zeroDecimal.subtract(bicTransactionSuccessByOrderIDPaidMoney)  ;
                ordPaidMoney =String.valueOf(ordPaidMoneyNegative) ;

            }


            Long orderIdResponse = null;
            EJson jsonObjectData = jsonObjectReponseCreate.getJSONObject("data");
            if (jsonObjectData != null && jsonObjectData.getString("type").equalsIgnoreCase("200") ) {
                orderIdResponse = jsonObjectReponseCreate.getLong("orderId");
            }else {
                orderIdResponse = -1L ;
            }
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId(client.get().getConsumerId());
            bicTransaction.setCustomerName(object.getDetail().getOrders().getOrdBillFirstName());
            bicTransaction.setCustomerAddress((object.getDetail().getOrders().getOrdBillStreet1() == null || object.getDetail().getOrders().getOrdBillStreet1().equals("") == true) ? "-1" : object.getDetail().getOrders().getOrdBillStreet1());
            bicTransaction.setEmail(object.getDetail().getOrders().getOrdBillEmail());
            bicTransaction.setResultCode(resultCode);
            bicTransaction.setLogTimestamp(new Date());
            bicTransaction.setOrderId(orderIdResponse == null ? "-1" : String.valueOf(object.getDetail().getOrders().getOrderId()));
            bicTransaction.setOrderReference(object.getDetail().getOrders().getOrderReference());
            bicTransaction.setPhoneNumber(object.getDetail().getOrders().getOrdBillMobile());
            bicTransaction.setRequestId(object.getRequestId());
            bicTransaction.setType(typeTransaction);
            bicTransaction.setClientIp(clientIp);
            bicTransaction.setDestroy(object.getDetail().getTrv().getDestroy());

            if(object.getDetail().getTrv().getDestroy() ==1 ){
                bicTransaction.setOrdPaidMoney(ordPaidMoney);
            }

        }

        return bicTransactionRepository.save(bicTransaction);
    }

    @Override
    public BICTransaction createBICTransactionFromUpdateTravelOubox(BaseDetail<UpdateTravelInsuranceBICRequest> object,
                                                               EJson jsonObjectReponseCreate,
                                                               String resultCode, String bicResultCode ,
                                                               String clientIp, String typeTransaction,String clientId) {
        BICTransaction bicTransaction = new BICTransaction();

        Optional<Client> client = clientService.findByclientName(clientId);

        BigDecimal  ordPaidMoneyNegative = null ;
        String ordPaidMoney = null ;

        //check
        if (object != null && object.getDetail() != null) {
            BigDecimal zeroDecimal = new BigDecimal("0");
            Optional<BICTransaction> bicTransactionSuccessByOrderID = bicTransactionRepository.findBICTransactionSuccessByOrderID(object.getDetail().getOrders().getOrderId()) ;

            if(bicTransactionSuccessByOrderID.isPresent()){
                BigDecimal bicTransactionSuccessByOrderIDPaidMoney = new BigDecimal(bicTransactionSuccessByOrderID.get().getOrdPaidMoney()) ;
                ordPaidMoneyNegative = zeroDecimal.subtract(bicTransactionSuccessByOrderIDPaidMoney)  ;
                ordPaidMoney =String.valueOf(ordPaidMoneyNegative) ;

            }


            Long orderIdResponse = null;
            EJson jsonObjectData = jsonObjectReponseCreate.getJSONObject("data");
            if (jsonObjectData != null && jsonObjectData.getString("type").equalsIgnoreCase("200") ) {
                orderIdResponse = jsonObjectReponseCreate.getLong("orderId");
            }else {
                orderIdResponse = -1L ;
            }
            bicTransaction.setBicResultCode(bicResultCode);
            bicTransaction.setConsumerId(client.get().getConsumerId());
            bicTransaction.setCustomerName(object.getDetail().getOrders().getOrdBillFirstName());
            bicTransaction.setCustomerAddress((object.getDetail().getOrders().getOrdBillStreet1() == null || object.getDetail().getOrders().getOrdBillStreet1().equals("") == true) ? "-1" : object.getDetail().getOrders().getOrdBillStreet1());
            bicTransaction.setEmail(object.getDetail().getOrders().getOrdBillEmail());
            bicTransaction.setResultCode(resultCode);
            bicTransaction.setLogTimestamp(new Date());
            bicTransaction.setOrderId(orderIdResponse == null ? "-1" : String.valueOf(object.getDetail().getOrders().getOrderId()));
            bicTransaction.setOrderReference(object.getDetail().getOrders().getOrderReference());
            bicTransaction.setPhoneNumber(object.getDetail().getOrders().getOrdBillMobile());
            bicTransaction.setRequestId(object.getRequestId());
            bicTransaction.setType(typeTransaction);
            bicTransaction.setClientIp(clientIp);
            bicTransaction.setDestroy(object.getDetail().getTrv().getDestroy());

            if(object.getDetail().getTrv().getDestroy() ==1 ){
                bicTransaction.setOrdPaidMoney(ordPaidMoney);
            }

        }

        return bicTransactionRepository.save(bicTransaction);
    }
}
