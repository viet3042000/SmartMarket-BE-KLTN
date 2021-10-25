package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smartmarket.code.constants.AggregateType;
import com.smartmarket.code.constants.TravelInsuranceState;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.TravelInsuranceRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.TravelInsurance;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateTravelInsuranceBICRequest;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.request.UpdateTravelInsuranceBICRequest;
import com.smartmarket.code.service.OrderOutboxService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderOutboxServiceImp implements OrderOutboxService {

    @Autowired
    OutboxRepository outboxRepository;

    @Autowired
    TravelInsuranceRepository travelInsuranceRepository;

    @Autowired
    BICTransactionRepository bicTransactionRepository;

    @Autowired
    TravelInsuranceServiceImpl travelInsuranceService;


    public void processMessageFromOrderOutbox(String op,String aggregateId, String type, String orderReference,
                                              String requestId, JSONObject jsonPayload) throws Exception {

        Outbox outBox = new Outbox();
        Gson g = new Gson();

        String clientIp = jsonPayload.getString("clientIp");
        String clientId = jsonPayload.getString("clientId");
        String hostName= jsonPayload.getString("hostName");
        Long startTime = jsonPayload.getLong("startTime");
        if (op.equals("c")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);

            JSONObject detail = new JSONObject();
            detail = jsonPayload.getJSONObject("detail");
            String d = detail.toString();

            if (type.equals("createTravelInsuranceBIC")) {
                orderReference = UUID.randomUUID().toString();
                detail.getJSONObject("orders").put("orderReference", orderReference);

                TravelInsurance travelInsurance = new TravelInsurance();
                travelInsurance.setId(orderReference);
                travelInsurance.setState(TravelInsuranceState.CREATING);
                travelInsurance.setProductName("TravelInsuranceBIC");
                travelInsurance.setCreatedLogtimestamp(createAt);
                travelInsuranceRepository.save(travelInsurance);

                CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = g.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);
                BaseDetail baseDetail = new BaseDetail();
                baseDetail.setDetail(createTravelInsuranceBICRequest);
                baseDetail.setRequestId(jsonPayload.getString("requestId"));
                baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
                baseDetail.setTargetId(jsonPayload.getString("targetId"));

                try {
                    // get result from API create.
                    ResponseEntity<String> responseEntity = travelInsuranceService.createOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
                    ObjectMapper mapper = new ObjectMapper();
                    String responseBody = mapper.writeValueAsString(responseEntity);
                    JSONObject jsonBody = new JSONObject(responseBody);
                    jsonBody.put("requestId", requestId);
                    jsonBody.put("OrderReference", orderReference);
                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                    Date dateFinished = new Date();
                    String stringFinishedAt = formatter.format(dateFinished);
                    Date finishedAt = formatter.parse(stringFinishedAt);
                    travelInsurance.setFinishedLogtimestamp(finishedAt);
                    //insert to outbox
                    if (statusCodeValue == 200) {
                        travelInsurance.setState(TravelInsuranceState.SUCCEEDED);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebody + status+requestId + orderRef
                        jsonBody.put("status", "success");
                        outBox.setPayload(jsonBody.toString());
                    } else {
                        travelInsurance.setState(TravelInsuranceState.CREATE_FAILURE);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebpdy + status+requestId+ orderRef
                        jsonBody.put("status", "failure");
                        outBox.setPayload(jsonBody.toString());
                    }
                    travelInsuranceRepository.save(travelInsurance);
                    outboxRepository.save(outBox);
                } catch (Exception ex) {
                    travelInsurance.setState(TravelInsuranceState.CREATE_FAILURE);

                    Date dateFinished = new Date();
                    String stringFinishedAt = formatter.format(dateFinished);
                    Date finishedAt = formatter.parse(stringFinishedAt);
                    travelInsurance.setFinishedLogtimestamp(finishedAt);
                    travelInsuranceRepository.save(travelInsurance);

                    throw ex;
                }
            }

            if (type.equals("updateTravelInsuranceBIC")) {
                String orderId = "";

                Optional<BICTransaction> bicTransaction = bicTransactionRepository.findBICTransactionSuccessByOrderRef(detail.getJSONObject("orders").getString("orderReference"));
                if (bicTransaction.isPresent()) {
                    orderId = bicTransaction.get().getOrderId();
                    detail.getJSONObject("orders").put("orderId", orderId);
                }

                Optional<TravelInsurance> travelInsurance = travelInsuranceRepository.findById(detail.getJSONObject("orders").getString("orderReference"));
                if (travelInsurance.isPresent()) {
                    TravelInsurance t = travelInsurance.get();
                    t.setState(TravelInsuranceState.UPDATING);
                    travelInsuranceRepository.save(t);

                    UpdateTravelInsuranceBICRequest updateTravelInsuranceBICRequest = g.fromJson(detail.toString(), UpdateTravelInsuranceBICRequest.class);
                    BaseDetail baseDetail = new BaseDetail();
                    baseDetail.setDetail(updateTravelInsuranceBICRequest);
                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
                    baseDetail.setTargetId(jsonPayload.getString("targetId"));

                    try {
                        // get result from API create.
                        ResponseEntity<String> responseEntity = travelInsuranceService.updateOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
                        ObjectMapper mapper = new ObjectMapper();
                        String responseBody = mapper.writeValueAsString(responseEntity);
                        JSONObject jsonBody = new JSONObject(responseBody);
                        jsonBody.put("requestId", requestId);
                        int statusCodeValue = jsonBody.getInt("statusCodeValue");

                        //insert to outbox
                        if (statusCodeValue == 200) {
                            t.setState(TravelInsuranceState.SUCCEEDED);

                            outBox.setCreatedLogtimestamp(createAt);
                            outBox.setAggregateId(aggregateId);
                            outBox.setAggregateType(AggregateType.Order);
                            outBox.setType(type);

                            //payload = responsebody + status+requestId
                            jsonBody.put("status", "success");
                            outBox.setPayload(jsonBody.toString());
                        } else {
                            t.setState(TravelInsuranceState.UPDATE_FAILURE);

                            outBox.setCreatedLogtimestamp(createAt);
                            outBox.setAggregateId(aggregateId);
                            outBox.setAggregateType(AggregateType.Order);
                            outBox.setType(type);

                            //payload = responsebody + status+requestId
                            jsonBody.put("status", "failure");
                            outBox.setPayload(jsonBody.toString());
                        }
                        travelInsuranceRepository.save(t);
                        outboxRepository.save(outBox);
                    } catch (Exception ex) {
                        t.setState(TravelInsuranceState.UPDATE_FAILURE);
                        travelInsuranceRepository.save(t);
                        throw ex;
                    }
                }
            }

//            if (type.equals("getTravelInsuranceBIC")) {
//                QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
//                BaseDetail baseDetail = new BaseDetail();
//                baseDetail.setDetail(queryTravelInsuranceBICRequest);
//                baseDetail.setRequestId(jsonPayload.getString("requestId"));
//                baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
//                baseDetail.setTargetId(jsonPayload.getString("targetId"));
//
//                // get result from API create.
//                ResponseEntity<String> responseEntity = travelInsuranceService.getOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
//                ObjectMapper mapper = new ObjectMapper();
//                String responseBody = mapper.writeValueAsString(responseEntity);
//                JSONObject jsonBody = new JSONObject(responseBody);
//                jsonBody.put("requestId", requestId);
//                int statusCodeValue = jsonBody.getInt("statusCodeValue");
//
//                outBox.setCreatedLogtimestamp(createAt);
//                outBox.setAggregateId(aggregateId);
//                outBox.setAggregateType(AggregateType.Order);
//                outBox.setType(type);
//                //insert to outbox
//                if (statusCodeValue == 200) {
//                    //payload = responsebody+ status+requestId
//                    jsonBody.put("status", "success");
//                    outBox.setPayload(jsonBody.toString());
//                } else {
//                    //payload = responsebody+ status+requestId
//                    jsonBody.put("status", "failure");
//                    outBox.setPayload(jsonBody.toString());
//                }
//                outboxRepository.save(outBox);
//            }
        }
    }
}
