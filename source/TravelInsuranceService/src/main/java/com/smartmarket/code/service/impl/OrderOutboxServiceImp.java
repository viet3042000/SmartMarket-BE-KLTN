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
import com.smartmarket.code.request.*;
import com.smartmarket.code.request.entity.*;
import com.smartmarket.code.service.OrderOutboxService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

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


    public void processMessageFromOrderOutbox(String op,String aggregateId,String type,
                                              JSONObject jsonPayload) throws Exception {

        Outbox outBox = new Outbox();
        Gson g = new Gson();

        if (op.equals("c")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String stringCreateAt = formatter.format(date);
            Date createAt = formatter.parse(stringCreateAt);

            JSONObject detail = jsonPayload.getJSONObject("detail");
            JSONArray orderItems = detail.getJSONArray("orderItems");
            String requestId = jsonPayload.getString("requestId");
            String requestTime = jsonPayload.getString("requestTime");
            String orderReference ="";

            if (type.equals("createOrder")) {
                ObjectMapper mapper = new ObjectMapper();
                List<ItemDetailCreateRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCreateRequest[].class));
                int createItemIndex = jsonPayload.getInt("createItemIndex");

                ItemDetailCreateRequest itemDetailCreateRequest = items.get(createItemIndex);
                ProductDetailCreateRequest productDetailCreateRequest = itemDetailCreateRequest.getProductDetailCreateRequest();
                JSONObject productDetail =new JSONObject(productDetailCreateRequest);
                String productName = itemDetailCreateRequest.getProductName();
                String productProvider = itemDetailCreateRequest.getProductProvider();

                String clientIp = jsonPayload.getString("clientIp");
                String clientId = jsonPayload.getString("clientId");
                String hostName = jsonPayload.getString("hostName");
                Long startTime = jsonPayload.getLong("startTime");

                orderReference = UUID.randomUUID().toString();
                productDetail.getJSONObject("orders").put("orderReference",orderReference);

                TravelInsurance travelInsurance = new TravelInsurance();
                travelInsurance.setId(orderReference);
                travelInsurance.setState(TravelInsuranceState.CREATING);
                travelInsurance.setProductName(productName);
                travelInsurance.setCreatedLogtimestamp(createAt);
//                travelInsurance.setProductDetail(productDetail.toString());
                travelInsuranceRepository.save(travelInsurance);

                CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = g.fromJson(productDetail.toString(), CreateTravelInsuranceBICRequest.class);
                BaseDetail baseDetail = new BaseDetail();
                baseDetail.setDetail(createTravelInsuranceBICRequest);
                baseDetail.setRequestId(requestId);
                baseDetail.setRequestTime(requestTime);
                baseDetail.setTargetId(productProvider);

                try {
                    // get result from API create.
                    ResponseEntity<String> responseEntity = travelInsuranceService.createOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
                    String responseBody = mapper.writeValueAsString(responseEntity);
                    JSONObject jsonBody = new JSONObject(responseBody);
                    jsonBody.put("OrderReference", orderReference);
                    jsonBody.put("requestPayload", jsonPayload.toString());//có index của sp vừa tạo
                    jsonBody.put("productName", productName);
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

                        //payload = responsebody + status+requestId+ orderRef
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

                    outBox.setCreatedLogtimestamp(createAt);
                    outBox.setAggregateId(aggregateId);
                    outBox.setAggregateType(AggregateType.Order);
                    outBox.setType(type);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("requestPayload",jsonPayload.toString());
                    jsonBody.put("OrderReference", orderReference);
                    jsonBody.put("productName", productName);
                    jsonBody.put("status", "failure");
                    outBox.setPayload(jsonBody.toString());
                    outboxRepository.save(outBox);

                    throw ex;
                }
            }

            if (type.equals("cancelOrder")) {
                ObjectMapper mapper = new ObjectMapper();
                List<ItemDetailCancelRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCancelRequest[].class));
                int cancelItemIndex = jsonPayload.getInt("cancelItemIndex");

                ItemDetailCancelRequest itemDetailCancelRequest = items.get(cancelItemIndex);

                String productName = itemDetailCancelRequest.getProductName();
                String productProvider = itemDetailCancelRequest.getProductProvider();
                orderReference = itemDetailCancelRequest.getOrderReference();

                String clientIp = jsonPayload.getString("clientIp");
                String clientId = jsonPayload.getString("clientId");
                String hostName = jsonPayload.getString("hostName");
                Long startTime = jsonPayload.getLong("startTime");

                TravelInsurance travelInsurance = travelInsuranceRepository.findById(orderReference).orElse(null);
                travelInsurance.setState(TravelInsuranceState.CANCELING);
                travelInsuranceRepository.save(travelInsurance);

                //call api query
                QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = new QueryTravelInsuranceBICRequest();
                queryTravelInsuranceBICRequest.setInquiryType(2L);
                queryTravelInsuranceBICRequest.setOrderReference(orderReference);
                BaseDetail baseDetailQuery = new BaseDetail();
                baseDetailQuery.setDetail(queryTravelInsuranceBICRequest);
                baseDetailQuery.setRequestId(requestId);
                baseDetailQuery.setRequestTime(requestTime);
                baseDetailQuery.setTargetId(productProvider);

