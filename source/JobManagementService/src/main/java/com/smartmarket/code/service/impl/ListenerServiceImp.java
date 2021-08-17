package com.smartmarket.code.service.impl;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.smartmarket.code.constants.ResponseCode;
import com.smartmarket.code.dao.PendingBICTransactionRepository;
import com.smartmarket.code.model.PendingBICTransaction;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.service.ListenerService;
import com.smartmarket.code.util.GetKeyPairUtil;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
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
import java.util.HashMap;
import java.util.Map;

@Component
public class ListenerServiceImp implements ListenerService {

    @Autowired
    GetKeyPairUtil getKeyPairUtil;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    TravelInsuranceServiceImpl travelInsuranceService;

    @Autowired
    PendingBICTransactionRepository pendingBICTransactionRepository;

    @Value("${kafka.topic.travelinsurance.pendingBictransaction}")
    String topicTravelInsurancePendingBICTransaction;


    @KafkaListener(id = "${kafka.groupID.travelinsurance.pendingBictransaction}",topics = "${kafka.topic.travelinsurance.pendingBictransaction}")
    public void listenPendingBicTransaction(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception {
        String op ="";

        String orderId ="";
        String orderReference ="";
        Long id = 0L;
        String requestId = "";
        Long fromOrderService = 0L;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        PendingBICTransaction pendingBICTransaction = new PendingBICTransaction();
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
                                    if (k.equals("from_order_service")) {
                                        fromOrderService = ((Number)keyPairs.get(k)).longValue();
                                    }
                                }

                                //insert to pending
                                if(!requestId.equals("Format not True")) {
                                    pendingBICTransaction.setId(id);
                                    pendingBICTransaction.setRequestId(requestId);
                                    pendingBICTransaction.setOrderId(orderId);
                                    pendingBICTransaction.setOrderReference(orderReference);
                                    pendingBICTransaction.setFromOrderService(fromOrderService);

                                    pendingBICTransactionRepository.save(pendingBICTransaction);
                                }

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
                                for (String k : keyPairs.keySet()) {
                                    if (k.equals("id")) {
                                        id = ((Number)keyPairs.get(k)).longValue();
                                    }
                                }

                                //delete pending by id
                                pendingBICTransactionRepository.deletePendingBICTransactionByID(id);
                            }
                        } else {
                            System.out.println("beforeObj is null");
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
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicTravelInsurancePendingBICTransaction,
                    "outbox", op ,dateTimeFormatter.format(currentTime),
                    "Can not commit offset", ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (KafkaException ex){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicTravelInsurancePendingBICTransaction,
                    "outbox", op , dateTimeFormatter.format(currentTime),
                    ResponseCode.MSG.INVALID_TRANSACTION_MSG, ResponseCode.CODE.INVALID_TRANSACTION, Throwables.getStackTraceAsString(ex));
            logService.createListenerLogExceptionException(listenerExceptionObject);

        }catch (Exception ex) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            ListenerExceptionObject listenerExceptionObject = new ListenerExceptionObject(topicTravelInsurancePendingBICTransaction,
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