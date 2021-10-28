package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseProductService {
    public void createDatabaseProduct(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseProduct(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseProduct(Map<String, Object> keyPairs);
    public void truncateDatabaseProduct();
}