//                  // get result from API create.
                ResponseEntity<String> responseEntityQuery = travelInsuranceService.getOrderOutbox(baseDetailQuery, clientIp, clientId, startTime, hostName);
                String responseBodyQuery = mapper.writeValueAsString(responseEntityQuery);
                JSONObject jsonResponseBody = new JSONObject(responseBodyQuery);
                JSONObject trvQuery = jsonResponseBody.getJSONObject("body").getJSONObject("detail").getJSONObject("trv");
                JSONArray trvDetailsQuery = jsonResponseBody.getJSONObject("body").getJSONObject("detail").getJSONArray("trvDetails");
                List<TRVDetail> trvDetailsList = Arrays.asList(mapper.readValue(trvDetailsQuery.toString(), TRVDetail[].class));

                OrderUpdate orders = new OrderUpdate();
                BICTransaction bicTransaction = bicTransactionRepository.findBICTransactionSuccessByOrderRef(orderReference).orElse(null);
                orders.setOrderId(bicTransaction.getOrderId());
                orders.setOrderReference(orderReference);
                orders.setOrdBillEmail(bicTransaction.getEmail());
                orders.setOrdBillStreet1(bicTransaction.getCustomerAddress());
                orders.setOrdBillFirstName(bicTransaction.getCustomerName());
                orders.setOrdBillMobile(bicTransaction.getPhoneNumber());
                orders.setOrdStatus(1L);//chưa thanh toán

                TRVUpdate trv = new TRVUpdate();
                trv.setDestroy(1L);//hủy
                trv.setTrvId(trvQuery.getLong("trvId"));
                trv.setOrderId(bicTransaction.getOrderId());

                TRVDetail trvDetails = trvDetailsList.get(0);
                TRVDetailUpdate trvDetailUpdate = new TRVDetailUpdate();
                trvDetailUpdate.setTrvId(trvDetails.getTrvId());
                trvDetailUpdate.setFullName(trvDetails.getFullName());
                trvDetailUpdate.setGender(trvDetails.getGender());
                trvDetailUpdate.setDateOfBirth(trvDetails.getDateOfBirth());
                trvDetailUpdate.setPassportCard(trvDetails.getPassportCard());

                ArrayList<TRVDetailUpdate> trvDetailUpdates = new ArrayList<>();
                trvDetailUpdates.add(trvDetailUpdate);

                UpdateTravelInsuranceBICRequest updateTravelInsuranceBICRequest = new UpdateTravelInsuranceBICRequest();
                updateTravelInsuranceBICRequest.setOrders(orders);
                updateTravelInsuranceBICRequest.setTrv(trv);
                updateTravelInsuranceBICRequest.setTrvDetails(trvDetailUpdates);

                BaseDetail baseDetail = new BaseDetail();
                baseDetail.setDetail(updateTravelInsuranceBICRequest);
                baseDetail.setRequestId(requestId);
                baseDetail.setRequestTime(requestTime);
                baseDetail.setTargetId(productProvider);

                try {
                    // get result from API create.
                    ResponseEntity<String> responseEntity = travelInsuranceService.updateOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
                    String responseBody = mapper.writeValueAsString(responseEntity);
                    JSONObject jsonBody = new JSONObject(responseBody);
                    jsonBody.put("productName", productName);
                    jsonBody.put("requestPayload", jsonPayload.toString());
                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                    //insert to outbox
                    if (statusCodeValue == 200) {
                        travelInsurance.setState(TravelInsuranceState.CANCELED);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebody + status+requestId
                        jsonBody.put("status", "success");
                        outBox.setPayload(jsonBody.toString());
                    } else {
                        travelInsurance.setState(TravelInsuranceState.CANCEL_FAILURE);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebody + status+requestId
                        jsonBody.put("status", "failure");
                        outBox.setPayload(jsonBody.toString());
                    }
                    travelInsuranceRepository.save(travelInsurance);
                    outboxRepository.save(outBox);
                } catch (Exception ex) {
                    travelInsurance.setState(TravelInsuranceState.CANCEL_FAILURE);
                    travelInsuranceRepository.save(travelInsurance);

                    outBox.setCreatedLogtimestamp(createAt);
                    outBox.setAggregateId(aggregateId);
                    outBox.setAggregateType(AggregateType.Order);
                    outBox.setType(type);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("requestPayload",jsonPayload.toString());
                    jsonBody.put("productName", productName);
                    jsonBody.put("status", "failure");
                    outBox.setPayload(jsonBody.toString());
                    outboxRepository.save(outBox);

                    throw ex;
                }
            }

            if (type.equals("abortOrder")) {//after create failure
                ObjectMapper mapper = new ObjectMapper();
                List<ItemDetailCreateRequest> items = Arrays.asList(mapper.readValue(orderItems.toString(), ItemDetailCreateRequest[].class));
                int abortItemIndex = jsonPayload.getInt("abortItemIndex");

                ItemDetailCreateRequest itemDetailCreateRequest = items.get(abortItemIndex);
                String productName = itemDetailCreateRequest.getProductName();
                String productProvider = itemDetailCreateRequest.getProductProvider();

                orderReference= itemDetailCreateRequest.getProductDetailCreateRequest().getOrders().getOrderReference();
                String clientIp = jsonPayload.getString("clientIp");
                String clientId = jsonPayload.getString("clientId");
                String hostName = jsonPayload.getString("hostName");
                Long startTime = jsonPayload.getLong("startTime");

                TravelInsurance travelInsurance = travelInsuranceRepository.findById(orderReference).orElse(null);
                travelInsurance.setState(TravelInsuranceState.ABORTING);
                travelInsuranceRepository.save(travelInsurance);

                //call api query
                QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = new QueryTravelInsuranceBICRequest();
                queryTravelInsuranceBICRequest.setInquiryType(2L);
                queryTravelInsuranceBICRequest.setOrderReference(orderReference);
                BaseDetail baseDetailQuery = new BaseDetail();
                baseDetailQuery.setDetail(queryTravelInsuranceBICRequest);
                baseDetailQuery.setRequestId(requestId);
                baseDetailQuery.setRequestTime(requestTime);
                baseDetailQuery.setTargetId(productProvider);
//
//                  // get result from API create.
                ResponseEntity<String> responseEntityQuery = travelInsuranceService.getOrderOutbox(baseDetailQuery, clientIp, clientId, startTime, hostName);
                String responseBodyQuery = mapper.writeValueAsString(responseEntityQuery);
                JSONObject jsonResponseBody = new JSONObject(responseBodyQuery);
                JSONObject trvQuery = jsonResponseBody.getJSONObject("body").getJSONObject("detail").getJSONObject("trv");
                JSONArray trvDetailsQuery = jsonResponseBody.getJSONObject("body").getJSONObject("detail").getJSONArray("trvDetails");
                List<TRVDetail> trvDetailsList = Arrays.asList(mapper.readValue(trvDetailsQuery.toString(), TRVDetail[].class));

                OrderUpdate orders= new OrderUpdate();
                BICTransaction bicTransaction = bicTransactionRepository.findBICTransactionSuccessByOrderRef(orderReference).orElse(null);
                orders.setOrderId(bicTransaction.getOrderId());
                orders.setOrderReference(orderReference);
                orders.setOrdBillEmail(bicTransaction.getEmail());
                orders.setOrdBillStreet1(bicTransaction.getCustomerAddress());
                orders.setOrdBillFirstName(bicTransaction.getCustomerName());
                orders.setOrdBillMobile(bicTransaction.getPhoneNumber());
                orders.setOrdStatus(1L);//chưa thanh toán

                TRVUpdate trv = new TRVUpdate();
                trv.setDestroy(1L);//hủy
                trv.setTrvId(trvQuery.getLong("trvId"));
                trv.setOrderId(bicTransaction.getOrderId());

                TRVDetail trvDetails = trvDetailsList.get(0);
                TRVDetailUpdate trvDetailUpdate = new TRVDetailUpdate();
                trvDetailUpdate.setTrvId(trvDetails.getTrvId());
                trvDetailUpdate.setFullName(trvDetails.getFullName());
                trvDetailUpdate.setGender(trvDetails.getGender());
                trvDetailUpdate.setDateOfBirth(trvDetails.getDateOfBirth());
                trvDetailUpdate.setPassportCard(trvDetails.getPassportCard());

                ArrayList<TRVDetailUpdate> trvDetailUpdates = new ArrayList<>();
                trvDetailUpdates.add(trvDetailUpdate);

                UpdateTravelInsuranceBICRequest updateTravelInsuranceBICRequest = new UpdateTravelInsuranceBICRequest();
                updateTravelInsuranceBICRequest.setOrders(orders);
                updateTravelInsuranceBICRequest.setTrv(trv);
                updateTravelInsuranceBICRequest.setTrvDetails(trvDetailUpdates);

                BaseDetail baseDetail = new BaseDetail();
                baseDetail.setDetail(updateTravelInsuranceBICRequest);
                baseDetail.setRequestId(requestId);
                baseDetail.setRequestTime(requestTime);
                baseDetail.setTargetId(productProvider);

                try {
                    // get result from API create.
                    ResponseEntity<String> responseEntity = travelInsuranceService.updateOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
                    String responseBody = mapper.writeValueAsString(responseEntity);
                    JSONObject jsonBody = new JSONObject(responseBody);
                    jsonBody.put("productName", productName);
                    jsonBody.put("requestPayload", jsonPayload.toString());
//                        jsonBody.put("OrderReference", orderReference);
                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                    //insert to outbox
                    if (statusCodeValue == 200) {
                        travelInsurance.setState(TravelInsuranceState.ABORTED);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebody + status+requestId
                        jsonBody.put("status", "success");
                        outBox.setPayload(jsonBody.toString());
                    } else {
                        travelInsurance.setState(TravelInsuranceState.ABORT_FAILURE);

                        outBox.setCreatedLogtimestamp(createAt);
                        outBox.setAggregateId(aggregateId);
                        outBox.setAggregateType(AggregateType.Order);
                        outBox.setType(type);

                        //payload = responsebody + status+requestId
                        jsonBody.put("status", "failure");
                        outBox.setPayload(jsonBody.toString());
                    }
                    travelInsuranceRepository.save(travelInsurance);
                    outboxRepository.save(outBox);
                } catch (Exception ex) {
                    travelInsurance.setState(TravelInsuranceState.ABORT_FAILURE);
                    travelInsuranceRepository.save(travelInsurance);

                    outBox.setCreatedLogtimestamp(createAt);
                    outBox.setAggregateId(aggregateId);
                    outBox.setAggregateType(AggregateType.Order);
                    outBox.setType(type);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("requestPayload",jsonPayload.toString());
                    jsonBody.put("productName", productName);
                    jsonBody.put("status", "failure");
                    outBox.setPayload(jsonBody.toString());
                    outboxRepository.save(outBox);

                    throw ex;
                }

            }
        }
    }

}
