package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProductProviderRepository;
import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.model.UserProductProvider;
import com.smartmarket.code.service.UserProductProviderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserProductProviderKafkaServiceImpl implements UserProductProviderKafkaService {
    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    public UserProductProvider createUserProductProviderKafka(Map<String, Object> keyPairs) throws ParseException {
        UserProductProvider userProductProvider = new UserProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userProductProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_name")) {
                userProductProvider.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("user_name")) {
                userProductProvider.setUserName((String) keyPairs.get(k));
            }
        }
        return userProductProviderRepository.save(userProductProvider);
    }

    public UserProductProvider updateUserProductProviderKafka(Map<String, Object> keyPairs) throws ParseException{
        UserProductProvider userProductProvider = new UserProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userProductProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_name")) {
                userProductProvider.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("user_name")) {
                userProductProvider.setUserName((String) keyPairs.get(k));
            }
        }
        return userProductProviderRepository.save(userProductProvider);
    }


    public int deleteUserProductProviderKafka(String userName) {
        return userProductProviderRepository.deleteUserProductProviderKafka(userName);
    }

    public int truncateUserProductProviderKafka() {
        return userProductProviderRepository.truncateUserProductProviderKafka();
    }
}
