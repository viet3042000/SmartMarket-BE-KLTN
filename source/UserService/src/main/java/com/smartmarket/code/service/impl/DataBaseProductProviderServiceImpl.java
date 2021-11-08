package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProductProviderRepository;
import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.dao.UserRepository;
import com.smartmarket.code.dao.UserRoleRepository;
import com.smartmarket.code.model.UserProductProvider;
import com.smartmarket.code.service.DataBaseProductProviderService;
import com.smartmarket.code.service.ProductProviderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class DataBaseProductProviderServiceImpl implements DataBaseProductProviderService {

    @Autowired
    ProductProviderKafkaService productProviderKafkaService;

    @Autowired
    UserProductProviderRepository userProductProviderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    public void createDatabaseProductProvider(Map<String, Object> keyPairs) throws ParseException {
        productProviderKafkaService.createProductProviderKafka(keyPairs);
    }

    public void updateDatabaseProductProvider(Map<String, Object> keyPairs) throws ParseException{
        productProviderKafkaService.updateProductProviderKafka(keyPairs);
    }

    public void deleteDatabaseProductProvider(Map<String, Object> keyPairs){
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                Long id = ((Number)keyPairs.get(k)).longValue();
                productProviderKafkaService.deleteProductProviderKafka(id);

                List<UserProductProvider> userProductProviders = userProductProviderRepository.findByProductProviderId(id);
                if(!userProductProviders.isEmpty()){
                    for(UserProductProvider userProductProvider : userProductProviders){
                        String userName = userProductProvider.getUserName();
                        userProductProviderRepository.delete(userProductProvider);
                        userRepository.deleteByUserName(userName);
                        userProfileRepository.deleteByUserName(userName);
                        userRoleRepository.deleteByUserName(userName);
                    }
                }
            }
        }
    }

    public void truncateDatabaseProductProvider(){
        productProviderKafkaService.truncateProductProviderKafka();
    }
}
