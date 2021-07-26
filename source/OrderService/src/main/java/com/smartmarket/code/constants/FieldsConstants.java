package com.smartmarket.code.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FieldsConstants{

    @Value("${config}")
    private String config;

    //UPDATE API DSVN
    @Value("${updateTravelBIC.DSVN.trv.si}")
    public String updateTrvSi;

    @Value("${updateTravelBIC.DSVN.trv.includePayer}")
    public String updateTrvIncludePayer ;

    @Value("${updateTravelBIC.DSVN.trv.endorsement}")
    public String updateTrvEndorsement;

    @Value("${updateTravelBIC.DSVN.trv.userID}")
    public String updateTrvUserID;

    @Value("${updateTravelBIC.DSVN.trv.userUpproveID}")
    public String updateTrvUserUpproveID ;

    @Value("${updateTravelBIC.DSVN.trv.writeByHand}")
    public String updateTrvWriteByHand;

    @Value("${updateTravelBIC.DSVN.trv.printedPaperNo}")
    public String updateTrvPrintedPaperNo;

    @Value("${updateTravelBIC.DSVN.trv.certificateForm}")
    public String updateTrvCertificateForm;

    @Value("${updateTravelBIC.DSVN.trv.moduleId}")
    public String updateTrvModuleId;

    @Value("${updateTravelBIC.DSVN.trv.periodTime}")
    public String updateTrvPeriodTime;

    @Value("${updateTravelBIC.DSVN.trvDetail.id}")
    public String updateTrvDetailId;

    @Value("${updateTravelBIC.DSVN.trvDetail.trvId}")
    public String updateTrvDetailTrvId;

    @Value("${updateTravelBIC.DSVN.order.userId}")
    public String updateOrderUserId;

    @Value("${updateTravelBIC.DSVN.order.ordToken}")
    public String updateOrderOrdToken;

    @Value("${updateTravelBIC.DSVN.order.ordIsDigital}")
    public String updateOrderOrdIsDigital;

    @Value("${updateTravelBIC.DSVN.order.ordCustId}")
    public String updateOrderOrdCustId;

    @Value("${updateTravelBIC.DSVN.order.ordCustMessage}")
    public String updateOrderOrdCustMessage;

    @Value("${updateTravelBIC.DSVN.order.productId}")
    public String updateOrderProductId;

    @Value("${updateTravelBIC.DSVN.order.orderShipModule}")
    public String updateOrderOrderShipModule;

    @Value("${updateTravelBIC.DSVN.order.ordSource}")
    public String updateOrderOrdSource;

    //CREATE API DSVN

    @Value("${createTravelBIC.DSVN.order.orderId}")
    public String createOrderOrderId;

    @Value("${createTravelBIC.DSVN.order.userId}")
    public String createOrderUserId;


    @Value("${createTravelBIC.DSVN.order.ordToken}")
    public String createOrderOrdToken;

    @Value("${createTravelBIC.DSVN.order.ordIsDigital}")
    public String createOrderOrdIsDigital;

    @Value("${createTravelBIC.DSVN.order.ordCustId}")
    public String createOrderOrdCustId;

    @Value("${createTravelBIC.DSVN.order.ordCustMessage}")
    public String createOrderOrdCustMessage;

    @Value("${createTravelBIC.DSVN.order.productId}")
    public String createOrderProductId;

    @Value("${createTravelBIC.DSVN.order.orderShipModule}")
    public String createOrderOrderShipModule;

    @Value("${createTravelBIC.DSVN.order.ordSource}")
    public String createOrderOrdSource;

    @Value("${createTravelBIC.DSVN.trv.trvId}")
    public String createTrvTrvId;

    @Value("${createTravelBIC.DSVN.trv.orderId}")
    public String createTrvOrderId;

    @Value("${createTravelBIC.DSVN.trv.si}")
    public String createTrvSi;

    @Value("${createTravelBIC.DSVN.trv.includePayer}")
    public String createTrvIncludePayer;

    @Value("${createTravelBIC.DSVN.trv.endorsement}")
    public String createTrvEndorsement;

    @Value("${createTravelBIC.DSVN.trv.userID}")
    public String createTrvUserID;

    @Value("${createTravelBIC.DSVN.trv.userUpproveID}")
    public String createTrvUserUpproveID;

    @Value("${createTravelBIC.DSVN.trv.destroy}")
    public String createTrvDestroy;

    @Value("${createTravelBIC.DSVN.trv.status}")
    public String createTrvStatus;

    @Value("${createTravelBIC.DSVN.trv.writeByHand}")
    public String createTrvWriteByHand;

    @Value("${createTravelBIC.DSVN.trv.printedPaperNo}")
    public String createTrvPrintedPaperNo;

    @Value("${createTravelBIC.DSVN.trv.certificateForm}")
    public String createTrvCertificateForm;

    @Value("${createTravelBIC.DSVN.trv.moduleId}")
    public String createTrvModuleId;

    @Value("${createTravelBIC.DSVN.trv.periodTime}")
    public String createTrvPeriodTime;

    @Value("${createTravelBIC.DSVN.trvDetail.id}")
    public String createTrvDetailId;

    @Value("${createTravelBIC.DSVN.trvDetail.trvId}")
    public String createTrvDetailTrvId;

    @Value("${createTravelBIC.DSVN.trv.fromZoneGuid}")
    public String createTrvFromZoneGuid;

    @Value("${createTravelBIC.DSVN.trv.toZoneGuid}")
    public String createTrvToZoneGuid;





}
