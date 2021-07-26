package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseConsumerService {
    public void createDatabaseConsumer( Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseConsumer(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseConsumer(Map<String, Object> keyPairs);
    public void truncateDatabaseConsumer();
    public void readAndUpdateDatabaseConsumer(Map<String, Object> keyPairs, int count) throws ParseException;
}
