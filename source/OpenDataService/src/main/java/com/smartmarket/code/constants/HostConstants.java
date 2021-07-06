package com.smartmarket.code.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HostConstants {

    @Value("${api.loginTravelBIC}")
    public String BIC_HOST_LOGIN;
//    @Value("${api.createTravelBIC}")
    public String BIC_HOST_CREATE = "https://app.bic.vn/EbizApiTest/api/v1/TRV/Create";
    @Value("${api.updateTravelBIC}")
    public String BIC_HOST_UPDATE;
//    @Value("${api.getTravelBICByOrderId}")
    public String BIC_HOST_GET_BY_ORDER_ID ="https://app.bic.vn/EbizApiTest/api/v1/TRV/Get/{id}" ;
    @Value("${api.getTravelBICByOderReference}")
    public String BIC_HOST_GET_BY_ORDER_REFERANCE;

    public String URL_CREATE =  "/insurance/travel-insurance-service/v1/create-bic-travel-insurance/**" ;
    public String URL_UPDATE =  "/insurance/travel-insurance-service/v1/change-bic-travel-insurance/**" ;
}
