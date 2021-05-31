package com.smartmarket.code.util;

import org.json.JSONArray;
import org.json.JSONObject;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.entity.Orders;
import com.smartmarket.code.request.entity.TRV;
import com.smartmarket.code.request.entity.TRVDetail;
import com.smartmarket.code.request.entityBIC.CreateTravelInsuranceToBIC;
import com.smartmarket.code.request.entityBIC.OrdersBIC;
import com.smartmarket.code.request.entityBIC.trvBIC;
import com.smartmarket.code.request.entityBIC.trvDetailBIC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MapperUtils {

    public static CreateTravelInsuranceToBIC mapCreateObjectToBIC(CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest) {
        CreateTravelInsuranceToBIC createTravelInsuranceToBIC = new CreateTravelInsuranceToBIC();
        Orders orders = createTravelInsuranceBICRequest.getOrders();
        TRV trv = createTravelInsuranceBICRequest.getTrv();
        ArrayList<TRVDetail> trvDetails = createTravelInsuranceBICRequest.getTrvDetails();

        OrdersBIC ordersBIC = new OrdersBIC();
        trvBIC trvBIC = new trvBIC();
        ArrayList<trvDetailBIC> trvDetailsBICs = new ArrayList<>();

        ordersBIC.setOrderid(Long.parseLong(trv.getOrderId()));
        ordersBIC.setOrdcustid(orders.getOrdCustId());
        ordersBIC.setOrdcustmessage(orders.getOrdCustMessage());
        ordersBIC.setOrdbillfirstname(orders.getOrdBillFirstName());
        ordersBIC.setOrdbillmobile(orders.getOrdBillMobile());
        ordersBIC.setOrdbillstreet1(orders.getOrdBillStreet1());
        ordersBIC.setOrdbillemail(orders.getOrdBillEmail());
        ordersBIC.setOrddate(orders.getOrdDate());
        ordersBIC.setOrdstatus(orders.getOrdStatus());
        ordersBIC.setProductId(orders.getProductId());
        ordersBIC.setOrdtotalqty(orders.getOrdTotalQty());
        ordersBIC.setOrderpaymentmethod(orders.getOrderPaymentMethod());
        ordersBIC.setOrdershipmodule(orders.getOrderShipModule());
        ordersBIC.setOrdisdigital(orders.getOrdIsDigital());
        ordersBIC.setOrdtoken(orders.getOrdToken());
        ordersBIC.setOrdpaidmoney(orders.getOrdPaidMoney());
        ordersBIC.setOrdtotal(orders.getOrdTotal());
        ordersBIC.setOrddiscountamount(orders.getOrdDiscountAmount());
        ordersBIC.setUserId(orders.getUserId());


        trvBIC.setTRVID(trv.getTrvId());
        trvBIC.setOrderid(Long.parseLong(trv.getOrderId()));
        trvBIC.setAmountPersons(trv.getAmountPersons());
        trvBIC.setAmountDays(trv.getAmountDays());
        trvBIC.setSI(trv.getSi());
        trvBIC.setPremium(trv.getPremium());
        trvBIC.setPromotion(getBool(trv.getPromotion()));
        trvBIC.setPromotionAddress(trv.getPromotionAddress());
        trvBIC.setPeriodTime(Long.parseLong(trv.getPeriodTime()));
        trvBIC.setFromDate(trv.getFromDate());
        trvBIC.setToDate(trv.getToDate());
        trvBIC.setIssueDate(trv.getIssueDate());
        trvBIC.setIncludePayer(getBool(trv.getIncludePayer()));
        trvBIC.setEndorsement(trv.getEndorsement());
        trvBIC.setUserID(trv.getUserID());
        trvBIC.setUserUpproveID(trv.getUserUpproveID());
        trvBIC.setDestroy(getBool(trv.getDestroy()));
        trvBIC.setStatus(getBool(trv.getStatus()));
        trvBIC.setWriteByHand(getBool(trv.getWriteByHand()));
        trvBIC.setPrintedPaperNo(trv.getPrintedPaperNo());
        trvBIC.setCertificateForm(trv.getCertificateForm());
        trvBIC.setModuleid(trv.getModuleId());

        if (trvDetails != null && trvDetails.size() > 0) {
            for (TRVDetail trvDetail : trvDetails) {
                trvDetailBIC trvDetailBIC = new trvDetailBIC();
                trvDetailBIC.setDateofBirth(trvDetail.getDateofBirth());
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

        return createTravelInsuranceToBIC;
    }


    public static CreateTravelInsuranceBICRequest queryCreateObjectToBIC(Long orderId, String token) {

        APIUtils apiUtils = new APIUtils();
        Map<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        ResponseEntity<String> resultBIC = apiUtils.getApiWithParam("https://app.bic.vn/EbizApiTest/api/v1/TRV/Get/{id}", null, map, token);
        CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = new CreateTravelInsuranceBICRequest();

        if (resultBIC.getStatusCode() == HttpStatus.OK && resultBIC.getBody() != null) {
            JSONObject jsonObjectResultBIC = new JSONObject(resultBIC.getBody());
            JSONObject ordersBIC = jsonObjectResultBIC.getJSONObject("Orders");
            JSONObject trvBIC = jsonObjectResultBIC.getJSONObject("TRV");
            JSONArray trvDetailsBIC = jsonObjectResultBIC.getJSONArray("TRVDetail");


            Orders orders = new Orders();
            TRV trv = new TRV();
            ArrayList<TRVDetail> trvDetails = new ArrayList<>();

            orders.setOrderId(ordersBIC.getLong("Orderid"));
            orders.setOrdCustId(ordersBIC.getLong("Ordcustid"));
            orders.setOrdCustMessage(ordersBIC.getString("Ordcustmessage") == null ? null : ordersBIC.getString("Ordcustmessage").toString());
            orders.setOrdBillFirstName(ordersBIC.getString("Ordcustmessage") == null ? null : ordersBIC.getString("Ordcustmessage").toString());
            orders.setOrdBillMobile(ordersBIC.getString("Ordbillmobile") == null ? null : ordersBIC.getString("Ordbillmobile").toString());
            orders.setOrdBillStreet1(ordersBIC.getString("Ordshipstreet1") == null ? null : ordersBIC.getString("Ordshipstreet1").toString());
            orders.setOrdBillEmail(ordersBIC.getString("Ordbillemail") == null ? null : ordersBIC.getString("Ordbillemail").toString());
            orders.setOrdDate(ordersBIC.getString("Orddate") == null ? null : ordersBIC.getString("Orddate").toString());
            orders.setOrdStatus(ordersBIC.getLong("Ordstatus"));
            orders.setProductId(ordersBIC.getString("ProductId") == null ? null : ordersBIC.getString("ProductId").toString());
            orders.setOrdTotalQty(ordersBIC.getLong("Ordtotalqty"));
            orders.setOrderPaymentMethod(ordersBIC.getLong("Orderpaymentmethod"));
            orders.setOrderShipModule(ordersBIC.getString("Ordershipmodule") == null ? null : ordersBIC.getString("Ordershipmodule").toString());
            orders.setOrdIsDigital(ordersBIC.getLong("Ordisdigital"));
            orders.setOrdToken(ordersBIC.getString("Ordtoken") == null ? null : ordersBIC.getString("Ordtoken").toString());
            orders.setOrdPaidMoney(ordersBIC.getBigDecimal("Ordpaidmoney"));
            orders.setOrdTotal(ordersBIC.getBigDecimal("Ordtotal"));
            orders.setOrdDiscountAmount(ordersBIC.getBigDecimal("Orddiscountamount"));
            orders.setUserId(ordersBIC.getLong("UserID"));

            trv.setTrvId(trvBIC.getLong("TRVID"));
            trv.setOrderId(String.valueOf(trvBIC.getLong("Orderid")));
            trv.setAmountPersons(trvBIC.getLong("AmountPersons"));
            trv.setAmountDays(trvBIC.getLong("AmountDays"));
            trv.setSi(trvBIC.getBigDecimal("SI"));
            trv.setPremium(trvBIC.getBigDecimal("Premium"));
            trv.setPromotion(getLongFromBool(trvBIC.getBoolean("Promotion")));
            trv.setPromotionAddress(trvBIC.getString("PromotionAddress"));
            trv.setPeriodTime(String.valueOf(trvBIC.getLong("PeriodTime")));
            trv.setFromDate(trvBIC.getString("FromDate"));
            trv.setToDate(trvBIC.getString("ToDate"));
            trv.setIssueDate(trvBIC.getString("IssueDate"));
            trv.setIncludePayer(getLongFromBool(trvBIC.getBoolean("IncludePayer")));
            trv.setEndorsement(trvBIC.getString("Endorsement"));
            trv.setUserID(trvBIC.getLong("UserID"));
            trv.setUserUpproveID(trvBIC.getLong("UserUpproveID"));
            trv.setDestroy(getLongFromBool(trvBIC.getBoolean("Destroy")));
            trv.setStatus(getLongFromBool(trvBIC.getBoolean("Status")));
            trv.setWriteByHand(getLongFromBool(trvBIC.getBoolean("WriteByHand")));
            trv.setPrintedPaperNo(trvBIC.getString("PrintedPaperNo"));
            trv.setCertificateForm(trvBIC.getString("PrintedPaperNo"));
            trv.setModuleId(trvBIC.getLong("Moduleid"));

            for (int i = 0; i < trvDetailsBIC.length(); i++) {
                JSONObject trvDetailBIC = trvDetailsBIC.getJSONObject(i);
                TRVDetail trvDetail = new TRVDetail();
                trvDetail.setDateofBirth(trvDetailBIC.getString("DateofBirth"));
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
        }



        return createTravelInsuranceBICRequest;
    }

    public static boolean getBool(Long value) {
        if (value.equals(1L)) {
            return true;
        }
        return false;
    }

    public static Long getLongFromBool(boolean value) {
        if (value == true) {
            return 1L;
        }
        return 0L;
    }

}
