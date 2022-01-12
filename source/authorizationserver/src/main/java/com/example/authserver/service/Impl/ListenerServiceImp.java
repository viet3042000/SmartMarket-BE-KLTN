package com.example.authserver.service.Impl;

import com.example.authserver.constants.ResponseCode;
import com.example.authserver.exception.ListenerExceptionObject;
import com.example.authserver.service.UserRoleService;
import com.example.authserver.service.UserService;
import com.example.authserver.service.ListenerService;
import com.example.authserver.utils.GetKeyPairUtil;
import com.google.common.base.Throwables;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Value("${kafka.topic.users}")
    String topicUsers;

    @Value("${kafka.topic.user_role}")
    String topicUserRole;

    @Value("${kafka.topic.roles}")
    String topicRole;


//    @KafkaListener(id = "${kafka.groupID.users}",topics = "${kafka.topic.users}")
    public void listenUser(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op = "";
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

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                userService.createUser(keyPairs);
                            }
                            if (op.equals("u")) {
                                userService.updateUser(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                userService.deleteUser(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            userService.truncateUser();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op, dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUsers,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        } finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


//    @KafkaListener(id = "${kafka.groupID.user_role}",topics = "${kafka.topic.user_role}")
    public void listenUserRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        String op = "";
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

                        Map<String, Object> keyPairs = new HashMap<>();
                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                userRoleService.createUserRole(keyPairs);
                            }
                            if (op.equals("u")) {
                                userRoleService.updateUserRole(keyPairs);
                            }
                        } else {
                            System.out.println("afterObj is null");
                        }

                        if (!payloadObj.isNull("before")) {
                            JSONObject beforeObj = payloadObj.getJSONObject("before");

                            //Get key-pair in afterObj
                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);

                            if (op.equals("d")) {
                                userRoleService.deleteUserRole(keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            userRoleService.truncateUserRole();
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicUserRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        } finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }


////    @KafkaListener(id = "${kafka.groupID.roles}",topics = "${kafka.topic.roles}")
//    public void listenRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
//        String op = "";
//        try {
//            for (ConsumerRecord<String, String> record : records) {
//                System.out.println(record.offset());
//                if (record.value() != null) {
//                    String valueRecord = record.value();
//                    JSONObject valueObj = new JSONObject(valueRecord);
//                    if (!valueObj.isNull("payload")) {
//                        JSONObject payloadObj = valueObj.getJSONObject("payload");
//                        JSONObject sourceObj = payloadObj.getJSONObject("source");
//                        op = payloadObj.getString("op");
//
//                        Map<String, Object> keyPairs = new HashMap<>();
//                        if (!payloadObj.isNull("after")) {
//                            JSONObject afterObj = payloadObj.getJSONObject("after");
//
//                            //Get key-pair in afterObj
//                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);
//
//                            if (op.equals("c")) {
//                                roleService.createRole(keyPairs);
//                            }
//                            if (op.equals("u")) {
//                                roleService.updateRole(keyPairs);
//                            }
//                        } else {
//                            System.out.println("afterObj is null");
//                        }
//
//                        if (!payloadObj.isNull("before")) {
//                            JSONObject beforeObj = payloadObj.getJSONObject("before");
//
//                            //Get key-pair in afterObj
//                            getKeyPairUtil.getKeyPair(beforeObj, keyPairs);
//
//                            if (op.equals("d")) {
//                                roleService.deleteRole(keyPairs);
//                            }
//                        } else {
//                            System.out.println("beforeObj is null");
//                        }
//
//                        if (op.equals("t")) {
//                            roleService.truncateRole();
//                        }
//
//                    } else {
//                        System.out.println("payload is null");
//                    }
//                } else {
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
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
//                    "users", op, dateTimeFormatter.format(currentTime),
//                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createListenerLogExceptionException(kafkaExceptionObject);
//
//        } catch (KafkaException ex) {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
//                    "users", op, dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
//            logService.createListenerLogExceptionException(kafkaExceptionObject);
//
//        } catch (Exception ex) {
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime currentTime = LocalDateTime.now();
//            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
//                    "users", op, dateTimeFormatter.format(currentTime),
//                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
//            logService.createListenerLogExceptionException(kafkaExceptionObject);
//        } finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
////            System.out.println("Closed consumer and we are done");
//        }
//    }
}
