package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseClientService {
    public void createDatabaseClient(String table, Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseClient(String table, Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseClient(String table, Map<String, Object> keyPairs);
    public void truncateDatabaseClient(String table);
}
