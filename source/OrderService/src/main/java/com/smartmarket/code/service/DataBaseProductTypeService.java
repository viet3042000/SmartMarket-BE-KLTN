package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseProductTypeService {

    public void createDatabaseProductType(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseProductType(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseProductType(Map<String, Object> keyPairs);
    public void truncateDatabaseProductType();
}
