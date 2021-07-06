package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseService {
    public void createDatabase(String table, Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabase(String table, Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabase(String table, Map<String, Object> keyPairs);
    public void truncateDatabase(String table);
}
