package com.smartmarket.code.service;

import com.smartmarket.code.model.UserRole;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public interface UserRoleKafkaService {
    public UserRole createUserRoleKafka(Map<String, Object> keyPairs) throws ParseException;

    public int updateUserRoleKafka(Long userId, Long roleId, Long enabled,Long id);

    public int deleteUserRoleKafka(Long userId) ;

    public int truncateUserRoleKafka() ;
}
