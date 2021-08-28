package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface DataBaseUserProfileService {
    public void createDatabaseUserProfile(Map<String, Object> keyPairs) throws ParseException;
    public void updateDatabaseUserProfile(Map<String, Object> keyPairs) throws ParseException;
    public void deleteDatabaseUserProfile(Map<String, Object> keyPairs);
    public void truncateDatabaseUserProfile();
}
