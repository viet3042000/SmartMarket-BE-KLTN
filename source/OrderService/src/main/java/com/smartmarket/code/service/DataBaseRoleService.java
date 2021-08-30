package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseRoleService {
    public void createDatabaseRole(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseRole(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseRole(Map<String, Object> keyPairs);
    public void truncateDatabaseRole();
}
