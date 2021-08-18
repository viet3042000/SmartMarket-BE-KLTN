package com.smartmarket.code.service;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

public interface ListenerService {
    public void listenPendingBicTransaction(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception;

    public void listenTravelServiceOutbox(@Payload(required = false) ConsumerRecords<String, String> records, Acknowledgment acknowledgment) throws Exception;
}
