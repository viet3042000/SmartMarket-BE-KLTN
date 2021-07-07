package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseConsumerService {
    public void createDatabaseConsumer(String table, Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseConsumer(String table, Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseConsumer(String table, Map<String, Object> keyPairs);
    public void truncateDatabaseConsumer(String table);
}
