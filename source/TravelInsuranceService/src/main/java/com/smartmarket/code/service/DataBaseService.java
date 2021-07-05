package com.smartmarket.code.service;

import java.util.Map;

public interface DataBaseService {
    public void createDatabase(String table, Map<String, Object> keyPairs);
    public void updateDatabase(String table, Map<String, Object> keyPairs);
    public void deleteDatabase(String table, Map<String, Object> keyPairs);
    public void truncateDatabase(String table);
}
