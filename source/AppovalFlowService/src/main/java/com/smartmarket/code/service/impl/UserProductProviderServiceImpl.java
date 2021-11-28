package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProductProviderRepository;
import com.smartmarket.code.model.UserProductProvider;
import com.smartmarket.code.service.UserProductProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserProductProviderServiceImpl implements UserProductProviderService {

    @Autowired
    UserProductProviderRepository userProductProviderRepository;


    public void createUserProductProvider(Map<String, Object> keyPairs) throws ParseException {
        UserProductProvider userProductProvider = new UserProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userProductProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_id")) {
                userProductProvider.setProductProviderId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("user_name")) {
                userProductProvider.setUserName((String) keyPairs.get(k));
            }
        }
        userProductProviderRepository.save(userProductProvider);
    }

    public void updateUserProductProvider(Map<String, Object> keyPairs) throws ParseException{
        UserProductProvider userProductProvider = new UserProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userProductProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_id")) {
                userProductProvider.setProductProviderId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("user_name")) {
                userProductProvider.setUserName((String) keyPairs.get(k));
            }
        }
        userProductProviderRepository.save(userProductProvider);
    }

    public void deleteUserProductProvider(Map<String, Object> keyPairs){
        String userName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("user_name")) {
                userName = (String) keyPairs.get(k);
            }
        }
        userProductProviderRepository.deleteUserProductProviderKafka(userName);
    }

    public void truncateUserProductProvider(){
        userProductProviderRepository.truncateUserProductProviderKafka();
    }
}
