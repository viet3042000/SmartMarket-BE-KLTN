package com.smartmarket.code.service.impl;

import com.google.gson.Gson;
import com.nimbusds.jose.util.IOUtils;
import com.smartmarket.code.config.RequestWrapper;
import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.service.BICTransactionExceptionService;
import com.smartmarket.code.service.BICTransactionService;
import com.smartmarket.code.util.EJson;
import com.smartmarket.code.util.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Service
public class BICTransactionServiceExceptionImp implements BICTransactionExceptionService {


    @Autowired
    HostConstants hostConstants;

    @Autowired
    ConfigurableEnvironment environment;

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

        String messasgeId = "no requestId" ;
        String orderReference ="-1" ;
        String orderId = "-1";
        String customerName = "-1";
        String phoneNumber ="-1";
        String email = "-1";
        String ordPaidMoney = "-1";
        String consumerId = "DSVN";
        String fromDate = "-1";
        String toDate = "-1";
        String ordDate = "-1";
        String productId = environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
        String customerAddress = "-1";
        String clientIp = "unknown" ;
        String type = "unknown" ;

        if(Utils.isJSONValid(jsonString)){
            EJson requestBody = new EJson(jsonString);
            AntPathMatcher matcher = new AntPathMatcher();
            String URLRequest = request.getRequestURI();

            if (matcher.match(hostConstants.URL_CREATE, URLRequest) || matcher.match(hostConstants.URL_UPDATE, URLRequest)) {

                if(matcher.match(hostConstants.URL_CREATE, URLRequest)){
                    type = Constant.TYPE_CREATE ;
                }else if (matcher.match(hostConstants.URL_UPDATE, URLRequest)){
                    type = Constant.TYPE_UPDATE ;
                }
                messasgeId = requestBody.getString("requestId") == null ? "no requestId" : requestBody.getString("requestId");
                EJson detail = (requestBody.getJSONObject("detail"));


                //convert jsonobject to CreateTravelInsuranceBICRequest
                Gson gson = new Gson(); // Or use new GsonBuilder().create();
                CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = gson.fromJson(detail.jsonToString(), CreateTravelInsuranceBICRequest.class);

                //set properties detail
                if (createTravelInsuranceBICRequest.getOrders() != null) {
                    orderReference = createTravelInsuranceBICRequest.getOrders().getOrderReference() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrderReference();
                    orderId = "-1";
                    customerName = createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillFirstName();
                    phoneNumber = createTravelInsuranceBICRequest.getOrders().getOrdBillMobile() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillMobile();
                    email = createTravelInsuranceBICRequest.getOrders().getOrdBillEmail() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillEmail();
                    ordPaidMoney = createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdPaidMoney().toString();
                    consumerId = "DSVN";

                    if (createTravelInsuranceBICRequest.getTrv() != null) {
                        fromDate = createTravelInsuranceBICRequest.getTrv().getFromDate() == null ? "-1" : createTravelInsuranceBICRequest.getTrv().getFromDate();
                        toDate = createTravelInsuranceBICRequest.getTrv().getToDate() == null ? "-1" : createTravelInsuranceBICRequest.getTrv().getToDate();
                    }

                    ordDate = createTravelInsuranceBICRequest.getOrders().getOrdDate() == null ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdDate();
                    productId =environment.getRequiredProperty("createTravelBIC.DSVN.order.productId");
                    customerAddress = (createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1() == null || createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1().equals("") == true) ? "-1" : createTravelInsuranceBICRequest.getOrders().getOrdBillStreet1();
                    clientIp = Utils.getClientIp(request) ;

                    bicTransaction = bicTransactionService.createBICTransactionParameter(messasgeId, orderReference, orderId, customerName,
                            phoneNumber, email, ordPaidMoney, consumerId,
                            fromDate, toDate, new Date(), resultCode, bicResultCode,
                            ordDate, productId, customerAddress,clientIp,type);
                }
            }
        }else {
            clientIp = Utils.getClientIp(request) ;
            bicTransaction = bicTransactionService.createBICTransactionParameter("Format not True", orderReference, orderId, customerName,
                    phoneNumber, email, ordPaidMoney, consumerId,
                    fromDate, toDate, new Date(), resultCode, bicResultCode,
                    ordDate, productId, customerAddress,clientIp,type);
        }

        return bicTransaction ;
    }
}