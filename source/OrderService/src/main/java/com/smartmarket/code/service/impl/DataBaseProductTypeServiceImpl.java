package com.smartmarket.code.service.impl;

import com.smartmarket.code.service.DataBaseProductTypeService;
import com.smartmarket.code.service.ProductTypeKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;

@Service
public class DataBaseProductTypeServiceImpl implements DataBaseProductTypeService {

    @Autowired
    ProductTypeKafkaService productTypeKafkaService;

    public void createDatabaseProductType(Map<String, Object> keyPairs) throws ParseException {
        productTypeKafkaService.createProductTypeKafka(keyPairs);
    }

    public void updateDatabaseProductType(Map<String, Object> keyPairs) throws ParseException{
        productTypeKafkaService.updateProductTypeKafka(keyPairs);
    }

    public void deleteDatabaseProductType(Map<String, Object> keyPairs){
        String productTypeName = "";

        for (String k : keyPairs.keySet()) {
            if (k.equals("product_type_name")) {
                productTypeName = (String) keyPairs.get(k);
            }
        }
        productTypeKafkaService.deleteProductTypeKafka(productTypeName);
    }

    public void truncateDatabaseProductType(){
        productTypeKafkaService.truncateProductTypeKafka();
    }
}
