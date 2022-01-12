package com.smartmarket.code.service;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.json.JSONException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

public interface ListenerService {
    public void listenOrderServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception;

//    public void listenJobManagementServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception;

    public void listenUser(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException ;

    public void listenUserRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException ;

//    public void listenRole(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws JSONException ;
}
