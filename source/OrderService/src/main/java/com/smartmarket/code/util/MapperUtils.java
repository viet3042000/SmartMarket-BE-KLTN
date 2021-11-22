package com.smartmarket.code.util;

import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.constants.HostConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

//scope to refresh fieldsConstants and hostConstants
@Component
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapperUtils {

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    FieldsConstants fieldsConstants;

    @Autowired
    HostConstants hostConstants;

//    public CreateTravelInsuranceToBIC mapCreateObjectToBIC(CreateOrderRequest createOrderRequest) {
//        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = new CreateTravelInsuranceToBIC();
//
//        Orders orders = createOrderRequest.getOrders();
//        TRV trv = createOrderRequest.getTrv();
//        ArrayList<TRVDetail> trvDetails = createOrderRequest.getTrvDetails();
//
//        OrdersBIC ordersBIC = new OrdersBIC();
//        trvBIC trvBIC = new trvBIC();
//        ArrayList<trvDetailBIC> trvDetailsBICs = new ArrayList<>();
//
//        if (orders != null) {
////            ordersBIC.setOrderReference(orders.getOrderReference());
//            ordersBIC.setOrderid(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.order.orderId")));
//            ordersBIC.setOrdcustid(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.order.ordCustId")));
//            ordersBIC.setOrdcustmessage(environment.getProperty("createTravelBIC.DSVN.order.ordCustMessage"));
//            ordersBIC.setOrdbillfirstname(orders.getOrdBillFirstName());
//            ordersBIC.setOrdbillmobile(orders.getOrdBillMobile());
//            ordersBIC.setOrdbillstreet1(orders.getOrdBillStreet1().trim().equals("") ? "No Address" : orders.getOrdBillStreet1());
//            ordersBIC.setOrdbillemail(orders.getOrdBillEmail());
//            ordersBIC.setOrddate(orders.getOrdDate());
//            ordersBIC.setOrdstatus(orders.getOrdStatus());
//            ordersBIC.setProductId(environment.getRequiredProperty("createTravelBIC.DSVN.order.productId"));
//            ordersBIC.setOrdtotalqty(orders.getOrdTotalQty());
//            ordersBIC.setOrderpaymentmethod(orders.getOrderPaymentMethod());
//            ordersBIC.setOrdershipmodule(environment.getRequiredProperty("createTravelBIC.DSVN.order.orderShipModule"));
//            ordersBIC.setOrdisdigital(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.order.ordIsDigital")));
//            ordersBIC.setOrdtoken(environment.getRequiredProperty("createTravelBIC.DSVN.order.ordToken"));
//            ordersBIC.setOrdpaidmoney(orders.getOrdPaidMoney());
//            ordersBIC.setOrdtotal(orders.getOrdPaidMoney());
//            ordersBIC.setOrddiscountamount(orders.getOrdDiscountAmount());
//            ordersBIC.setUserId(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.order.userId")));
//            ordersBIC.setOrdsource(environment.getRequiredProperty("createTravelBIC.DSVN.order.ordSource"));
//        }
//
//
//        if (trv != null) {
//            trvBIC.setTRVID(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.trvId")));
//            trvBIC.setOrderid(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.order.orderId")));
//            trvBIC.setAmountPersons(trv.getAmountPersons());
//            trvBIC.setAmountDays(trv.getAmountDays());
//            trvBIC.setSI(new BigDecimal(environment.getRequiredProperty("createTravelBIC.DSVN.trv.si")));
//            trvBIC.setPremium(orders.getOrdPaidMoney());
//            trvBIC.setPromotion(getBool(trv.getPromotion()));
//            trvBIC.setPromotionAddress(trv.getPromotionAddress());
//            trvBIC.setPeriodTime(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.periodTime")));
//            trvBIC.setFromDate(trv.getFromDate());
//            trvBIC.setToDate(trv.getToDate());
//            trvBIC.setIssueDate(trv.getIssueDate());
//            trvBIC.setIncludePayer(getBool(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.includePayer"))));
//            trvBIC.setEndorsement(environment.getProperty("createTravelBIC.DSVN.trv.includePayer"));
//            trvBIC.setUserID(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.userID")));
//            trvBIC.setUserUpproveID(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.userUpproveID")));
//            trvBIC.setDestroy(getBool(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.destroy"))));
//            trvBIC.setStatus(getBool(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.status"))));
//            trvBIC.setWriteByHand(getBool(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.writeByHand"))));
//            trvBIC.setPrintedPaperNo(environment.getProperty("createTravelBIC.DSVN.trv.printedPaperNo"));
//            trvBIC.setCertificateForm(environment.getProperty("createTravelBIC.DSVN.trv.certificateForm"));
//            trvBIC.setModuleid(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trv.moduleId")));
//            trvBIC.setFromZoneGuid(environment.getProperty("createTravelBIC.DSVN.trv.fromZoneGuid"));
//            trvBIC.setToZoneGuid(environment.getProperty("createTravelBIC.DSVN.trv.toZoneGuid"));
//        }
//
//
//        if (trvDetails != null && trvDetails.size() > 0) {
//            for (TRVDetail trvDetail : trvDetails) {
//                trvDetailBIC trvDetailBIC = new trvDetailBIC();
//                trvDetailBIC.setDateofBirth(trvDetail.getDateOfBirth() == null ? "" : trvDetail.getDateOfBirth());
//                trvDetailBIC.setFullName(trvDetail.getFullName());
//                trvDetailBIC.setGender(trvDetail.getGender());
//                trvDetailBIC.setID(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trvDetail.id")));
//                trvDetailBIC.setTRVID(Long.parseLong(environment.getRequiredProperty("createTravelBIC.DSVN.trvDetail.trvId")));
//                trvDetailBIC.setPassportCard(trvDetail.getPassportCard());
//                trvDetailsBICs.add(trvDetailBIC);
//            }
//        }
//
//        createTravelInsuranceToBIC.setOrders(ordersBIC);
//        createTravelInsuranceToBIC.setTRV(trvBIC);
//        createTravelInsuranceToBIC.setTRVDetail(trvDetailsBICs);
//
//        return createTravelInsuranceToBIC;
//    }


//    public UpdateTravelInsuranceToBIC mapUpdateObjectToBIC(UpdateOrderRequest updateOrderRequest) {
//        UpdateTravelInsuranceToBIC updateTravelInsuranceToBIC = new UpdateTravelInsuranceToBIC();
//
//        OrderUpdate orders = updateOrderRequest.getOrders();
//        TRVUpdate trv = updateOrderRequest.getTrv();
//        ArrayList<TRVDetailUpdate> trvDetails = updateOrderRequest.getTrvDetails();
//
//        OrdersUpdateBIC ordersBIC = new OrdersUpdateBIC();
//        trvUpdateBIC trvBIC = new trvUpdateBIC();
//        ArrayList<trvDetailUpdateBIC> trvDetailsBICs = new ArrayList<>();
//
//        if (orders != null) {
////            ordersBIC.setOrderReference(orders.getOrderReference());
////            ordersBIC.setOrderid(Long.parseLong(orders.getOrderId()));
//            ordersBIC.setOrdbillfirstname(orders.getOrdBillFirstName());
//            ordersBIC.setOrdbillmobile(orders.getOrdBillMobile());
//            ordersBIC.setOrdbillstreet1(orders.getOrdBillStreet1().trim().equals("") ? "No Address" : orders.getOrdBillStreet1());
//            ordersBIC.setOrdbillemail(orders.getOrdBillEmail());
//            ordersBIC.setOrdstatus(orders.getOrdStatus());
//        }
//
//        if (trv != null) {
//            trvBIC.setTRVID(trv.getTrvId());
//            trvBIC.setOrderid(Long.parseLong(trv.getOrderId()));
//            trvBIC.setDestroy(getBool(trv.getDestroy()));
//        }
//
//        if (trvDetails != null && trvDetails.size() > 0) {
//            for (TRVDetailUpdate trvDetail : trvDetails) {
//                trvDetailUpdateBIC trvDetailBIC = new trvDetailUpdateBIC();
//                trvDetailBIC.setDateofBirth(trvDetail.getDateOfBirth() == null ? "" : trvDetail.getDateOfBirth());
//                trvDetailBIC.setFullName(trvDetail.getFullName());
//                trvDetailBIC.setGender(trvDetail.getGender());
//                trvDetailBIC.setTRVID(trvDetail.getTrvId());
//                trvDetailBIC.setPassportCard(trvDetail.getPassportCard());
//                trvDetailsBICs.add(trvDetailBIC);
//            }
//        }
//
//        updateTravelInsuranceToBIC.setOrders(ordersBIC);
//        updateTravelInsuranceToBIC.setTRV(trvBIC);
//        updateTravelInsuranceToBIC.setTRVDetail(trvDetailsBICs);
//
//        return updateTravelInsuranceToBIC;
//    }


//    public CreateOrderRequest queryCreateObjectToBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, ResponseEntity<String> resultBIC, String token, String requestId) throws APIAccessException {
//
//        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
//
//        EJson jsonObjectResultBIC = new EJson(resultBIC.getBody());
//        EJson ordersBIC = jsonObjectResultBIC.getJSONObject("Orders");
//        EJson trvBIC = jsonObjectResultBIC.getJSONObject("TRV");
//        List<EJson> trvDetailsBIC = jsonObjectResultBIC.getJSONArray("TRVDetail");
//
//        Orders orders = new Orders();
//        TRV trv = new TRV();
//        ArrayList<TRVDetail> trvDetails = new ArrayList<>();
//
//        orders.setOrderId(String.valueOf(ordersBIC.getLong("Orderid")));
////        orders.setOrderReference(ordersBIC.getString("OrderReference") == null ? null : ordersBIC.getString("OrderReference").toString());
////                orders.setOrdCustId(ordersBIC.getLong("Ordcustid"));
////                orders.setOrdCustMessage(ordersBIC.getString("Ordcustmessage") == null ? null : ordersBIC.getString("Ordcustmessage").toString());
//        orders.setOrdBillFirstName(ordersBIC.getString("Ordbillfirstname") == null ? null : ordersBIC.getString("Ordbillfirstname").toString());
//        orders.setOrdBillMobile(ordersBIC.getString("Ordbillmobile") == null ? null : ordersBIC.getString("Ordbillmobile").toString());
//        orders.setOrdBillStreet1(ordersBIC.getString("Ordbillstreet1") == null ? null : ordersBIC.getString("Ordbillstreet1").toString());
//        orders.setOrdBillEmail(ordersBIC.getString("Ordbillemail") == null ? null : ordersBIC.getString("Ordbillemail").toString());
//        orders.setOrdDate(ordersBIC.getString("Orddate") == null ? null : ordersBIC.getString("Orddate").toString());
//        orders.setOrdStatus(ordersBIC.getLong("Ordstatus"));
////                orders.setProductId(ordersBIC.getString("ProductId") == null ? null : ordersBIC.getString("ProductId").toString());
//        orders.setOrdTotalQty(ordersBIC.getLong("Ordtotalqty"));
//        orders.setOrderPaymentMethod(ordersBIC.getLong("Orderpaymentmethod"));
////                orders.setOrderShipModule(ordersBIC.getString("Ordershipmodule") == null ? null : ordersBIC.getString("Ordershipmodule").toString());
////                orders.setOrdIsDigital(ordersBIC.getLong("Ordisdigital"));
////                orders.setOrdToken(ordersBIC.getString("Ordtoken") == null ? null : ordersBIC.getString("Ordtoken").toString());
//        orders.setOrdPaidMoney(ordersBIC.getBigDecimal("Ordpaidmoney"));
////                orders.setOrdTotal(ordersBIC.getBigDecimal("Ordtotal"));
//        orders.setOrdDiscountAmount(ordersBIC.getBigDecimal("Orddiscountamount"));
////                orders.setUserId(ordersBIC.getLong("UserID"));
//
//        trv.setTrvId(trvBIC.getLong("TRVID"));
//        trv.setOrderId(String.valueOf(trvBIC.getLong("Orderid")));
//        trv.setAmountPersons(trvBIC.getLong("AmountPersons"));
//        trv.setAmountDays(trvBIC.getLong("AmountDays"));
////                trv.setSi(trvBIC.getBigDecimal("SI"));
////                trv.setPremium(trvBIC.getBigDecimal("Premium"));
//        trv.setPromotion(getLongFromBool(trvBIC.getBoolean("Promotion")));
//        trv.setPromotionAddress(trvBIC.getString("PromotionAddress"));
////                trv.setPeriodTime(String.valueOf(trvBIC.getLong("PeriodTime")));
//        trv.setFromDate(trvBIC.getString("FromDate"));
//        trv.setToDate(trvBIC.getString("ToDate"));
//        trv.setIssueDate(trvBIC.getString("IssueDate"));
////                trv.setIncludePayer(getLongFromBool(trvBIC.getBoolean("IncludePayer")));
////                trv.setEndorsement(trvBIC.getString("Endorsement"));
////                trv.setUserID(trvBIC.getLong("UserID"));
////                trv.setUserUpproveID(trvBIC.getLong("UserUpproveID"));
//        trv.setDestroy(getLongFromBool(trvBIC.getBoolean("Destroy")));
//        trv.setStatus(getLongFromBool(trvBIC.getBoolean("Status")));
////                trv.setWriteByHand(getLongFromBool(trvBIC.getBoolean("WriteByHand")));
////                trv.setPrintedPaperNo(trvBIC.getString("PrintedPaperNo"));
////                trv.setCertificateForm(trvBIC.getString("PrintedPaperNo"));
////                trv.setModuleId(trvBIC.getLong("Moduleid"));
//
//        for (int i = 0; i < trvDetailsBIC.size(); i++) {
//            EJson trvDetailBIC = trvDetailsBIC.get(i);
//            TRVDetail trvDetail = new TRVDetail();
//            trvDetail.setDateOfBirth(convertDOB(trvDetailBIC.getString("DateofBirth")));
//            trvDetail.setFullName(trvDetailBIC.getString("FullName"));
//            trvDetail.setGender(trvDetailBIC.getLong("Gender"));
//            trvDetail.setId(trvDetailBIC.getLong("ID"));
//            trvDetail.setTrvId(trvDetailBIC.getLong("TRVID"));
//            trvDetail.setPassportCard(trvDetailBIC.getString("PassportCard"));
//            trvDetails.add(trvDetail);
//        }
//
//        createOrderRequest.setOrders(orders);
//        createOrderRequest.setTrv(trv);
//        createOrderRequest.setTrvDetails(trvDetails);
//
//
//        return createOrderRequest;
//    }

    public static boolean getBool(Long value) {
        if (value.equals(1L)) {
            return true;
        }
        return false;
    }

    public static Long getLongFromBool(Boolean value) {
        if (value != null) {
            if (value == true) {
                return 1L;
            }
            return 0L;
        }
        return null;
    }


    public static String convertDOB(String DOB){
        String DOBResponse = "" ;
        if (!StringUtils.isEmpty(DOB)){
            DOBResponse = DOB.substring(0,DOB.indexOf("T")) ;
            return DOBResponse ;
        }
        return DOBResponse ;
    }

}
