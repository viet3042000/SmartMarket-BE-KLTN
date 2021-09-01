package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.AggregateType;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.constants.TravelInsuranceState;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.dao.TravelInsuranceRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.TravelInsurance;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.*;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    OrderOutboxService orderOutboxService;

    @Autowired
    DataBaseUserService dataBaseUserService;

    @Autowired
    DataBaseUserRoleService dataBaseUserRoleService;

    @Autowired
    DataBaseUserProfileService dataBaseUserProfileService;

    @Autowired
    DataBaseRoleService dataBaseRoleService;

    @Autowired
    BICTransactionRepository bicTransactionRepository;

    @Autowired
    PendingBICTransactionRepository pendingBICTransactionRepository;

    @Autowired
    OutboxRepository outboxRepository;

    @Value("${kafka.topic.orderoutbox}")
    String topicOrderOutbox;

    @Value("${kafka.topic.jobmanagementoutbox}")
    String topicJobManagementOutbox;

    @Value("${kafka.topic.users}")
    String topicUsers;

    @Value("${kafka.topic.user_role}")
    String topicUserRole;

    @Value("${kafka.topic.roles}")
    String topicRole;

    @Value("${kafka.topic.user_profile}")
    String topicUserProfile;

    //published when the consumer appears to be blocked in the poll method.
