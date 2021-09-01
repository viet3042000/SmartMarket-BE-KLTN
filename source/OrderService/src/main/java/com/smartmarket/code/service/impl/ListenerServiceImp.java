package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.OrderProductRepository;
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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
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
    DataBaseUserProfileService dataBaseUserProfileService;

    @Autowired
    DataBaseClientService dataBaseClientService;

    @Autowired
    DataBaseConsumerService dataBaseConsumerService;

    @Autowired
    DataBaseRoleService dataBaseRoleService;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Value("${kafka.topic.travelinsuranceoutbox}")
    String topicTravelInsuranceOutbox;

    @Value("${kafka.topic.users}")
    String topicUsers;

    @Value("${kafka.topic.user_role}")
    String topicUserRole;

    @Value("${kafka.topic.roles}")
    String topicRole;

    @Value("${kafka.topic.user_profile}")
    String topicUserProfile;

//    @Value("${kafka.topic.clients}")
//    String topicClients;
//
//    @Value("${kafka.topic.consumers}")
//    String topicConsumers;


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
        //=order ref
        String aggregateId = "";
        String aggregateType = "";
        String type = "";
        String payload = "";

        String requestId ="";
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
                                JSONObject j = new JSONObject(payload);
                                requestId = j.getString("requestId");
                                status = j.getString("status");

                                if (op.equals("c")) {
                                    travelInsuranceOutboxService.processMessageFromTravelOutbox(j,requestId,status,aggregateId,type);

                                    //optimize_start_point
//                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                                    Date date = new Date();
//                                    String stringFinishedAt = formatter.format(date);
//                                    Date finishedAt = formatter.parse(stringFinishedAt);
//                                    if (type.equals("createTravelInsuranceBIC")) {
//                                        orderReference = j.getString("OrderReference");
//
//                                        Optional<OrdersServiceEntity> orders = orderRepository.findByOrderId(aggregateId);
//                                        Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);
//                                        OrderProduct orderProduct = new OrderProduct();
//                                        if (orders.isPresent() && sagaState.isPresent()) {
//                                            OrdersServiceEntity order = orders.get();
//                                            order.setFinishedLogtimestamp(finishedAt);
//
//                                            SagaState st = sagaState.get();
//                                            st.setType(type);
//                                            st.setFinishedLogtimestamp(finishedAt);
//                                            st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
//                                            JSONObject s = new JSONObject();
//                                            //insert to outbox
//                                            if (status.equals("success")) {
//                                                order.setState(OrderEntityState.SUCCEEDED);
//
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.SUCCEEDED);
//
//                                                orderProduct.setOrderId(aggregateId);
//                                                orderProduct.setProductId(orderReference);
//                                                orderProduct.setProductName("TravelInsuranceBIC");
//                                                orderProduct.setState("Succeeded");
//                                                orderProduct.setFinishedLogtimestamp(finishedAt);
//                                            } else {
//                                                order.setState(OrderEntityState.ABORTED);
//
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.ABORTED);
//
//                                                orderProduct.setOrderId(aggregateId);
//                                                orderProduct.setProductId(orderReference);
//                                                orderProduct.setProductName("TravelInsuranceBIC");
//                                                orderProduct.setState("Aborted");
//                                                orderProduct.setFinishedLogtimestamp(finishedAt);
//                                            }
//                                            orderProductRepository.save(orderProduct);
//                                            orderRepository.save(order);
//                                            sagaStateRepository.save(st);
//                                        }
//                                    }
//
//                                    if (type.equals("updateTravelInsuranceBIC")) {
//                                        Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);
//
//                                        if (sagaState.isPresent()) {
//                                            SagaState st = sagaState.get();
//                                            st.setFinishedLogtimestamp(finishedAt);
//                                            st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
//                                            st.setType(type);
//                                            JSONObject s = new JSONObject();
//                                            //insert to outbox
//                                            if (status.equals("success")) {
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.SUCCEEDED);
//                                            } else {
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.ABORTED);
//                                            }
//                                            sagaStateRepository.save(st);
//                                        }
//                                    }
//
//                                    if (type.equals("getTravelInsuranceBIC")) {
//                                        Optional<SagaState> sagaState = sagaStateRepository.findById(requestId);
//
//                                        if (sagaState.isPresent()) {
//                                            SagaState st = sagaState.get();
//                                            st.setFinishedLogtimestamp(finishedAt);
//                                            st.setType(type);
//                                            st.setCurrentStep(AggregateType.TRAVEL_INSURANCE);
//                                            JSONObject s = new JSONObject();
//                                            //insert to outbox
//                                            if (status.equals("success")) {
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.SUCCEEDED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.SUCCEEDED);
//                                            } else {
//                                                s.put(AggregateType.TRAVEL_INSURANCE, SagaStateStepState.ABORTED);
//                                                st.setStepState(s.toString());
//                                                st.setStatus(SagaStateStatus.ABORTED);
//                                            }
//                                            sagaStateRepository.save(st);
//                                        }
//                                    }
                                    //optimize_end_point
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
