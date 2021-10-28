package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseProductService;
import com.smartmarket.code.service.ProductKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseProductServiceImpl implements DataBaseProductService {

    @Autowired
    ProductKafkaService productKafkaService;

    public void createDatabaseProduct(Map<String, Object> keyPairs) throws ParseException {
        productKafkaService.createProductKafka(keyPairs);
    }

    public void updateDatabaseProduct(Map<String, Object> keyPairs) throws ParseException{
        productKafkaService.updateProductKafka(keyPairs);
    }

    public void deleteDatabaseProduct(Map<String, Object> keyPairs){
        String productName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("product_name")) {
                productName = (String) keyPairs.get(k);
            }
        }
        productKafkaService.deleteProductKafka(productName);
    }

    public void truncateDatabaseProduct(){
        productKafkaService.truncateProductKafka();
    }
}
