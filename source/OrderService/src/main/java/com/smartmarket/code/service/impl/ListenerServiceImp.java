package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.dao.SagaStateRepository;
import com.smartmarket.code.model.OrdersServiceEntity;
import com.smartmarket.code.model.SagaState;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.service.ListenerService;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    SagaStateRepository sagaStateRepository;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    DataBaseUserServiceImp dataBaseUserServiceImp;

    @Autowired
    DataBaseClientServiceImp dataBaseClientServiceImp;

    @Autowired
    DataBaseConsumerServiceImp dataBaseConsumerServiceImp;

    @Value("${kafka.topic.clients}")
    String topicClients;

    @Value("${kafka.topic.users}")
    String topicUsers;

    @Value("${kafka.topic.consumers}")
    String topicConsumers;

    @Value("${kafka.topic.travelinsuranceoutbox}")
    String topicTravelInsuranceOutbox;

    int countReadUser = 0;
    int countReadClient =0;
    int countReadConsumer =0;


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


    @KafkaListener(id = "${kafka.groupID.travelinsuranceoutbox}",topics = "${kafka.topic.travelinsuranceoutbox}")
    public void listenOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        String op = "";
//        UUID orderId = UUID.randomUUID();
        //=order ref
        String orderId ="";
        String aggregateId = "";
        String aggregateType = "";
        String type = "";
        String payload = "";
        String status = "";

        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if (record.value() != null) {
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
//                                    if (k.equals("order_id")) {
//                                        String s = (String) keyPairs.get(k);
//                                        orderId= UUID.fromString(s);
//                                    }
                                if (k.equals("order_id")) {
                                    orderId = (String) keyPairs.get(k);
                                }
                                if (k.equals("aggregateid")) {
                                    aggregateId = (String) keyPairs.get(k);
                                }
                                if (k.equals("aggregatetype")) {
                                    aggregateType = (String) keyPairs.get(k);
                                }
                                if (k.equals("type")) {
                                    type = (String) keyPairs.get(k);
                                }
                                if (k.equals("payload")) {
                                    payload = (String) keyPairs.get(k);
                                }
                                if (k.equals("status")) {
                                    status = (String) keyPairs.get(k);
                                }
                            }

                            if (op.equals("c")) {
                                if (aggregateType.equals("OrderService")) {
                                    if (type.equals("createTravelInsuranceBIC")) {
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                        OrdersServiceEntity orders = orderRepository.findByOrderId(orderId);
                                        SagaState sagaState = sagaStateRepository.findByOrderId(orderId);

                                        //insert to outbox
                                        if (status.equals("success")) {
                                            orders.setState("Success");
                                            Date date = new Date();
                                            String stringFinishedAt = formatter.format(date);
                                            Date finishedAt = formatter.parse(stringFinishedAt);
                                            orders.setFinishedAt(finishedAt);

                                            sagaState.setCurrentStep("TravelInsuranceService");

                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "SUCCEEDED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("SUCCEEDED");
                                        } else {
                                            orders.setState("CreateAborted");
                                            Date date = new Date();
                                            String stringFinishedAt = formatter.format(date);
                                            Date finishedAt = formatter.parse(stringFinishedAt);
                                            orders.setFinishedAt(finishedAt);

                                            sagaState.setCurrentStep("TravelInsuranceService");

                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "ABORTED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("ABORTED");
                                        }
                                        //if other status of message
                                        // do ABORTING

                                        orderRepository.save(orders);
                                        sagaStateRepository.save(sagaState);
                                    }

                                    if (type.equals("updateTravelInsuranceBIC")) {
                                        OrdersServiceEntity orders = orderRepository.findByOrderId(orderId);
                                        SagaState sagaState = sagaStateRepository.findByOrderId(orderId);

                                        //insert to outbox
                                        if (status.equals("success")) {
                                            orders.setState("Success");

                                            sagaState.setCurrentStep("TravelInsuranceService");
                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "SUCCEEDED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("SUCCEEDED");
                                        } else {
                                            orders.setState("UpdateAborted");

                                            sagaState.setCurrentStep("TravelInsuranceService");
                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "ABORTED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("ABORTED");
                                        }
                                        //if other status of message
                                        // do ABORTING

                                        orderRepository.save(orders);
                                        sagaStateRepository.save(sagaState);
                                    }

                                    if (type.equals("getTravelInsuranceBIC")) {
                                        OrdersServiceEntity orders = orderRepository.findByOrderId(orderId);
                                        SagaState sagaState = sagaStateRepository.findByOrderId(orderId);

                                        //insert to outbox
                                        if (status.equals("success")) {
                                            orders.setState("Success");
                                            orders.setPayloadGet(payload);

                                            sagaState.setCurrentStep("TravelInsuranceService");

                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "SUCCEEDED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("SUCCEEDED");
                                        } else {
                                            orders.setState("GetAborted");

                                            sagaState.setCurrentStep("TravelInsuranceService");

                                            JSONObject s = new JSONObject();
                                            s.put("TravelInsuranceService", "ABORTED");
                                            sagaState.setStepState(s.toString());

                                            sagaState.setType(type);
                                            sagaState.setStatus("ABORTED");
                                        }
                                        //if other status of message
                                        // do ABORTING

                                        orderRepository.save(orders);
                                        sagaStateRepository.save(sagaState);
                                    }
                                }
                            }
                            if (op.equals("r")) {
                                //countReadOutBox ++;

                            }

                        } else {
                            System.out.println("afterObj is null");
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                } else {
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicTravelInsuranceOutbox,
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicTravelInsuranceOutbox,
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicTravelInsuranceOutbox,
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        } finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }



//    @KafkaListener(id = "${kafka.groupID.users}",topics = "${kafka.topic.users}")
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

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                countReadUser=0;
                                dataBaseUserServiceImp.createDatabaseUser(keyPairs);
                            }
                            if (op.equals("u")) {
                                countReadUser=0;
                                dataBaseUserServiceImp.updateDatabaseUser(keyPairs);
                            }
                            if (op.equals("r")) {
                                // truncate all table
                                // create table from key-value
                                //check timeout (if timeout>= n --> read consecutively --> reset countReadUser=0 ?
                                countReadUser ++;
                                dataBaseUserServiceImp.readAndUpdateDatabaseUser(keyPairs,countReadUser);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                countReadUser=0;
                                dataBaseUserServiceImp.deleteDatabaseUser(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            countReadUser=0;
                            dataBaseUserServiceImp.truncateDatabaseUser();
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
            logService.createKafkaLogException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    //    @KafkaListener(id = "${kafka.groupID.clients}",topics = "${kafka.topic.clients}")
    public void listenClient(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
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

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                countReadClient=0;
                                dataBaseClientServiceImp.createDatabaseClient(keyPairs);
                            }
                            if (op.equals("u")) {
                                countReadClient=0;
                                dataBaseClientServiceImp.updateDatabaseClient(keyPairs);
                            }
                            if (op.equals("r")) {
                                countReadClient ++;
                                dataBaseClientServiceImp.readAndUpdateDatabaseClient(keyPairs,countReadClient);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                countReadClient=0;
                                dataBaseClientServiceImp.deleteDatabaseClient(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            countReadClient=0;
                            dataBaseClientServiceImp.truncateDatabaseClient();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
                    "clients", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
                    "clients", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        }catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
                    "clients", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


    //    @KafkaListener(id = "${kafka.groupID.consumers}",topics = "${kafka.topic.consumers}")
    public void listenConsumer(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
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

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                countReadConsumer=0;
                                dataBaseConsumerServiceImp.createDatabaseConsumer(keyPairs);
                            }
                            if (op.equals("u")) {
                                countReadConsumer=0;
                                dataBaseConsumerServiceImp.updateDatabaseConsumer(keyPairs);
                            }
                            if (op.equals("r")) {
                                countReadConsumer ++;
                                dataBaseConsumerServiceImp.readAndUpdateDatabaseConsumer(keyPairs,countReadConsumer);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                countReadConsumer=0;
                                dataBaseConsumerServiceImp.deleteDatabaseConsumer(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            countReadConsumer=0;
                            dataBaseConsumerServiceImp.truncateDatabaseConsumer();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
                    "consumers", op , dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
                    "consumers", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
                    "consumers", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }
}