//    @EventListener
//    public void handleNonResponsiveConsumerEvent(NonResponsiveConsumerEvent event) {
//        System.out.println("ERROR_KAFKA_NONRESPONSIVECONSUMEREVENT " + event.getListenerId() + " LOG_MSG_DELIMITER " + event.toString());
////        event.getConsumer().paused();
//    }
//
//    @EventListener
//    public void handleContextRefreshed(ContextRefreshedEvent event) throws InterruptedException {
//        System.out.println("Cannot connect to Kafka");
//    }


    //outbox table only insert --> only crete/read
    @KafkaListener(id = "${kafka.groupID.orderoutbox}",topics = "${kafka.topic.orderoutbox}")
    public void listenOrderServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception {
        String op ="";
        //order_id
        String aggregateId = "";
        String aggregateType = "";
        String type = "";
        String payload = "";

        String requestId = "";
        String orderReference ="";
//        JSONObject detail = new JSONObject();

//        Outbox outBox = new Outbox();
//        Gson g = new Gson();
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        op = payloadObj.getString("op");

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            for (String k : keyPairs.keySet()) {
                                if (k.equals("aggregateid")) {
                                    aggregateId =(String) keyPairs.get(k);
                                }
                                if (k.equals("aggregatetype")) {
                                    aggregateType =(String) keyPairs.get(k);
                                }
                                if (k.equals("type")) {
                                    type = (String) keyPairs.get(k);
                                }
                                if (k.equals("payload")) {
                                    payload =(String) keyPairs.get(k);
                                }
                            }
                            JSONObject jsonPayload = new JSONObject(payload);
                            requestId = jsonPayload.getString("requestId");

                            orderOutboxService.processMessageFromOrderOutbox(op,aggregateId,type,orderReference,
                                                                             requestId,jsonPayload);

                            //optimize_start_point
//                            if (op.equals("c")) {
//                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                                Date date = new Date();
//                                String stringCreateAt = formatter.format(date);
//                                Date createAt = formatter.parse(stringCreateAt);
//
//                                detail = jsonPayload.getJSONObject("detail");
//                                String d = detail.toString();
//
//                                if(type.equals("createTravelInsuranceBIC")){
//                                    orderReference = UUID.randomUUID().toString();
//                                    detail.getJSONObject("orders").put("orderReference",orderReference);
//
//                                    TravelInsurance travelInsurance = new TravelInsurance();
//                                    travelInsurance.setId(orderReference);
//                                    travelInsurance.setState(TravelInsuranceState.CREATING);
//                                    travelInsurance.setProductName("TravelInsuranceBIC");
//                                    travelInsurance.setCreatedLogtimestamp(createAt);
//                                    travelInsuranceRepository.save(travelInsurance);
//
//                                    CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = g.fromJson(detail.toString(), CreateTravelInsuranceBICRequest.class);
//                                    BaseDetail baseDetail = new BaseDetail();
//                                    baseDetail.setDetail(createTravelInsuranceBICRequest);
//                                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
//                                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
//                                    baseDetail.setTargetId(jsonPayload.getString("targetId"));
//
//                                    try {
//                                        // get result from API create.
//                                        ResponseEntity<String> responseEntity = travelInsuranceService.createOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
//                                        ObjectMapper mapper = new ObjectMapper();
//                                        String responseBody = mapper.writeValueAsString(responseEntity);
//                                        JSONObject jsonBody = new JSONObject(responseBody);
//                                        jsonBody.put("requestId", requestId);
//                                        jsonBody.put("OrderReference", orderReference);
//                                        int statusCodeValue = jsonBody.getInt("statusCodeValue");
//
//                                        Date dateFinished = new Date();
//                                        String stringFinishedAt = formatter.format(dateFinished);
//                                        Date finishedAt = formatter.parse(stringFinishedAt);
//                                        travelInsurance.setFinishedLogtimestamp(finishedAt);
//                                        //insert to outbox
//                                        if (statusCodeValue == 200) {
//                                            travelInsurance.setState(TravelInsuranceState.SUCCEEDED);
//
//                                            outBox.setCreatedLogtimestamp(createAt);
//                                            outBox.setAggregateId(aggregateId);
//                                            outBox.setAggregateType(AggregateType.Order);
//                                            outBox.setType(type);
//
//                                            //payload = responsebody + status+requestId + orderRef
//                                            jsonBody.put("status", "success");
//                                            outBox.setPayload(jsonBody.toString());
//                                        } else {
//                                            travelInsurance.setState(TravelInsuranceState.CREATE_FAILURE);
//
//                                            outBox.setCreatedLogtimestamp(createAt);
//                                            outBox.setAggregateId(aggregateId);
//                                            outBox.setAggregateType(AggregateType.Order);
//                                            outBox.setType(type);
//
//                                            //payload = responsebpdy + status+requestId+ orderRef
//                                            jsonBody.put("status", "failure");
//                                            outBox.setPayload(jsonBody.toString());
//                                        }
//                                        travelInsuranceRepository.save(travelInsurance);
//                                        outboxRepository.save(outBox);
//                                    }catch (Exception ex){
//                                        travelInsurance.setState(TravelInsuranceState.CREATE_FAILURE);
//
//                                        Date dateFinished = new Date();
//                                        String stringFinishedAt = formatter.format(dateFinished);
//                                        Date finishedAt = formatter.parse(stringFinishedAt);
//                                        travelInsurance.setFinishedLogtimestamp(finishedAt);
//                                        travelInsuranceRepository.save(travelInsurance);
//
//                                        throw ex;
//                                    }
//                                }
//
//                                if(type.equals("updateTravelInsuranceBIC")){
//                                    String orderId = "";
//
//                                    Optional<BICTransaction> bicTransaction = bicTransactionRepository.findBICTransactionSuccessByOrderRef(detail.getJSONObject("orders").getString("orderReference"));
//                                    if(bicTransaction.isPresent()) {
//                                        orderId= bicTransaction.get().getOrderId();
//                                        detail.getJSONObject("orders").put("orderId", orderId);
//                                    }
//
//                                    Optional<TravelInsurance> travelInsurance = travelInsuranceRepository.findById(detail.getJSONObject("orders").getString("orderReference"));
//                                    if(travelInsurance.isPresent()) {
//                                        TravelInsurance t = travelInsurance.get();
//                                        t.setState(TravelInsuranceState.UPDATING);
//                                        travelInsuranceRepository.save(t);
//
//                                        UpdateTravelInsuranceBICRequest updateTravelInsuranceBICRequest = g.fromJson(detail.toString(), UpdateTravelInsuranceBICRequest.class);
//                                        BaseDetail baseDetail = new BaseDetail();
//                                        baseDetail.setDetail(updateTravelInsuranceBICRequest);
//                                        baseDetail.setRequestId(jsonPayload.getString("requestId"));
//                                        baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
//                                        baseDetail.setTargetId(jsonPayload.getString("targetId"));
//
//                                        try {
//                                            // get result from API create.
//                                            ResponseEntity<String> responseEntity = travelInsuranceService.updateOrderOutbox(baseDetail, clientIp, clientId, startTime, hostName);
//                                            ObjectMapper mapper = new ObjectMapper();
//                                            String responseBody = mapper.writeValueAsString(responseEntity);
//                                            JSONObject jsonBody = new JSONObject(responseBody);
//                                            jsonBody.put("requestId", requestId);
//                                            int statusCodeValue = jsonBody.getInt("statusCodeValue");
//
//                                            //insert to outbox
//                                            if (statusCodeValue == 200) {
//                                                t.setState(TravelInsuranceState.SUCCEEDED);
//
//                                                outBox.setCreatedLogtimestamp(createAt);
//                                                outBox.setAggregateId(aggregateId);
//                                                outBox.setAggregateType(AggregateType.Order);
//                                                outBox.setType(type);
//
//                                                //payload = responsebody + status+requestId
//                                                jsonBody.put("status", "success");
//                                                outBox.setPayload(jsonBody.toString());
//                                            } else {
//                                                t.setState(TravelInsuranceState.UPDATE_FAILURE);
//
//                                                outBox.setCreatedLogtimestamp(createAt);
//                                                outBox.setAggregateId(aggregateId);
//                                                outBox.setAggregateType(AggregateType.Order);
//                                                outBox.setType(type);
//
//                                                //payload = responsebody + status+requestId
//                                                jsonBody.put("status", "failure");
//                                                outBox.setPayload(jsonBody.toString());
//                                            }
//                                            travelInsuranceRepository.save(t);
//                                            outboxRepository.save(outBox);
//                                        }catch(Exception ex){
//                                            t.setState(TravelInsuranceState.UPDATE_FAILURE);
//                                            travelInsuranceRepository.save(t);
//                                            throw ex;
//                                        }
//                                    }
//                                }
//
//                                if(type.equals("getTravelInsuranceBIC")){
//                                    QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
//                                    BaseDetail baseDetail = new BaseDetail();
//                                    baseDetail.setDetail(queryTravelInsuranceBICRequest);
//                                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
//                                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
//                                    baseDetail.setTargetId(jsonPayload.getString("targetId"));
//
//                                    // get result from API create.
//                                    ResponseEntity<String> responseEntity = travelInsuranceService.getOrderOutbox(baseDetail,clientIp,clientId,startTime,hostName);
//                                    ObjectMapper mapper = new ObjectMapper();
//                                    String responseBody = mapper.writeValueAsString(responseEntity);
//                                    JSONObject jsonBody = new JSONObject(responseBody);
//                                    jsonBody.put("requestId",requestId);
//                                    int statusCodeValue = jsonBody.getInt("statusCodeValue");
//
//                                    outBox.setCreatedLogtimestamp(createAt);
//                                    outBox.setAggregateId(aggregateId);
//                                    outBox.setAggregateType(AggregateType.Order);
//                                    outBox.setType(type);
//                                    //insert to outbox
//                                    if(statusCodeValue == 200){
//                                        //payload = responsebody+ status+requestId
//                                        jsonBody.put("status","success");
//                                        outBox.setPayload(jsonBody.toString());
//                                    }else {
//                                        //payload = responsebody+ status+requestId
//                                        jsonBody.put("status","failure");
//                                        outBox.setPayload(jsonBody.toString());
//                                    }
//                                    outboxRepository.save(outBox);
//                                }
//                            }
                            //optimize_end_point

                        } else {
                            System.out.println("afterObj is null");
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }else{
                    System.out.println("record.value is null");
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        } catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
            // nên coordinator tưởng là consumer chết rồi-->Không commit được
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicOrderOutbox,
                    "outbox", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicOrderOutbox,
                    "outbox", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicOrderOutbox,
                    "outbox", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

