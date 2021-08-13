package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.TravelinsuranceOutbox;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.request.*;
import com.smartmarket.code.service.ListenerService;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    TravelInsuranceServiceImpl travelInsuranceService;

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
        UUID orderId = UUID.randomUUID();
        Long id = 0L;
        String aggregateId = "";
        String aggregateType = "";
        String type = "";
        String payload = "";
        String clientIp = "";
        String clientId = "";
        Long startTime = 0L;
        String hostName="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        TravelinsuranceOutbox outBox = new TravelinsuranceOutbox();
        Gson g = new Gson();
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

                            if (op.equals("c")) {
                                for (String k : keyPairs.keySet()) {
                                    if (k.equals("id")) {
                                        id = ((Number)keyPairs.get(k)).longValue();
                                    }
                                    if (k.equals("order_id")) {
                                        String s = (String) keyPairs.get(k);
                                        orderId= UUID.fromString(s);
//                                        orderId = ((Number)keyPairs.get(k)).longValue();
                                    }
                                    if (k.equals("aggregateid")) {
                                        aggregateId =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("aggregatetype")) {
                                        aggregateType =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("type")) {
                                        type =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("payload")) {
                                        payload =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("client_ip")) {
                                        clientIp =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("client_id")) {
                                        clientId =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("start_time")) {
                                        startTime = ((Number)keyPairs.get(k)).longValue();
                                    }
                                    if (k.equals("host_name")) {
                                        hostName = (String) keyPairs.get(k);
                                    }
                                }

                                if(type.equals("createTravelInsuranceBIC")){
                                    //convert payload--> BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest
                                    JSONObject jsonPayload = new JSONObject(payload);
                                    JSONObject detail = jsonPayload.getJSONObject("detail");
                                    String d = detail.toString();
                                    CreateTravelInsuranceBICRequest createTravelInsuranceBICRequest = g.fromJson(d, CreateTravelInsuranceBICRequest.class);
                                    BaseDetail baseDetail = new BaseDetail();
                                    baseDetail.setDetail(createTravelInsuranceBICRequest);
                                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
                                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
                                    baseDetail.setTargetId(jsonPayload.getString("targetId"));

                                    // get result from API create.
                                    ResponseEntity<String> responseEntity = travelInsuranceService.createOrderOutbox(baseDetail,clientIp,clientId, startTime,hostName);
                                    ObjectMapper mapper = new ObjectMapper();
                                    String responseBody = mapper.writeValueAsString(responseEntity);
                                    JSONObject jsonBody = new JSONObject(responseBody);
                                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                                    //insert to outbox
                                    if(statusCodeValue == 200){
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("success");
                                        outBox.setPayload(responseBody);
                                    }else {
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("failure");
                                        outBox.setPayload(responseBody);
                                    }
                                    outboxRepository.save(outBox);
                                }

                                if(type.equals("updateTravelInsuranceBIC")){
                                    //convert payload--> BaseDetail<CreateTravelInsuranceBICRequest> createTravelInsuranceBICRequest
                                    JSONObject jsonPayload = new JSONObject(payload);
                                    JSONObject detail = jsonPayload.getJSONObject("detail");

                                    String d = detail.toString();
                                    UpdateTravelInsuranceBICRequest updateTravelInsuranceBICRequest = g.fromJson(d, UpdateTravelInsuranceBICRequest.class);
                                    BaseDetail baseDetail = new BaseDetail();
                                    baseDetail.setDetail(updateTravelInsuranceBICRequest);
                                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
                                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
                                    baseDetail.setTargetId(jsonPayload.getString("targetId"));

                                    // get result from API create.
                                    ResponseEntity<String> responseEntity = travelInsuranceService.updateOrderOutbox(baseDetail,clientIp,clientId,startTime,hostName);
                                    ObjectMapper mapper = new ObjectMapper();
                                    String responseBody = mapper.writeValueAsString(responseEntity);
                                    JSONObject jsonBody = new JSONObject(responseBody);
                                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                                    //insert to outbox
                                    if(statusCodeValue == 200){
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("success");
                                        outBox.setPayload(responseBody);
                                    }else {
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("failure");
                                        outBox.setPayload(responseBody);
                                    }
                                    outboxRepository.save(outBox);
                                }

                                if(type.equals("getTravelInsuranceBIC")){
                                    JSONObject jsonPayload = new JSONObject(payload);
                                    JSONObject detail = jsonPayload.getJSONObject("detail");

                                    String d = detail.toString();
                                    QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
                                    BaseDetail baseDetail = new BaseDetail();
                                    baseDetail.setDetail(queryTravelInsuranceBICRequest);
                                    baseDetail.setRequestId(jsonPayload.getString("requestId"));
                                    baseDetail.setRequestTime(jsonPayload.getString("requestTime"));
                                    baseDetail.setTargetId(jsonPayload.getString("targetId"));

                                    // get result from API create.
                                    ResponseEntity<String> responseEntity = travelInsuranceService.getOrderOutbox(baseDetail,clientIp,clientId,startTime,hostName);
                                    ObjectMapper mapper = new ObjectMapper();
                                    String responseBody = mapper.writeValueAsString(responseEntity);
                                    JSONObject jsonBody = new JSONObject(responseBody);
                                    int statusCodeValue = jsonBody.getInt("statusCodeValue");

                                    //insert to outbox
                                    if(statusCodeValue == 200){
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("success");
                                        outBox.setPayload(responseBody);
                                    }else {
                                        outBox.setId(id);
                                        outBox.setOrderId(orderId);
                                        outBox.setAggregateId(aggregateId);
                                        outBox.setAggregateType("OrderService");
                                        outBox.setType(type);
                                        outBox.setStatus("failure");
                                        outBox.setPayload(responseBody);
                                    }
                                    outboxRepository.save(outBox);
                                }

                            }
                            if (op.equals("r")) {
//                                countReadOutBox ++;

                            }
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
            outBox.setId(id);
            outBox.setOrderId(orderId);
            outBox.setAggregateId(aggregateId);
            outBox.setAggregateType("OrderService");
            outBox.setType(type);
            outBox.setStatus("failure");
            outBox.setPayload("Exception in TravelInsuranceService");
            outboxRepository.save(outBox);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    @KafkaListener(id = "${kafka.groupID.jobmanagementoutbox}",topics = "${kafka.topic.jobmanagementoutbox}")
    public void listenJobManagementServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception {
        String op ="";
        Long id = 0L;
        String orderId ="";
        String orderReference ="";
        String requestId = "";
        Long startTime = 0L;

        Long count =0L;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Gson g = new Gson();
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

                            if (op.equals("c")) {
                                for (String k : keyPairs.keySet()) {
                                    if (k.equals("id")) {
                                        id = ((Number)keyPairs.get(k)).longValue();
                                    }
                                    if (k.equals("order_id")) {
                                        orderId= (String) keyPairs.get(k);
                                    }
                                    if (k.equals("order_reference")) {
                                        orderReference= (String) keyPairs.get(k);
                                    }
                                    if (k.equals("request_id")) {
                                        requestId =(String) keyPairs.get(k);
                                    }
                                    if (k.equals("start_time")) {
                                        startTime = ((Number)keyPairs.get(k)).longValue();
                                    }
                                }

                                //ìf count <=2
                                if(count <=2) {
                                    JSONObject detail = new JSONObject();
                                    detail.put("inquiryType",2);
                                    detail.put("orderId",orderId);
                                    detail.put("orderReference",orderReference);

                                    String d = detail.toString();
                                    QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
                                    BaseJobDetail baseJobDetail = new BaseJobDetail();
                                    baseJobDetail.setDetail(queryTravelInsuranceBICRequest);
                                    baseJobDetail.setRequestId(requestId);

                                    // get result from API create.
                                    ResponseEntity<String> responseEntity = travelInsuranceService.getJobOutbox(baseJobDetail,startTime);
                                    ObjectMapper mapper = new ObjectMapper();
                                    String responseBody = mapper.writeValueAsString(responseEntity);
                                    JSONObject jsonBody = new JSONObject(responseBody);
                                    int statusCodeValue = jsonBody.getInt("statusCodeValue");


                                    if (statusCodeValue == 200) {
                                         //modify bictransaction (result_code, BIC result_code )
                                         //find bictransaction by requestid , orderid, orderref from pending
//                                        bicTransactionRepository.deleteBICTransactionPending(orderId,orderReference,requestId);
                                        Optional<BICTransaction> bicTransaction = bicTransactionRepository.findBICTransactionPending(orderId,orderReference,requestId);
                                        if(bicTransaction.isPresent()){
                                            BICTransaction b = bicTransaction.get();
                                            b.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                                            b.setBicResultCode("200 OK");
                                            bicTransactionRepository.save(b);
                                        }

                                        //delete pending by id
                                        pendingBICTransactionRepository.deletePendingBICTransactionByID(id);

                                    } else {
                                        //count in pending ++
                                        count ++;
                                        Optional<PendingBICTransaction> pendingBICTransaction = pendingBICTransactionRepository.findById(id);
                                        if(pendingBICTransaction.isPresent()) {
                                            PendingBICTransaction p = pendingBICTransaction.get();
                                            p.setCount(count);
                                            pendingBICTransactionRepository.save(p);
                                        }
                                    }
                                }else{
                                    //delete pending by id if count >2
                                    pendingBICTransactionRepository.deletePendingBICTransactionByID(id);
                                }

                            }
                            if (op.equals("r")) {
//                                countReadOutBox ++;

                            }
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
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
                    "outbox", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
                    "outbox", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
                    "outbox", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }
}
