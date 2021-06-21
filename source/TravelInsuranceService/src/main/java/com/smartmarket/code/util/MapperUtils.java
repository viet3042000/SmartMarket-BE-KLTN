package com.smartmarket.code.util;

import com.smartmarket.code.constants.FieldsConstants;
import com.smartmarket.code.constants.HostConstants;
import com.smartmarket.code.exception.APIAccessException;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.entity.Orders;
import com.smartmarket.code.request.entity.TRV;
import com.smartmarket.code.request.entity.TRVDetail;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.OrdersBIC;
import com.smartmarket.code.request.entityBIC.trvBIC;
import com.smartmarket.code.request.entityBIC.trvDetailBIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//scope to refresh fieldsConstants and hostConstants
@Component
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapperUtils {

    @Autowired
    FieldsConstants fieldsConstants;

    @Autowired
    HostConstants hostConstants;

    public CreateTravelInsuranceToBIC mapCreateObjectToBIC(CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest) {
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = new CreateTravelInsuranceToBIC();

        try {
            Orders orders = createTravelInsuranceBICRequest.getOrders();
            TRV trv = createTravelInsuranceBICRequest.getTrv();
            ArrayList<TRVDetail> trvDetails = createTravelInsuranceBICRequest.getTrvDetails();

            OrdersBIC ordersBIC = new OrdersBIC();
            trvBIC trvBIC = new trvBIC();
            ArrayList<trvDetailBIC> trvDetailsBICs = new ArrayList<>();

            if (orders != null) {
                ordersBIC.setOrderReference(orders.getOrderReference());
                ordersBIC.setOrderid(Long.parseLong(fieldsConstants.createOrderOrderId));
                ordersBIC.setOrdcustid(Long.parseLong(fieldsConstants.createOrderOrdCustId));
                ordersBIC.setOrdcustmessage(fieldsConstants.createOrderOrdCustMessage);
                ordersBIC.setOrdbillfirstname(orders.getOrdBillFirstName());
                ordersBIC.setOrdbillmobile(orders.getOrdBillMobile());
                ordersBIC.setOrdbillstreet1(orders.getOrdBillStreet1().equals("") == true ? "Not Address" : orders.getOrdBillStreet1() );
                ordersBIC.setOrdbillemail(orders.getOrdBillEmail());
                ordersBIC.setOrddate(orders.getOrdDate());
                ordersBIC.setOrdstatus(orders.getOrdStatus());
                ordersBIC.setProductId(fieldsConstants.createOrderProductId);
                ordersBIC.setOrdtotalqty(orders.getOrdTotalQty());
                ordersBIC.setOrderpaymentmethod(orders.getOrderPaymentMethod());
                ordersBIC.setOrdershipmodule(fieldsConstants.createOrderOrderShipModule);
                ordersBIC.setOrdisdigital(Long.parseLong(fieldsConstants.createOrderOrdIsDigital));
                ordersBIC.setOrdtoken(fieldsConstants.createOrderOrdToken);
                ordersBIC.setOrdpaidmoney(orders.getOrdPaidMoney());
                ordersBIC.setOrdtotal(orders.getOrdPaidMoney());
                ordersBIC.setOrddiscountamount(orders.getOrdDiscountAmount());
                ordersBIC.setUserId(Long.parseLong(fieldsConstants.createOrderUserId));
            }


            if (trv != null) {
                trvBIC.setTRVID(Long.parseLong(fieldsConstants.createTrvTrvId));
                trvBIC.setOrderid(Long.parseLong(fieldsConstants.createOrderOrderId));
                trvBIC.setAmountPersons(trv.getAmountPersons());
                trvBIC.setAmountDays(trv.getAmountDays());
                trvBIC.setSI(new BigDecimal(fieldsConstants.createTrvSi));
                trvBIC.setPremium(orders.getOrdPaidMoney());
                trvBIC.setPromotion(getBool(trv.getPromotion()));
                trvBIC.setPromotionAddress(trv.getPromotionAddress());
                trvBIC.setPeriodTime(Long.parseLong(fieldsConstants.createTrvPeriodTime));
                trvBIC.setFromDate(trv.getFromDate());
                trvBIC.setToDate(trv.getToDate());
                trvBIC.setIssueDate(trv.getIssueDate());
                trvBIC.setIncludePayer(getBool(Long.parseLong(fieldsConstants.createTrvIncludePayer)));
                trvBIC.setEndorsement(fieldsConstants.createTrvEndorsement);
                trvBIC.setUserID(Long.parseLong(fieldsConstants.createTrvUserID));
                trvBIC.setUserUpproveID(Long.parseLong(fieldsConstants.createTrvUserUpproveID));
                trvBIC.setDestroy(getBool(Long.parseLong(fieldsConstants.createTrvDestroy)));
                trvBIC.setStatus(getBool(Long.parseLong(fieldsConstants.createTrvStatus)));
                trvBIC.setWriteByHand(getBool(Long.parseLong(fieldsConstants.createTrvWriteByHand)));
                trvBIC.setPrintedPaperNo(fieldsConstants.createTrvPrintedPaperNo);
                trvBIC.setCertificateForm(fieldsConstants.createTrvCertificateForm);
                trvBIC.setModuleid(Long.parseLong(fieldsConstants.createTrvModuleId));
            }


            if (trvDetails != null && trvDetails.size() > 0) {
                for (TRVDetail trvDetail : trvDetails) {
                    trvDetailBIC trvDetailBIC = new trvDetailBIC();
                    trvDetailBIC.setDateofBirth(trvDetail.getDateOfBirth() == null ? "" : trvDetail.getDateOfBirth() );
                    trvDetailBIC.setFullName(trvDetail.getFullName());
                    trvDetailBIC.setGender(trvDetail.getGender());
                    trvDetailBIC.setID(Long.parseLong(fieldsConstants.createTrvDetailId));
                    trvDetailBIC.setTRVID(Long.parseLong(fieldsConstants.createTrvDetailTrvId));
                    trvDetailBIC.setPassportCard(trvDetail.getPassportCard());
                    trvDetailsBICs.add(trvDetailBIC);
                }
            }

            createTravelInsuranceToBIC.setOrders(ordersBIC);
            createTravelInsuranceToBIC.setTRV(trvBIC);
            createTravelInsuranceToBIC.setTRVDetail(trvDetailsBICs);

        } catch (Exception ex) {
            throw new CustomException("An error occurred during data mapping", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createTravelInsuranceToBIC;
    }


    public CreateTravelInsuranceToBIC mapUpdateObjectToBIC(CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest) {
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = new CreateTravelInsuranceToBIC();

        try {
            Orders orders = createTravelInsuranceBICRequest.getOrders();
            TRV trv = createTravelInsuranceBICRequest.getTrv();
            ArrayList<TRVDetail> trvDetails = createTravelInsuranceBICRequest.getTrvDetails();

            OrdersBIC ordersBIC = new OrdersBIC();
            trvBIC trvBIC = new trvBIC();
            ArrayList<trvDetailBIC> trvDetailsBICs = new ArrayList<>();

            if (orders != null) {
                ordersBIC.setOrderReference(orders.getOrderReference());
                ordersBIC.setOrderid(orders.getOrderId());
                ordersBIC.setOrdcustid(Long.parseLong(fieldsConstants.updateOrderOrdCustId));
                ordersBIC.setOrdcustmessage(fieldsConstants.updateOrderOrdCustMessage);
                ordersBIC.setOrdbillfirstname(orders.getOrdBillFirstName());
                ordersBIC.setOrdbillmobile(orders.getOrdBillMobile());
                ordersBIC.setOrdbillstreet1(orders.getOrdBillStreet1());
                ordersBIC.setOrdbillemail(orders.getOrdBillEmail());
                ordersBIC.setOrddate(orders.getOrdDate());
                ordersBIC.setOrdstatus(orders.getOrdStatus());
                ordersBIC.setProductId(fieldsConstants.updateOrderProductId);
                ordersBIC.setOrdtotalqty(orders.getOrdTotalQty());
                ordersBIC.setOrderpaymentmethod(orders.getOrderPaymentMethod());
                ordersBIC.setOrdershipmodule(fieldsConstants.updateOrderOrderShipModule);
                ordersBIC.setOrdisdigital(Long.parseLong(fieldsConstants.updateOrderOrdIsDigital));
                ordersBIC.setOrdtoken(fieldsConstants.updateOrderOrdToken);
                ordersBIC.setOrdpaidmoney(orders.getOrdPaidMoney());
                ordersBIC.setOrdtotal(orders.getOrdPaidMoney());
                ordersBIC.setOrddiscountamount(orders.getOrdDiscountAmount());
                ordersBIC.setUserId(Long.parseLong(fieldsConstants.updateOrderUserId));
            }


            if (trv != null) {
                trvBIC.setTRVID(trv.getTrvId());
                trvBIC.setOrderid(Long.parseLong(trv.getOrderId()));
                trvBIC.setAmountPersons(trv.getAmountPersons());
                trvBIC.setAmountDays(trv.getAmountDays());
                trvBIC.setSI(new BigDecimal(fieldsConstants.updateTrvSi));
                trvBIC.setPremium(orders.getOrdPaidMoney());
                trvBIC.setPromotion(getBool(trv.getPromotion()));
                trvBIC.setPromotionAddress(trv.getPromotionAddress());
                trvBIC.setPeriodTime(Long.parseLong(fieldsConstants.updateTrvPeriodTime));
                trvBIC.setFromDate(trv.getFromDate());
                trvBIC.setToDate(trv.getToDate());
                trvBIC.setIssueDate(trv.getIssueDate());
                trvBIC.setIncludePayer(getBool(Long.parseLong(fieldsConstants.updateTrvIncludePayer)));
                trvBIC.setEndorsement(fieldsConstants.updateTrvEndorsement);
                trvBIC.setUserID(Long.parseLong(fieldsConstants.updateTrvUserID));
                trvBIC.setUserUpproveID(Long.parseLong(fieldsConstants.updateTrvUserUpproveID));
                trvBIC.setDestroy(getBool(trv.getDestroy()));
                trvBIC.setStatus(getBool(trv.getStatus()));
                trvBIC.setWriteByHand(getBool(Long.parseLong(fieldsConstants.updateTrvWriteByHand)));
                trvBIC.setPrintedPaperNo(fieldsConstants.updateTrvPrintedPaperNo);
                trvBIC.setCertificateForm(fieldsConstants.updateTrvCertificateForm);
                trvBIC.setModuleid(Long.parseLong(fieldsConstants.updateTrvModuleId));

            }

            if (trvDetails != null && trvDetails.size() > 0) {
                for (TRVDetail trvDetail : trvDetails) {
                    trvDetailBIC trvDetailBIC = new trvDetailBIC();
                    trvDetailBIC.setDateofBirth(trvDetail.getDateOfBirth() == null ? "" : trvDetail.getDateOfBirth());
                    trvDetailBIC.setFullName(trvDetail.getFullName());
                    trvDetailBIC.setGender(trvDetail.getGender());
                    trvDetailBIC.setID(trvDetail.getId());
                    trvDetailBIC.setTRVID(trvDetail.getTrvId());
                    trvDetailBIC.setPassportCard(trvDetail.getPassportCard());
                    trvDetailsBICs.add(trvDetailBIC);
                }
            }

            createTravelInsuranceToBIC.setOrders(ordersBIC);
            createTravelInsuranceToBIC.setTRV(trvBIC);
            createTravelInsuranceToBIC.setTRVDetail(trvDetailsBICs);

        } catch (Exception ex) {
            throw new CustomException("An error occurred during data mapping", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return createTravelInsuranceToBIC;
    }


    public CreateTravelInsuranceBICRequest queryCreateObjectToBIC(BaseDetail<QueryTravelInsuranceBICRequest> queryTravelInsuranceBICRequest, String token, String requestId) throws APIAccessException {

        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = new CreateTravelInsuranceBICRequest();

        APIUtils apiUtils = new APIUtils();
        Map<String, Object> map = new HashMap<>();

        ResponseEntity<String> resultBIC = null;
        if (queryTravelInsuranceBICRequest.getDetail() != null) {
            if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(1L)) {
                String orderId = queryTravelInsuranceBICRequest.getDetail().getOrderId();
                if (orderId != null) {
                    map.put("id", orderId);
                    resultBIC = apiUtils.getApiWithParam(hostConstants.BIC_HOST_GET_BY_ORDER_ID, null, map, token, requestId);
                }
            }
            if (queryTravelInsuranceBICRequest.getDetail().getInquiryType().equals(2L)) {
                String orderReference = queryTravelInsuranceBICRequest.getDetail().getOrderReference();
                if (orderReference != null) {
                    map.put("id", orderReference);
                    resultBIC = apiUtils.getApiWithParam(hostConstants.BIC_HOST_GET_BY_ORDER_REFERANCE, null, map, token, requestId);
                }

            }
        }

//        try {

        if (resultBIC != null && resultBIC.getStatusCode() == HttpStatus.OK && resultBIC.getBody() != null) {
            try {
                EJson jsonObjectResultBIC = new EJson(resultBIC.getBody());
                EJson ordersBIC = jsonObjectResultBIC.getJSONObject("Orders");
                EJson trvBIC = jsonObjectResultBIC.getJSONObject("TRV");
                List<EJson> trvDetailsBIC = jsonObjectResultBIC.getJSONArray("TRVDetail");

                Orders orders = new Orders();
                TRV trv = new TRV();
                ArrayList<TRVDetail> trvDetails = new ArrayList<>();

                orders.setOrderId(ordersBIC.getLong("Orderid"));
                orders.setOrderReference(ordersBIC.getString("OrderReference") == null ? null : ordersBIC.getString("OrderReference").toString());
//                orders.setOrdCustId(ordersBIC.getLong("Ordcustid"));
//                orders.setOrdCustMessage(ordersBIC.getString("Ordcustmessage") == null ? null : ordersBIC.getString("Ordcustmessage").toString());
                orders.setOrdBillFirstName(ordersBIC.getString("Ordbillfirstname") == null ? null : ordersBIC.getString("Ordbillfirstname").toString());
                orders.setOrdBillMobile(ordersBIC.getString("Ordbillmobile") == null ? null : ordersBIC.getString("Ordbillmobile").toString());
                orders.setOrdBillStreet1(ordersBIC.getString("Ordbillstreet1") == null ? null : ordersBIC.getString("Ordbillstreet1").toString());
                orders.setOrdBillEmail(ordersBIC.getString("Ordbillemail") == null ? null : ordersBIC.getString("Ordbillemail").toString());
                orders.setOrdDate(ordersBIC.getString("Orddate") == null ? null : ordersBIC.getString("Orddate").toString());
                orders.setOrdStatus(ordersBIC.getLong("Ordstatus"));
//                orders.setProductId(ordersBIC.getString("ProductId") == null ? null : ordersBIC.getString("ProductId").toString());
                orders.setOrdTotalQty(ordersBIC.getLong("Ordtotalqty"));
                orders.setOrderPaymentMethod(ordersBIC.getLong("Orderpaymentmethod"));
//                orders.setOrderShipModule(ordersBIC.getString("Ordershipmodule") == null ? null : ordersBIC.getString("Ordershipmodule").toString());
//                orders.setOrdIsDigital(ordersBIC.getLong("Ordisdigital"));
//                orders.setOrdToken(ordersBIC.getString("Ordtoken") == null ? null : ordersBIC.getString("Ordtoken").toString());
                orders.setOrdPaidMoney(ordersBIC.getBigDecimal("Ordpaidmoney"));
//                orders.setOrdTotal(ordersBIC.getBigDecimal("Ordtotal"));
                orders.setOrdDiscountAmount(ordersBIC.getBigDecimal("Orddiscountamount"));
//                orders.setUserId(ordersBIC.getLong("UserID"));

                trv.setTrvId(trvBIC.getLong("TRVID"));
                trv.setOrderId(String.valueOf(trvBIC.getLong("Orderid")));
                trv.setAmountPersons(trvBIC.getLong("AmountPersons"));
                trv.setAmountDays(trvBIC.getLong("AmountDays"));
//                trv.setSi(trvBIC.getBigDecimal("SI"));
//                trv.setPremium(trvBIC.getBigDecimal("Premium"));
                trv.setPromotion(getLongFromBool(trvBIC.getBoolean("Promotion")));
                trv.setPromotionAddress(trvBIC.getString("PromotionAddress"));
//                trv.setPeriodTime(String.valueOf(trvBIC.getLong("PeriodTime")));
                trv.setFromDate(trvBIC.getString("FromDate"));
                trv.setToDate(trvBIC.getString("ToDate"));
                trv.setIssueDate(trvBIC.getString("IssueDate"));
//                trv.setIncludePayer(getLongFromBool(trvBIC.getBoolean("IncludePayer")));
//                trv.setEndorsement(trvBIC.getString("Endorsement"));
//                trv.setUserID(trvBIC.getLong("UserID"));
//                trv.setUserUpproveID(trvBIC.getLong("UserUpproveID"));
                trv.setDestroy(getLongFromBool(trvBIC.getBoolean("Destroy")));
                trv.setStatus(getLongFromBool(trvBIC.getBoolean("Status")));
//                trv.setWriteByHand(getLongFromBool(trvBIC.getBoolean("WriteByHand")));
//                trv.setPrintedPaperNo(trvBIC.getString("PrintedPaperNo"));
//                trv.setCertificateForm(trvBIC.getString("PrintedPaperNo"));
//                trv.setModuleId(trvBIC.getLong("Moduleid"));

                for (int i = 0; i < trvDetailsBIC.size(); i++) {
                    EJson trvDetailBIC = trvDetailsBIC.get(i);
                    TRVDetail trvDetail = new TRVDetail();
                    trvDetail.setDateOfBirth(trvDetailBIC.getString("DateofBirth"));
                    trvDetail.setFullName(trvDetailBIC.getString("FullName"));
                    trvDetail.setGender(trvDetailBIC.getLong("Gender"));
                    trvDetail.setId(trvDetailBIC.getLong("ID"));
                    trvDetail.setTrvId(trvDetailBIC.getLong("TRVID"));
                    trvDetail.setPassportCard(trvDetailBIC.getString("PassportCard"));
                    trvDetails.add(trvDetail);
                }

                createTravelInsuranceBICRequest.setOrders(orders);
                createTravelInsuranceBICRequest.setTrv(trv);
                createTravelInsuranceBICRequest.setTrvDetails(trvDetails);
            } catch (Exception ex) {
                throw new CustomException("An error occurred during data mapping ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new CustomException("Can not find the insurance order", HttpStatus.BAD_REQUEST);
        }


        return createTravelInsuranceBICRequest;
    }

    public static boolean getBool(Long value) {
        if (value.equals(1L)) {
            return true;
        }
        return false;
    }

    public static Long getLongFromBool(Boolean value) {
        if (value != null ){
            if (value == true) {
                return 1L;
            }
            return 0L;
        }
        return null ;
    }

}
