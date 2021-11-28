package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface UserService {
    public void createUser(Map<String, Object> keyPairs) throws ParseException;
    public void updateUser(Map<String, Object> keyPairs) throws ParseException;
    public void deleteUser(Map<String, Object> keyPairs);
    public void truncateUser();
}
