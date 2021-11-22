package com.smartmarket.code.service;

import org.json.JSONObject;

public interface OrderOutboxService {
    public void processMessageFromOrderOutbox(String op,String aggregateId,String type,
                                              JSONObject jsonPayload) throws Exception;
}
