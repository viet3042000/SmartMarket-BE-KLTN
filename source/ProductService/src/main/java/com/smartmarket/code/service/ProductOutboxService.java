package com.smartmarket.code.service;

public interface ProductOutboxService {
    public void processMessageFromOrderOutbox(String op,String aggregateId,String type,
                                              String payload) throws Exception;
}
