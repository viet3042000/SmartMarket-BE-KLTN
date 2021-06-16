package com.smartmarket.code.service.impl;

import com.google.gson.Gson;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Service
public class BICTransactionServiceExceptionImp implements BICTransactionExceptionService {

    @Autowired
    private BICTransactionRepository bicTransactionRepository;

    @Autowired
    private FieldsConstants fieldsConstants;

    @Autowired
    HostConstants hostConstants;


    @Autowired
    BICTransactionService bicTransactionService;

    @Override
    public BICTransaction createBICTransactionFromRequest(HttpServletRequest request, String resultCode, String bicResultCode) {
        //add BICTransaction
        BICTransaction bicTransaction = null ;

        String jsonString = null;
        try {
            request = new RequestWrapper(request);
            jsonString = IOUtils.readInputStreamToString(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject requestBody = new JSONObject(jsonString);
        AntPathMatcher matcher = new AntPathMatcher();
        String URLRequest = request.getRequestURI();
        if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {
            String messasgeId = requestBody.getString("requestId");
            JSONObject detail = (requestBody.getJSONObject("detail"));

            //convert jsonobject to CreateTravelInsuranceBICRequest
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);

            String orderReference ="-1" ;
            String orderId = "-1";
            String customerName = "DSVN";
            String phoneNumber ="-1";
            String email = "-1";
            String ordPaidMoney = "-1";
            String consumerId = "DSVN";
            String fromDate = "-1";
            String toDate = "-1";
//            String resultCode = ResponseCode.CODE.ERROR_IN_BACKEND;
//            String bicResultCode = HttpStatus.REQUEST_TIMEOUT.toString();
            String ordDate = "-1";
            String productId = fieldsConstants.createOrderProductId;
            String customerAddress = "default";
            //set properties detail
            if(createTravelInsuranceBICRequest.getOrders() != null ){
                 orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrderReference();
                 orderId = "-1";
                 customerName = "DSVN";
                 phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() ;
                 email = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail()  == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
                 ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
                 consumerId = "DSVN";
                 if(createTravelInsuranceBICRequest.getTrv()!= null ){
                     fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate() == null ? "-1" : createTravelInsuranceBICRequest.getTrv().getFromDate();
                     toDate = createTravelInsuranceBICRequest.getTrv().getToDate()== null ? "-1" : createTravelInsuranceBICRequest.getTrv().getToDate();
                 }
//            String resultCode = ResponseCode.CODE.ERROR_IN_BACKEND;
//            String bicResultCode = HttpStatus.REQUEST_TIMEOUT.toString();
                 ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdDate();
                 productId = fieldsConstants.createOrderProductId;
                 customerAddress = "default";

                bicTransaction =  bicTransactionService.createBICTransactionParameter(messasgeId, orderReference, orderId, customerName,
                        phoneNumber, email, ordPaidMoney, consumerId,
                        fromDate, toDate, new Date(), resultCode, bicResultCode,
                        ordDate, productId, customerAddress);
            }

        }
        return bicTransaction ;

    }
}