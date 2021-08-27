package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseUserRoleService {
    public void createDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseUserRole(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseUserRole(Map<String, Object> keyPairs);
    public void truncateDatabaseUserRole();
}
