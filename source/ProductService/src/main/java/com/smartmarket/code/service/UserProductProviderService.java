package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface UserProductProviderService {
    public void createUserProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void updateUserProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void deleteUserProductProvider(Map<String, Object> keyPairs);
    public void truncateUserProductProvider();
}
