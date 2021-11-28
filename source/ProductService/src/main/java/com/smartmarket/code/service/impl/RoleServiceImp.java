package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public void createRole(Map<String, Object> keyPairs) throws ParseException {
        Role role = new Role();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                role.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("role_name")) {
                role.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                role.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                role.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                role.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        roleRepository.save(role);
    }

    public void updateRole(Map<String, Object> keyPairs) throws ParseException{
        Role role = new Role();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                role.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("role_name")) {
                role.setRoleName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                role.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                role.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                role.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        roleRepository.save(role);
    }

    public void deleteRole(Map<String, Object> keyPairs){
        String roleName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("role_name")) {
                roleName = (String) keyPairs.get(k);
            }
        }
        roleRepository.deleteRoleKafka(roleName);
    }

    public void truncateRole(){
        roleRepository.truncateRoleKafka();
    }
}
