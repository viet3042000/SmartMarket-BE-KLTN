package com.smartmarket.code;

import com.smartmarket.code.service.impl.DataBaseServiceImp;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main implements ApplicationRunner{

    @Autowired
    DataBaseServiceImp dataBaseServiceImp;

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    ConfigurableEnvironment environment;

    // can desinate multi topic , partition , concurrency,...
    //Each function is marked by @KafkaListener = 1 consumer
    @KafkaListener(topics = "${kafka.topic.users}")
//    @KafkaListener(topics = "postgres.public.users")
    public void listen(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment){
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
//                        Map<String, Number> keyPairsLong = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                dataBaseServiceImp.createDatabase(table, keyPairs);
                            }
                            if (op.equals("u")) {
                                dataBaseServiceImp.updateDatabase(table, keyPairs);
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
                                dataBaseServiceImp.deleteDatabase(table, keyPairs);
                            }
                        } else {
                            System.out.println("beforeObj is null");
                        }

                        if (op.equals("t")) {
                            dataBaseServiceImp.truncateDatabase(table);
                        }

                    } else {
                        System.out.println("payload is null");
                    }
                }
            }

            //Commit after processed record in batch (records)
            acknowledgment.acknowledge();

        }catch (CommitFailedException ex) {
            // Do giữa các lần poll, thời gian xử lý của consumer lâu quá ,
            // nên coordinator tưởng là consumer chết rồi , nên không commit được
            ex.getMessage();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }finally {
//          In the case of an error, we want to make sure that we commit before we close and exit.
            acknowledgment.acknowledge();
//            System.out.println("Closed consumer and we are done");
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
