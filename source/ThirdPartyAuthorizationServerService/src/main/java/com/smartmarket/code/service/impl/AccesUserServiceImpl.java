package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.AccessUserRepository;
import com.smartmarket.code.model.AccessUser;
import com.smartmarket.code.service.AccesUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccesUserServiceImpl implements AccesUserService {

    @Autowired
    AccessUserRepository accessUserRepository ;

    @Override
    public Set<AccessUser> findAccessUserByUserIdAndUserUrlId(Long userIdToken, Long urlId) {
        return accessUserRepository.findAccessUserByUserIdAndUserUrlId(userIdToken,urlId);
    }

    @Override
    public Set<AccessUser> checkAccessUser(Long userIdToken, Long urlId, Long userIdAccess) {
        return accessUserRepository.checkAccessUser(userIdToken,urlId,userIdAccess);

    }

}
