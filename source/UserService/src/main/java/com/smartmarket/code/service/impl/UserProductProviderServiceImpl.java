package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProductProviderRepository;
import com.smartmarket.code.model.UserProductProvider;
import com.smartmarket.code.service.UserProductProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserProductProviderServiceImpl implements UserProductProviderService {

    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    public UserProductProvider create(String userName, String productProviderName){
        UserProductProvider userProductProvider=  new UserProductProvider();
        userProductProvider.setUserName(userName);
        userProductProvider.setProductProviderName(productProviderName);
        return userProductProviderRepository.save(userProductProvider);
    }

}
