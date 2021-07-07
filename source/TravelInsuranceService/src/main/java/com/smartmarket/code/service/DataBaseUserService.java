package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseUserService {
    public void createDatabaseUser(String table, Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseUser(String table, Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseUser(String table, Map<String, Object> keyPairs);
    public void truncateDatabaseUser(String table);

}
