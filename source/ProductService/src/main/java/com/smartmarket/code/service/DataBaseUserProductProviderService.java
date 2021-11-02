package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseUserProductProviderService {
    public void createDatabaseUserProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseUserProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseUserProductProvider(Map<String, Object> keyPairs);
    public void truncateDatabaseUserProductProvider();
}
