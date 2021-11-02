package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseProductProviderService {

    public void createDatabaseProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseProductProvider(Map<String, Object> keyPairs);
    public void truncateDatabaseProductProvider();
}