//            insert into travelinsurance_outbox table
            Outbox outBox = new Outbox();
            outBox.setAggregateId(aggregateId);
            outBox.setAggregateType(AggregateType.Order);
            outBox.setType(type);

            //payload = responsebody + status+ request_id + orderRef
            JSONObject jsonBody = new JSONObject();
            if(type.equals("createTravelInsuranceBIC")){
                jsonBody.put("OrderReference",orderReference);
            }
            jsonBody.put("requestId",requestId);
            jsonBody.put("status","failure");
            outBox.setPayload(jsonBody.toString());
            outboxRepository.save(outBox);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


//    @KafkaListener(id = "${kafka.groupID.jobmanagementoutbox}",topics = "${kafka.topic.jobmanagementoutbox}")
//    public void listenJobManagementServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception {
//        String op ="";
//        String payload ="";
//        String requestId = "";
//
//        Long pendingId= 0L;
//        String orderId ="";
//        String orderReference ="";
//        Long startTime = 0L;
//        String intervalId = "";
//        int step = 0;
//
//        Gson g = new Gson();
//        try {
//            for (ConsumerRecord<String, String> record : records) {
//                System.out.println(record.offset());
//                if(record.value() != null) {
//                    String valueRecord = record.value();
//                    JSONObject valueObj = new JSONObject(valueRecord);
//                    if (!valueObj.isNull("payload")) {
//                        JSONObject payloadObj = valueObj.getJSONObject("payload");
//                        JSONObject sourceObj = payloadObj.getJSONObject("source");
//                        op = payloadObj.getString("op");
//
//                        if (!payloadObj.isNull("after")) {
//                            JSONObject afterObj = payloadObj.getJSONObject("after");
//
//                            //Get key-pair in afterObj
//                            Map<String, Object> keyPairs = new HashMap<>();
//                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);
//
//                            for (String k : keyPairs.keySet()) {
//                                if (k.equals("payload")) {
//                                    payload= (String) keyPairs.get(k);
//                                }
//                                if (k.equals("aggregateid")) {
//                                    //= aggrate id
//                                    requestId =(String) keyPairs.get(k);
//                                }
//                            }
//
//                            //step, intervalId, startTime,orderReference, orderId,pendingId = payload.get
//                            JSONObject j = new JSONObject(payload);
//                            orderId = j.getString("orderId");
//                            orderReference = j.getString("orderReference");
//                            pendingId = j.getLong("pendingId");
//                            startTime = j.getLong("startTime");
//                            intervalId = j.getString("intervalId");
//                            step = j.getInt("step");
//
//                            if (op.equals("c")) {
//                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//
//                                Optional<PendingBICTransaction> pendingBICTransaction = pendingBICTransactionRepository.findById(pendingId);
//                                if(pendingBICTransaction.isPresent()) {
//                                    //ìf count <=5
//                                    if (pendingBICTransaction.get().getCount() == null || pendingBICTransaction.get().getCount() <= 5) {
//
//                                        JSONObject detail = new JSONObject();
//                                        detail.put("inquiryType", 2);
//                                        detail.put("orderId", orderId);
//                                        detail.put("orderReference", orderReference);
//
//                                        String d = detail.toString();
//                                        QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
//                                        BaseJobDetail baseJobDetail = new BaseJobDetail();
//                                        baseJobDetail.setDetail(queryTravelInsuranceBICRequest);
//                                        baseJobDetail.setRequestId(requestId);
//
//                                        // get result from API create.
//                                        ResponseEntity<String> responseEntity = travelInsuranceService.getJobOutbox(baseJobDetail, startTime);
//                                        ObjectMapper mapper = new ObjectMapper();
//                                        String responseBody = mapper.writeValueAsString(responseEntity);
//                                        JSONObject jsonBody = new JSONObject(responseBody);
//                                        int statusCodeValue = jsonBody.getInt("statusCodeValue");
//
//                                        if (statusCodeValue == 200) {
//
//                                            //if type is createTravelInsuranceBIC
//                                            if(pendingBICTransaction.get().getType().equals("createTravelInsuranceBIC")) {
//
//                                                Optional<BICTransaction> bicTransaction = bicTransactionRepository.findBICTransactionPending(orderId, orderReference, requestId);
//                                                if (bicTransaction.isPresent()) {
//                                                    BICTransaction b = bicTransaction.get();
//
//                                                    //modify bictransaction (result_code, BIC result_code )
//                                                    b.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
//                                                    b.setBicResultCode("200 OK");
//                                                    bicTransactionRepository.save(b);
//                                                }
//
//                                                //OrderService
//                                                Outbox outBoxOrderService = new Outbox();
//
//                                                Date date = new Date();
//                                                String stringCreateAtOrder = formatter.format(date);
//                                                Date createAtOrder = formatter.parse(stringCreateAtOrder);
//                                                outBoxOrderService.setCreatedLogtimestamp(createAtOrder);
//
//                                                outBoxOrderService.setAggregateId(orderReference);
//                                                outBoxOrderService.setAggregateType("Order");
//                                                outBoxOrderService.setType(pendingBICTransaction.get().getType());
//
//                                                //payload = responsebody + orderId = orderRef + status
//                                                jsonBody.put("orderId",orderReference);
//                                                jsonBody.put("status","success");
//                                                outBoxOrderService.setPayload(jsonBody.toString());
//                                                outboxRepository.save(outBoxOrderService);
//
//                                                //remove orderId = orderRef
//                                                jsonBody.remove("orderId");
//
//                                                //add to outbox to job know what order in 1 interval success
//                                                Outbox outBoxJobService = new Outbox();
//
//                                                Date dateJob = new Date();
//                                                String stringCreateAtJob = formatter.format(dateJob);
//                                                Date createAtJob = formatter.parse(stringCreateAtJob);
//                                                outBoxJobService.setCreatedLogtimestamp(createAtJob);
//
//                                                outBoxJobService.setAggregateId(requestId);
//                                                outBoxJobService.setAggregateType("Job");
//                                                outBoxJobService.setType(pendingBICTransaction.get().getType());
//
//                                                //response body + orderReference, order id, intervalId, step,status
//                                                jsonBody.put("orderId",orderId);
//                                                jsonBody.put("orderReference",orderReference);
//                                                jsonBody.put("intervalId",intervalId);
//                                                jsonBody.put("step",step);
//                                                outBoxJobService.setPayload(jsonBody.toString());
//
//                                                outboxRepository.save(outBoxJobService);
//
//                                                //delete pending by id
//                                                pendingBICTransactionRepository.deletePendingBICTransactionByID(pendingId);
//                                            }
//                                            //type = updateTravelInsuranceBIC
//                                            else {
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (op.equals("r")) {
////                                countReadOutBox ++;
//
//                            }
//                        } else {
//                            System.out.println("afterObj is null");
//                        }
//
//                    } else {
//                        System.out.println("payload is null");
//                    }
//                }else{
//                    System.out.println("record.value is null");
//                }
//            }
//
//            //Commit after processed record in batch (records)
//            acknowledgment.acknowledge();
//
//        } catch (CommitFailedException ex) {
//            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
//            // nên coordinator tưởng là consumer chết rồi-->Không commit được
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
//                    "outbox", op ,dateTimeFormatter.format(currentTime),
//                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createListenerLogExceptionException(listenerExceptionObject);
//
//        }catch (KafkaException ex){
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
//                    "outbox", op , dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createListenerLogExceptionException(listenerExceptionObject);
//
//        }catch (Exception ex) {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//
//            Optional<PendingBICTransaction> pendingBICTransaction = pendingBICTransactionRepository.findById(pendingId);
//            if (pendingBICTransaction.isPresent()) {
//                PendingBICTransaction p = pendingBICTransaction.get();
//                if (p.getCount() < 5) {
//                    Long count = p.getCount();
//                    count++;
//                    p.setCount(count);
//                    pendingBICTransactionRepository.save(p);
//                }
//
//                //case order not exist
//                if(ex instanceof CustomException) {
//
//                    //add to outbox to job know what order in 1 interval failure
//                    Outbox outBoxJobService = new Outbox();
//                    outBoxJobService.setAggregateId(requestId);
//                    outBoxJobService.setAggregateType("Job");
//                    outBoxJobService.setType(p.getType());
////                    outBoxJobService.setStatus("failure");
//
//                    //response body + orderReference, order id, intervalId, step + status
//                    JSONObject jsonBody = new JSONObject();
//                    jsonBody.put("orderId",orderId);
//                    jsonBody.put("orderReference",orderReference);
//                    jsonBody.put("intervalId",intervalId);
//                    jsonBody.put("status","failure");
//                    jsonBody.put("step",step);
//                    outBoxJobService.setPayload(jsonBody.toString());
//
//                    outboxRepository.save(outBoxJobService);
//
//                    ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
//                            "outbox", op , dateTimeFormatter.format(currentTime),
//                            ((CustomException) ex).getErrorMessage(), ((CustomException) ex).getHttpStatusCode(), Throwables.getStackTraceAsString(ex));
//                    logService.createListenerLogExceptionException(listenerExceptionObject);
//                }else {
//                    ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
//                            "outbox", op , dateTimeFormatter.format(currentTime),
//                            ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
//                    logService.createListenerLogExceptionException(listenerExceptionObject);
//                }
//            }
//        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we leave.
//            acknowledgment.acknowledge();
////            System.out.println("Closed consumer and we are done");
//        }
//    }


    @KafkaListener(id = "${kafka.groupID.users}",topics = "${kafka.topic.users}")
    public void listenUser(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op ="";
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        op = payloadObj.getString("op");

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                dataBaseUserService.createDatabaseUser(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseUserService.updateDatabaseUser(keyPairs);
                            }
//                            if (op.equals("r")) {
//                                dataBaseUserServiceImp.readAndUpdateDatabaseUser(keyPairs,countReadUser);
//                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseUserService.deleteDatabaseUser(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseUserService.truncateDatabaseUser();
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }else{
                    System.out.println("record.value is null");
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        }catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
            // nên coordinator tưởng là consumer chết rồi-->Không commit được
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    @KafkaListener(id = "${kafka.groupID.user_role}",topics = "${kafka.topic.user_role}")
    public void listenUserRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op ="";
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        op = payloadObj.getString("op");

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                dataBaseUserRoleService.createDatabaseUserRole(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseUserRoleService.updateDatabaseUserRole(keyPairs);
                            }
//                            if (op.equals("r")) {
//                                dataBaseUserServiceImp.readAndUpdateDatabaseUser(keyPairs,countReadUser);
//                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseUserRoleService.deleteDatabaseUserRole(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseUserRoleService.truncateDatabaseUserRole();
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }else{
                    System.out.println("record.value is null");
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        }catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
            // nên coordinator tưởng là consumer chết rồi-->Không commit được
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    @KafkaListener(id = "${kafka.groupID.user_profile}",topics = "${kafka.topic.user_profile}")
    public void listenUserProfile(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op ="";
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        op = payloadObj.getString("op");

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                dataBaseUserProfileService.createDatabaseUserProfile(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseUserProfileService.updateDatabaseUserProfile(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseUserProfileService.deleteDatabaseUserProfile(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseUserProfileService.truncateDatabaseUserProfile();
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }else{
                    System.out.println("record.value is null");
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        }catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
            // nên coordinator tưởng là consumer chết rồi-->Không commit được
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserProfile,
                    "users", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserProfile,
                    "users", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserProfile,
                    "users", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    @KafkaListener(id = "${kafka.groupID.roles}",topics = "${kafka.topic.roles}")
    public void listenRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op ="";
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        op = payloadObj.getString("op");

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                dataBaseRoleService.createDatabaseRole(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseRoleService.updateDatabaseRole(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseRoleService.deleteDatabaseRole(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseRoleService.truncateDatabaseRole();
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }else{
                    System.out.println("record.value is null");
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        }catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
            // nên coordinator tưởng là consumer chết rồi-->Không commit được
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }
}
