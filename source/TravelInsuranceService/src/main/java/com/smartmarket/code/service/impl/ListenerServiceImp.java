package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.ListenerService;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    DataBaseClientServiceImp dataBaseClientServiceImp;

    @Autowired
    DataBaseUserServiceImp dataBaseUserServiceImp;

    @Autowired
    DataBaseConsumerServiceImp dataBaseConsumerServiceImp;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    // can designate multi topic , partition , concurrency,...
    //Each function is marked by @KafkaListener = 1 consumer
    @KafkaListener(id = "group_id_clients",topics = "${kafka.topic.clients}")
    public void listenClient(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        String table = sourceObj.getString("table");
                        String op = payloadObj.getString("op");

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
//                                dataBaseServiceImp.createDatabase(table, keyPairs);
                                dataBaseClientServiceImp.createDatabaseClient(table,keyPairs);
                            }
                            if (op.equals("u")) {
//                                dataBaseServiceImp.updateDatabase(table, keyPairs);
                                dataBaseClientServiceImp.updateDatabaseClient(table,keyPairs);
                            }
                            if (op.equals("r")) {
                                //... by key-value in beforeObj to TravelInsurance DB that match with key-pair in sourceObj
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
//                                dataBaseServiceImp.deleteDatabase(table, keyPairs);
                                dataBaseClientServiceImp.deleteDatabaseClient(table,keyPairs);
                            }
                        } else {
//                        System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
//                            dataBaseServiceImp.truncateDatabase(table);
                            dataBaseClientServiceImp.truncateDatabaseClient(table);
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
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá ,
            // nên coordinator tưởng là consumer chết rồi , nên không commit được
            ex.getMessage();
        }
        catch (JSONException ex) {
            ex.getMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
//        }
    }


    @KafkaListener(id = "group_id_users",topics = "${kafka.topic.users}")
    public void listenUser(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        String table = sourceObj.getString("table");
                        String op = payloadObj.getString("op");

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
//                                dataBaseServiceImp.createDatabase(table, keyPairs);
                                dataBaseUserServiceImp.createDatabaseUser(table,keyPairs);
                            }
                            if (op.equals("u")) {
//                                dataBaseServiceImp.updateDatabase(table, keyPairs);
                                dataBaseUserServiceImp.updateDatabaseUser(table,keyPairs);
                            }
                            if (op.equals("r")) {
                                //... by key-value in beforeObj to TravelInsurance DB that match with key-pair in sourceObj
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
//                                dataBaseServiceImp.deleteDatabase(table, keyPairs);
                                dataBaseUserServiceImp.deleteDatabaseUser(table,keyPairs);
                            }
                        } else {
//                        System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
//                            dataBaseServiceImp.truncateDatabase(table);
                            dataBaseUserServiceImp.truncateDatabaseUser(table);
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
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá ,
            // nên coordinator tưởng là consumer chết rồi , nên không commit được
            ex.getMessage();
        }
        catch (JSONException ex) {
            ex.getMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
//        }
    }


    @KafkaListener(id = "group_id_consumers",topics = "${kafka.topic.consumers}")
    public void listenConsumer(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException {
        try {
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.offset());
                if(record.value() != null) {
                    String valueRecord = record.value();
                    JSONObject valueObj = new JSONObject(valueRecord);
                    if (!valueObj.isNull("payload")) {
                        JSONObject payloadObj = valueObj.getJSONObject("payload");
                        JSONObject sourceObj = payloadObj.getJSONObject("source");
                        String table = sourceObj.getString("table");
                        String op = payloadObj.getString("op");

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
//                                dataBaseServiceImp.createDatabase(table, keyPairs);
                                dataBaseConsumerServiceImp.createDatabaseConsumer(table,keyPairs);
                            }
                            if (op.equals("u")) {
//                                dataBaseServiceImp.updateDatabase(table, keyPairs);
                                dataBaseConsumerServiceImp.updateDatabaseConsumer(table,keyPairs);
                            }
                            if (op.equals("r")) {
                                //... by key-value in beforeObj to TravelInsurance DB that match with key-pair in sourceObj
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
//                                dataBaseServiceImp.deleteDatabase(table, keyPairs);
                                dataBaseConsumerServiceImp.deleteDatabaseConsumer(table,keyPairs);
                            }
                        } else {
//                        System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
//                            dataBaseServiceImp.truncateDatabase(table);
                            dataBaseConsumerServiceImp.truncateDatabaseConsumer(table);
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
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá ,
            // nên coordinator tưởng là consumer chết rồi , nên không commit được
            ex.getMessage();
        }
        catch (JSONException ex) {
            ex.getMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        finally {
////          In the case of an error, we want to make sure that we commit before we close and exit.
//            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
//        }
    }
}
