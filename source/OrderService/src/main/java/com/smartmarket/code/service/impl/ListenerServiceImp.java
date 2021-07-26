package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.OrderRepository;
import com.smartmarket.code.model.OrdersServiceEntity;
import com.smartmarket.code.model.OutBox;
import com.smartmarket.code.model.entitylog.KafkaExceptionObject;
import com.smartmarket.code.response.BaseResponse;
import com.smartmarket.code.service.ListenerService;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    LogServiceImpl logService;

    int countReadOutBox =0;


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


    @KafkaListener(id = "kafka.groupID.travelinsurance.outbox",topics = "travelinsurance.public.outbox")
    public void listenOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
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

                        if (!payloadObj.isNull("after")) {
                            JSONObject afterObj = payloadObj.getJSONObject("after");

                            //Get key-pair in afterObj
                            Map<String, Object> keyPairs = new HashMap<>();
                            getKeyPairUtil.getKeyPair(afterObj, keyPairs);

                            if (op.equals("c")) {
                                countReadOutBox = 0;
                                Long id = 0L;
                                String aggregateId = "";
                                String aggregateType = "";
                                String type = "";
                                String payload = "";
                                String status = "";

                                for (String k : keyPairs.keySet()) {
                                    if (k.equals("id")) {
                                        id = ((Number) keyPairs.get(k)).longValue();
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

                                if (type.equals("createTravelInsuranceBIC")) {
                                    JSONObject jsonPayload = new JSONObject(payload);
                                    JSONObject body = jsonPayload.getJSONObject("body");
                                    String b = body.toString();

                                    OrdersServiceEntity orders = orderRepository.findById(id);

                                    //insert to outbox
                                    if (status.equals("success")) {
                                        orders.setState("Success");
                                    } else {
                                        orders.setState("Aborted");
                                    }
                                    orderRepository.save(orders);
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
            KafkaExceptionObject kafkaExceptionObject = new KafkaExceptionObject("travelinsurance.public.outbox",
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);

        } catch (KafkaException ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            KafkaExceptionObject kafkaExceptionObject = new KafkaExceptionObject("travelinsurance.public.outbox",
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        } catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            KafkaExceptionObject kafkaExceptionObject = new KafkaExceptionObject("travelinsurance.public.outbox",
                    "outbox", op, dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.GENERAL_ERROR_MSG, ResponseCode.CODE.GENERAL_ERROR, Throwables.getStackTraceAsString(ex));
            logService.createKafkaLogException(kafkaExceptionObject);
        } finally {
//          In the case of an error, we want to make sure that we commit before we leave.
            acknowledgment.acknowledge();
            System.out.println("Closed consumer and we are done");
        }
    }
}
