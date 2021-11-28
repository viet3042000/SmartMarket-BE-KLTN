package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface UserRoleService {
    public void createUserRole(Map<String, Object> keyPairs) throws ParseException;
    public void updateUserRole(Map<String, Object> keyPairs) throws ParseException;
    public void deleteUserRole(Map<String, Object> keyPairs);
    public void truncateUserRole();
}
