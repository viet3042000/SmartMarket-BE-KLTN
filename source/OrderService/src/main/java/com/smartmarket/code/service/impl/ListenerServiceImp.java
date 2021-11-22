package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import java.util.Map;


@Service
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    TravelInsuranceOutboxService travelInsuranceOutboxService;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    DataBaseUserService dataBaseUserService;

    @Autowired
    DataBaseUserRoleService dataBaseUserRoleService;

    @Autowired
    DataBaseRoleService dataBaseRoleService;

    @Autowired
    DataBaseProductService dataBaseProductService;

    @Autowired
    DataBaseProductProviderService dataBaseProductProviderService;

    @Value("${kafka.topic.travelinsuranceoutbox}")
    String topicTravelInsuranceOutbox;

    @Value("${kafka.topic.users}")
    String topicUsers;

    @Value("${kafka.topic.user_role}")
    String topicUserRole;

    @Value("${kafka.topic.roles}")
    String topicRole;

    @Value("${kafka.topic.product}")
    String topicProduct;

    @Value("${kafka.topic.product_provider}")
    String topicProductProvider;

//    @Value("${kafka.topic.clients}")
//    String topicClients;
//
//    @Value("${kafka.topic.consumers}")
//    String topicConsumers;


    @KafkaListener(id = "${kafka.groupID.travelinsuranceoutbox}",topics = "${kafka.topic.travelinsuranceoutbox}")
    public void listenOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        String op = "";
        String aggregateId = "";//=order ref
        String aggregateType = "";
        String type = "";
        String payload = "";

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
                            }

                            if ("Order".equals(aggregateType)) {
                                //order id, status = payload.get
                                JSONObject jsonPayload = new JSONObject(payload);
                                if (op.equals("c")) {
                                    travelInsuranceOutboxService.processMessageFromTravelOutbox(jsonPayload,aggregateId,type);
                                }
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
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicTravelInsuranceOutbox,
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicTravelInsuranceOutbox,
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
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


//    @KafkaListener(id = "${kafka.groupID.user_role}",topics = "${kafka.topic.user_role}")
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
                    "user_role", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "user_role", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "user_role", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


//    @KafkaListener(id = "${kafka.groupID.roles}",topics = "${kafka.topic.roles}")
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
                    "roles", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "roles", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "roles", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


//    @KafkaListener(id = "${kafka.groupID.product}",topics = "${kafka.topic.product}")
    public void listenProduct(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
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
                                dataBaseProductService.createDatabaseProduct(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseProductService.updateDatabaseProduct(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseProductService.deleteDatabaseProduct(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseProductService.truncateDatabaseProduct();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProduct,
                    "product", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProduct,
                    "product", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProduct,
                    "product", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }

//    @KafkaListener(id = "${kafka.groupID.product_provider}",topics = "${kafka.topic.product_provider}")
    public void listenProductProvider(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
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
                                dataBaseProductProviderService.createDatabaseProductProvider(keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseProductProviderService.updateDatabaseProductProvider(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                dataBaseProductProviderService.deleteDatabaseProductProvider(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseProductProviderService.truncateDatabaseProductProvider();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProductProvider,
                    "product_type", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProductProvider,
                    "product_type", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicProductProvider,
                    "product_type", op ,  dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        }
        finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }



    //    @KafkaListener(id = "${kafka.groupID.clients}",topics = "${kafka.topic.clients}")
//    public void listenClient(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
//        String op ="";
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
//                            if (op.equals("c")) {
//                                dataBaseClientServiceImp.createDatabaseClient(keyPairs);
//                            }
//                            if (op.equals("u")) {
//                                dataBaseClientServiceImp.updateDatabaseClient(keyPairs);
//                            }
//                            if (op.equals("r")) {
//                                dataBaseClientServiceImp.readAndUpdateDatabaseClient(keyPairs,countReadClient);
//                            }
//                        } else {
//                            System.out.println("afterObj is null");
//                        }
//
//                        if (!payloadObj.isNull("before")) {
//                            JSONObject beforeObj = payloadObj.getJSONObject("before");
//
//                            //Get key-pair in afterObj
//                            Map<String, Object> keyPairs = new HashMap<>();
//                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);
//
//                            if (op.equals("d")) {
//                                dataBaseClientServiceImp.deleteDatabaseClient(keyPairs);
//                            }
//                        } else {
//                            System.out.println("beforeObj is null");
//                        }
//
//                        if (op.equals("t")) {
//                            dataBaseClientServiceImp.truncateDatabaseClient();
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
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
//                    "clients", op ,dateTimeFormatter.format(currentTime),
//                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//
//        }catch (KafkaException ex){
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
//                    "clients", op , dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//        }catch (Exception ex) {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicClients,
//                    "clients", op , dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
////            System.out.println("Closed consumer and we are done");
//        }
//    }
//
//
//    //    @KafkaListener(id = "${kafka.groupID.consumers}",topics = "${kafka.topic.consumers}")
//    public void listenConsumer(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
//        String op ="";
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
//                            if (op.equals("c")) {
//                                dataBaseConsumerServiceImp.createDatabaseConsumer(keyPairs);
//                            }
//                            if (op.equals("u")) {
//                                dataBaseConsumerServiceImp.updateDatabaseConsumer(keyPairs);
//                            }
//                            if (op.equals("r")) {
//                                dataBaseConsumerServiceImp.readAndUpdateDatabaseConsumer(keyPairs,countReadConsumer);
//                            }
//                        } else {
//                            System.out.println("afterObj is null");
//                        }
//
//                        if (!payloadObj.isNull("before")) {
//                            JSONObject beforeObj = payloadObj.getJSONObject("before");
//
//                            //Get key-pair in afterObj
//                            Map<String, Object> keyPairs = new HashMap<>();
//                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);
//
//                            if (op.equals("d")) {
//                                dataBaseConsumerServiceImp.deleteDatabaseConsumer(keyPairs);
//                            }
//                        } else {
//                            System.out.println("beforeObj is null");
//                        }
//
//                        if (op.equals("t")) {
//                            dataBaseConsumerServiceImp.truncateDatabaseConsumer();
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
//        }catch (CommitFailedException ex) {
//            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá,
//            // nên coordinator tưởng là consumer chết rồi-->Không commit được
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
//                    "consumers", op , dateTimeFormatter.format(currentTime),
//                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//
//        }catch (KafkaException ex){
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
//                    "consumers", op , dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//
//        } catch (Exception ex) {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicConsumers,
//                    "consumers", op , dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
//            logService.createKafkaLogException(kafkaExceptionObject);
//        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
////            System.out.println("Closed consumer and we are done");
//        }
//    }
}
