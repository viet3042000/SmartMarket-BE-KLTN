package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.BICTransactionRepository;
import com.smartmarket.code.dao.OutboxRepository;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Outbox;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.request.BaseJobDetail;
import com.smartmarket.code.request.QueryTravelInsuranceBICRequest;
import com.smartmarket.code.service.*;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    OrderOutboxService orderOutboxService;

    @Autowired
    TravelInsuranceService travelInsuranceService;

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

        //order_id
        String aggregateId = "";
//        String aggregateType = "";
        String type = "";
        String payload = "";

//        String orderReference ="";

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
//                                if (k.equals("aggregatetype")) {
//                                    aggregateType =(String) keyPairs.get(k);
//                                }
                                if (k.equals("type")) {
                                    type = (String) keyPairs.get(k);
                                }
                                if (k.equals("payload")) {
                                    payload =(String) keyPairs.get(k);
                                }
                            }
                            JSONObject jsonPayload = new JSONObject(payload);
                            orderOutboxService.processMessageFromOrderOutbox(op,aggregateId,type,
                                                                     jsonPayload);

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
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


//    @KafkaListener(id = "${kafka.groupID.jobmanagementoutbox}",topics = "${kafka.topic.jobmanagementoutbox}")
    public void listenJobManagementServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception {
        String op ="";
        String payload ="";
        String requestId = "";
        Long pendingId= 0L;
        String orderId ="";
        String orderReference ="";
        Long startTime = 0L;
        String intervalId = "";
        String type = "";
        int step = 0;

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

                            for (String k : keyPairs.keySet()) {
                                if (k.equals("payload")) {
                                    payload= (String) keyPairs.get(k);
                                }
                                if (k.equals("aggregate_id")) {
                                    //= aggregate_id
                                    requestId =(String) keyPairs.get(k);
                                }
                            }

                            //step, intervalId, startTime,orderReference, orderId,pendingId = payload.get
                            JSONObject j = new JSONObject(payload);
                            orderId = j.getString("orderId");
                            orderReference = j.getString("orderReference");
                            pendingId = j.getLong("pendingId");
                            startTime = j.getLong("startTime");
                            intervalId = j.getString("intervalId");
                            type = j.getString("type");
                            step = j.getInt("step");

                            if (op.equals("c")) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                Optional<PendingBICTransaction> pendingBICTransaction = pendingBICTransactionRepository.findById(pendingId);
                                if(pendingBICTransaction.isPresent()) {
                                    //ìf count <=5
                                    if (pendingBICTransaction.get().getCount() == null || pendingBICTransaction.get().getCount() <= 5) {

                                        JSONObject detail = new JSONObject();
                                        detail.put("inquiryType", 2);
                                        detail.put("orderId", orderId);
                                        detail.put("orderReference", orderReference);

                                        String d = detail.toString();
                                        QueryTravelInsuranceBICRequest queryTravelInsuranceBICRequest = g.fromJson(d, QueryTravelInsuranceBICRequest.class);
                                        BaseJobDetail baseJobDetail = new BaseJobDetail();
                                        baseJobDetail.setDetail(queryTravelInsuranceBICRequest);
                                        baseJobDetail.setRequestId(requestId);

                                        // get result from API create.
                                        ResponseEntity<String> responseEntity = travelInsuranceService.getJobOutbox(baseJobDetail, startTime);
                                        ObjectMapper mapper = new ObjectMapper();
                                        String responseBody = mapper.writeValueAsString(responseEntity);
                                        JSONObject jsonBody = new JSONObject(responseBody);
                                        int statusCodeValue = jsonBody.getInt("statusCodeValue");

                                        if (statusCodeValue == 200) {

                                            //if type is createTravelInsuranceBIC
                                            if(pendingBICTransaction.get().getType().equals("createTravelInsuranceBIC")) {

                                                Optional<BICTransaction> bicTransaction = bicTransactionRepository.findBICTransactionPending(orderId, orderReference, requestId);
                                                if (bicTransaction.isPresent()) {
                                                    BICTransaction b = bicTransaction.get();

                                                    //modify bictransaction (result_code, BIC result_code )
                                                    b.setResultCode(ResponseCode.CODE.TRANSACTION_SUCCESSFUL);
                                                    b.setBicResultCode("200 OK");
                                                    bicTransactionRepository.save(b);
                                                }

                                                // Call api cancel

                                                //add to outbox to job know what order in 1 interval success
                                                Outbox outBoxJobService = new Outbox();

                                                Date dateJob = new Date();
                                                String stringCreateAtJob = formatter.format(dateJob);
                                                Date createAtJob = formatter.parse(stringCreateAtJob);
                                                outBoxJobService.setCreatedLogtimestamp(createAtJob);
                                                outBoxJobService.setAggregateId(requestId);
                                                outBoxJobService.setAggregateType("Job");
                                                outBoxJobService.setType(type);

                                                //response body + orderReference, order id, intervalId, step,status
                                                jsonBody.put("orderId",orderId);
                                                jsonBody.put("orderReference",orderReference);
                                                jsonBody.put("status","success");
                                                jsonBody.put("intervalId",intervalId);
                                                jsonBody.put("step",step);
                                                outBoxJobService.setPayload(jsonBody.toString());

                                                outboxRepository.save(outBoxJobService);

                                                //delete pending by id
                                                pendingBICTransactionRepository.deletePendingBICTransactionByID(pendingId);
                                            }
                                            //type = updateTravelInsuranceBIC
                                            else {

                                            }
                                        }
                                    }
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

            //case order not exist
            if(ex instanceof CustomException) {
                if (((CustomException) ex).getDetailErrorMessage().equals("Not found order")) {
                    pendingBICTransactionRepository.deletePendingBICTransactionByID(pendingId);
                }

                ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
                        "outbox", op, dateTimeFormatter.format(currentTime),
                        ((CustomException) ex).getErrorMessage(), ((CustomException) ex).getHttpStatusCode(), Throwables.getStackTraceAsString(ex));
                logService.createListenerLogExceptionException(listenerExceptionObject);

            } else {
                Optional<PendingBICTransaction> pendingBICTransaction = pendingBICTransactionRepository.findById(pendingId);
                if (pendingBICTransaction.isPresent()) {
                    PendingBICTransaction p = pendingBICTransaction.get();
                    if (p.getCount() < 5) {
                        Long count = p.getCount();
                        count++;
                        p.setCount(count);
                        pendingBICTransactionRepository.save(p);
                    }

                    ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicJobManagementOutbox,
                            "outbox", op, dateTimeFormatter.format(currentTime),
                            ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
                    logService.createListenerLogExceptionException(listenerExceptionObject);
                }
            }
            //add to outbox to job know what order in 1 interval failure
            Outbox outBoxJobService = new Outbox();
            outBoxJobService.setAggregateId(requestId);
            outBoxJobService.setAggregateType("Job");
            outBoxJobService.setType(type);

            //response body + orderReference, order id, intervalId, step + status
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderId", orderId);
            jsonBody.put("orderReference", orderReference);
            jsonBody.put("intervalId", intervalId);
            jsonBody.put("status", "failure");
            jsonBody.put("step", step);
            outBoxJobService.setPayload(jsonBody.toString());
            outBoxJobService.setCreatedLogtimestamp(new Date());
            outboxRepository.save(outBoxJobService);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }

}
