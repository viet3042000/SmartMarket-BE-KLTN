package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseProductProviderService;
import com.smartmarket.code.service.ProductProviderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseProductProviderServiceImpl implements DataBaseProductProviderService {

    @Autowired
    ProductProviderKafkaService productProviderKafkaService;

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
            }
        }
    }

    public void truncateDatabaseProductProvider(){
        productProviderKafkaService.truncateProductProviderKafka();
    }
}
