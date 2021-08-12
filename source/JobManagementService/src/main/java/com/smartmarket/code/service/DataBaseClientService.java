package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseClientService {
    public void createDatabaseClient( Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseClient( Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseClient(Map<String, Object> keyPairs);
    public void truncateDatabaseClient();
    public void readAndUpdateDatabaseClient(Map<String, Object> keyPairs, int count) ;
}
