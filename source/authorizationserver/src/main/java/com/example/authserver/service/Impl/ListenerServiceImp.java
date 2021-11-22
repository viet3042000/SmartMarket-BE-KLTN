package com.example.authserver.service.Impl;

import com.example.authserver.constants.ResponseCode;
import com.example.authserver.exception.ListenerExceptionObject;
import com.example.authserver.service.DataBaseRoleService;
import com.example.authserver.service.DataBaseUserRoleService;
import com.example.authserver.service.DataBaseUserService;
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
    DataBaseUserService dataBaseUserService;

    @Autowired
    DataBaseUserRoleService dataBaseUserRoleService;

    @Autowired
    DataBaseRoleService dataBaseRoleService;

//    @Value("${kafka.topic.users}")
//    String topicUsers ;
    String topicUsers ="userservice.public.users";

//    @Value("${kafka.topic.user_role}")
    String topicUserRole ="userservice.public.user_role";

//    @Value("${kafka.topic.roles}")
    String topicRole = "userservice.public.roles";

//    @Value("${kafka.topic.clients}")
//    String topicClients;
//
//    @Value("${kafka.topic.consumers}")
//    String topicConsumers;


//    @KafkaListener(id = "group_id_author_server_users",topics = "userservice.public.users")
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
//    @KafkaListener(id = "group_id_author_server_user_role",topics = "userservice.public.user_role")
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


//    @KafkaListener(id = "${kafka.groupID.roles}", topics = "${kafka.topic.roles}")
//    @KafkaListener(id = "group_id_author_server_roles", topics = "userservice.public.roles")
    public void listenRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
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
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);

        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject kafkaExceptionObject = new ListenerExceptionObject(topicRole,
                    "users", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(kafkaExceptionObject);
        } finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }
}
