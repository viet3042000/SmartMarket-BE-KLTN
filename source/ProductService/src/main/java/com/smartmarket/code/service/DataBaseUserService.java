package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseUserService {
    public void createDatabaseUser(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseUser( Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseUser(Map<String, Object> keyPairs);
    public void truncateDatabaseUser();
    public void readAndUpdateDatabaseUser(Map<String, Object> keyPairs, int countReadUser);

}
