package com.smartmarket.code.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Client;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.UpdateTravelInsuranceToBIC;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.service.ClientService;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.JwtUtils;
import com.smartmarket.code.util.MapperUtils;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class BICTransactionServiceExceptionImp implements BICTransactionExceptionService {


    @Autowired
    HostConstants hostConstants;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    BICTransactionService bicTransactionService;

    @Autowired
    private BICTransactionRepository bicTransactionRepository;

    @Autowired
    ClientService clientService ;

    @Autowired
    MapperUtils mapperUtils;

    @Override
    public BICTransaction createBICTransactionFromRequest(HttpServletRequest request, String resultCode, String bicResultCode) {
        //add BICTransaction
        BICTransaction bicTransaction = null ;

        String clientId = JwtUtils.getClientId() ;
        Optional<Client> client = clientService.findByclientName(clientId);
        String jsonString = null;
        try {
            request = new RequestWrapper(request);
            jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String messasgeId = "no requestId" ;
        String orderReference =null ;
        String orderId = "-1";
        String customerName = null;
        String phoneNumber =null;
        String email = null;
        String ordPaidMoney = null;
        String consumerId = client.get().getConsumerId();
        String fromDate = null;
        String toDate = null;
        String ordDate = null;
        String productId = environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
        String customerAddress = null;
        String clientIp = "unknown" ;
        String type = "unknown" ;
        Long destroy = 0L ;


        if(Utils.isJSONValid(jsonString)){
            EJson requestBody = new EJson(jsonString);
            AntPathMatcher matcher = new AntPathMatcher();
            String URLRequest = request.getRequestURI();

            if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {


                messasgeId = requestBody.getString("requestId") == null ? "no requestId" : requestBody.getString("requestId");
                EJson detail = (requestBody.getJSONObject("detail"));

                if(matcher.match(hostConstants.URL_CREATE, URLRequest)){
                    type = Constant.TYPE_CREATE ;
//                    destroy =  0L ;
                }else if (matcher.match(hostConstants.URL_UPDATE, URLRequest)){
                    type = Constant.TYPE_UPDATE ;
//                    destroy =  createTravelInsuranceBICRequest.getTrv().getDestroy() ;
                }

                //convert jsonobject to CreateTravelInsuranceBICRequest
                Gson gson = new Gson(); // Or use new GsonBuilder().create();
                CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = null ;
                try{
                    createTravelInsuranceBICRequest = gson.fromJson(detail.jsonToString(), CreateTravelInsuranceBICRequest.class) ;
                }catch (JsonSyntaxException ex){
                    clientIp = Utils.getClientIp(request) ;
                    bicTransaction = bicTransactionService.createBICTransactionParameter("Format not True", orderReference, orderId, customerName,
                            phoneNumber, email, ordPaidMoney, consumerId,
                            fromDate, toDate, new Date(), resultCode, bicResultCode,
                            ordDate, productId, customerAddress,clientIp,type,destroy);
                    return bicTransaction ;
                }



                //set properties detail
                if (createTravelInsuranceBICRequest.getOrders() != null) {

                    if(type.equals(Constant.TYPE_CREATE)){
                        destroy =  0L ;
                        ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString() ;
                    }else if (type.equals(Constant.TYPE_UPDATE)){
                        destroy =  createTravelInsuranceBICRequest.getTrv().getDestroy() ;
                        if(destroy == 1 ){
                            BigDecimal zeroDecimal = new BigDecimal("0");
                            if (createTravelInsuranceBICRequest.getOrders().getOrderId() !=  null ){
                                Optional<BICTransaction> bicTransactionSuccessByOrderID = bicTransactionRepository.findBICTransactionSuccessByOrderID(createTravelInsuranceBICRequest.getOrders().getOrderId()) ;

                                if(bicTransactionSuccessByOrderID.isPresent()){
                                    BigDecimal bicTransactionSuccessByOrderIDPaidMoney = new BigDecimal(bicTransactionSuccessByOrderID.get().getOrdPaidMoney()) ;
                                    BigDecimal ordPaidMoneyNegative = zeroDecimal.subtract(bicTransactionSuccessByOrderIDPaidMoney)  ;
                                    ordPaidMoney =String.valueOf(ordPaidMoneyNegative) ;
                                }

                            }

                        }
                    }

                    orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrderReference();

                    customerName = (createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName() == null) ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName();
                    phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillMobile();
                    email = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
//                    ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();

                    if (createTravelInsuranceBICRequest.getTrv() != null) {
                        fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getFromDate();
                        toDate = createTravelInsuranceBICRequest.getTrv().getToDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getToDate();
                    }

                    ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdDate();
                    productId =environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
                    customerAddress = (createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1() == null || createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1().equals("") == true) ? "no address" : createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1();
                    clientIp = Utils.getClientIp(request) ;

                    bicTransaction = bicTransactionService.createBICTransactionParameter(messasgeId, orderReference, orderId, customerName,
                            phoneNumber, email, ordPaidMoney, consumerId,
                            fromDate, toDate, new Date(), resultCode, bicResultCode,
                            ordDate, productId, customerAddress,clientIp,type,destroy);
                }

//                if (detail != null) {
//
//                    orderReference = detail.getString("orderReference") == null ? "-1" : detail.getString("orderReference");
//                    orderId = "-1";
//                    customerName = detail.getString("ordBillFirstName") == null ? "-1" : detail.getString("ordBillFirstName");
//                    phoneNumber = detail.getString("ordBillMobile") == null ? "-1" : detail.getString("ordBillMobile");
//                    email = detail.getString("ordBillEmail") == null ? "-1" : detail.getString("ordBillEmail");
//                    ordPaidMoney = detail.getBigDecimal("ordBillEmail") == null ? "-1" : detail.getBigDecimal("ordBillEmail") .toString();
//                    consumerId = "DSVN";
//
//                    if (createTravelInsuranceBICRequest.getTrv() != null) {
//                        fromDate = detail.getBigDecimal("fromDate")  == null ? "-1" : createTravelInsuranceBICRequest.getTrv().getFromDate();
//                        toDate =detail.getBigDecimal("toDate") == null ? "-1" : createTravelInsuranceBICRequest.getTrv().getToDate();
//                    }
//
//                    ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdDate();
//                    productId =environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
//                    customerAddress = (createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1() == null || createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1().equals("") == true) ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1();
//                    clientIp = Utils.getClientIp(request) ;
//
//                    bicTransaction = bicTransactionService.createBICTransactionParameter(messasgeId, orderReference, orderId, customerName,
//                            phoneNumber, email, ordPaidMoney, consumerId,
//                            fromDate, toDate, new Date(), resultCode, bicResultCode,
//                            ordDate, productId, customerAddress,clientIp,type);
//                }

            }
        }else {
            clientIp = Utils.getClientIp(request) ;
            bicTransaction = bicTransactionService.createBICTransactionParameter("Format not True", orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type,destroy);
        }

        return bicTransaction ;
    }


    public BICTransaction createBICTransactionFromRequestCreate(BaseDetail<CreateTravelInsuranceBICRequest> requestCreateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId){
        //add BICTransaction
        BICTransaction bicTransaction = null ;
        Optional<Client> client = clientService.findByclientName(clientId);

        String messasgeId = "no requestId" ;
        String orderReference =null ;
        String orderId = "-1";
        String customerName = null;
        String phoneNumber =null;
        String email = null;
        String ordPaidMoney = null;
        String consumerId = client.get().getConsumerId();
        String fromDate = null;
        String toDate = null;
        String ordDate = null;
        String productId = environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
        String customerAddress = null;
        String clientIp = "unknown" ;
        String type = "unknown" ;
        Long destroy = 0L ;

        //Create BIC
//        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(requestCreateTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        Gson gson = new Gson();
        responseCreate = gson.toJson(requestCreateTravelInsuranceBICRequest.getDetail());
        JSONObject detail = new JSONObject(responseCreate);

        messasgeId = requestCreateTravelInsuranceBICRequest.getRequestId() == null ? "no requestId" : requestCreateTravelInsuranceBICRequest.getRequestId();
        type = Constant.TYPE_CREATE ;

        //convert jsonobject to CreateTravelInsuranceBICRequest
        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = null ;
        try{
            createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);
        }catch (JsonSyntaxException ex){
            bicTransaction = bicTransactionService.createBICTransactionParameter("Format not True", orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type,destroy);
            return bicTransaction ;
        }

        //set properties detail
        if (createTravelInsuranceBICRequest.getOrders() != null) {

            if(type.equals(Constant.TYPE_CREATE)){
                destroy =  0L ;
                ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString() ;
            }else if (type.equals(Constant.TYPE_UPDATE)){
                destroy =  createTravelInsuranceBICRequest.getTrv().getDestroy() ;
                if(destroy == 1 ){
                    BigDecimal zeroDecimal = new BigDecimal("0");
                    if (createTravelInsuranceBICRequest.getOrders().getOrderId() !=  null ){
                        Optional<BICTransaction> bicTransactionSuccessByOrderID = bicTransactionRepository.findBICTransactionSuccessByOrderID(createTravelInsuranceBICRequest.getOrders().getOrderId()) ;

                        if(bicTransactionSuccessByOrderID.isPresent()){
                            BigDecimal bicTransactionSuccessByOrderIDPaidMoney = new BigDecimal(bicTransactionSuccessByOrderID.get().getOrdPaidMoney()) ;
                            BigDecimal ordPaidMoneyNegative = zeroDecimal.subtract(bicTransactionSuccessByOrderIDPaidMoney)  ;
                            ordPaidMoney =String.valueOf(ordPaidMoneyNegative) ;
                        }

                    }

                }
            }

            orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrderReference();

            customerName = (createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName() == null) ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName();
            phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillMobile();
            email = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
//                    ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();

            if (createTravelInsuranceBICRequest.getTrv() != null) {
                fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getFromDate();
                toDate = createTravelInsuranceBICRequest.getTrv().getToDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getToDate();
            }

            ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdDate();
            productId =environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
            customerAddress = (createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1() == null || createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1().equals("") == true) ? "no address" : createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1();

            bicTransaction = bicTransactionService.createBICTransactionParameterOutbox(messasgeId, orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type,destroy,clientId);
        }

        return bicTransaction;
    }


    public BICTransaction createBICTransactionFromRequestUpdate(BaseDetail<UpdateTravelInsuranceBICRequest> requestUpdateTravelInsuranceBICRequest, String resultCode, String bicResultCode, String clientId){
        //add BICTransaction
        BICTransaction bicTransaction = null ;
        Optional<Client> client = clientService.findByclientName(clientId);

        String messasgeId = "no requestId" ;
        String orderReference =null ;
        String orderId = "-1";
        String customerName = null;
        String phoneNumber =null;
        String email = null;
        String ordPaidMoney = null;
        String consumerId = client.get().getConsumerId();
        String fromDate = null;
        String toDate = null;
        String ordDate = null;
        String productId = environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
        String customerAddress = null;
        String clientIp = "unknown" ;
        String type = "unknown" ;
        Long destroy = 0L ;

        //Create BIC
//        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = mapperUtils.mapCreateObjectToBIC(requestCreateTravelInsuranceBICRequest.getDetail());
        String responseCreate = null;
        Gson gson = new Gson();
        responseCreate = gson.toJson(requestUpdateTravelInsuranceBICRequest.getDetail());
        JSONObject detail = new JSONObject(responseCreate);

        messasgeId = requestUpdateTravelInsuranceBICRequest.getRequestId() == null ? "no requestId" : requestUpdateTravelInsuranceBICRequest.getRequestId();
        type = Constant.TYPE_UPDATE ;

        //convert jsonobject to CreateTravelInsuranceBICRequest
        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = null ;
        try{
            createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);
        }catch (JsonSyntaxException ex){
            bicTransaction = bicTransactionService.createBICTransactionParameter("Format not True", orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type,destroy);
            return bicTransaction ;
        }

        //set properties detail
        if (createTravelInsuranceBICRequest.getOrders() != null) {

            if(type.equals(Constant.TYPE_CREATE)){
                destroy =  0L ;
                ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString() ;
            }else if (type.equals(Constant.TYPE_UPDATE)){
                destroy =  createTravelInsuranceBICRequest.getTrv().getDestroy() ;
                if(destroy == 1 ){
                    BigDecimal zeroDecimal = new BigDecimal("0");
                    if (createTravelInsuranceBICRequest.getOrders().getOrderId() !=  null ){
                        Optional<BICTransaction> bicTransactionSuccessByOrderID = bicTransactionRepository.findBICTransactionSuccessByOrderID(createTravelInsuranceBICRequest.getOrders().getOrderId()) ;

                        if(bicTransactionSuccessByOrderID.isPresent()){
                            BigDecimal bicTransactionSuccessByOrderIDPaidMoney = new BigDecimal(bicTransactionSuccessByOrderID.get().getOrdPaidMoney()) ;
                            BigDecimal ordPaidMoneyNegative = zeroDecimal.subtract(bicTransactionSuccessByOrderIDPaidMoney)  ;
                            ordPaidMoney =String.valueOf(ordPaidMoneyNegative) ;
                        }

                    }

                }
            }

            orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrderReference();

            customerName = (createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName() == null) ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName();
            phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillMobile();
            email = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
//                    ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();

            if (createTravelInsuranceBICRequest.getTrv() != null) {
                fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getFromDate();
                toDate = createTravelInsuranceBICRequest.getTrv().getToDate() == null ? null : createTravelInsuranceBICRequest.getTrv().getToDate();
            }

            ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? null : createTravelInsuranceBICRequest.getOrders().getOrdDate();
            productId =environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
            customerAddress = (createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1() == null || createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1().equals("") == true) ? "no address" : createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1();

            bicTransaction = bicTransactionService.createBICTransactionParameterOutbox(messasgeId, orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type,destroy,clientId);
        }

        return bicTransaction;
    }
}