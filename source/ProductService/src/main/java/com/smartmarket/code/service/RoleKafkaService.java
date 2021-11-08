package com.smartmarket.code.service;

import com.smartmarket.code.model.Role;

import java.text.ParseException;
import java.util.Map;

public interface RoleKafkaService {
    public Role createRoleKafka(Map<String, Object> keyPairs) throws ParseException;

    public Role updateRoleKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteRoleKafka(String roleName) ;

    public int truncateRoleKafka() ;
}