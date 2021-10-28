package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.dao.ProductTypeRepository;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductType;
import com.smartmarket.code.service.ProductTypeKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ProductTypeKafkaServiceImpl implements ProductTypeKafkaService {

    @Autowired
    ProductTypeRepository productTypeRepository;


    public ProductType createProductTypeKafka(Map<String, Object> keyPairs) throws ParseException {
        ProductType productType = new ProductType();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                productType.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_type_name")) {
                productType.setProductTypeName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                productType.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                productType.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productTypeRepository.save(productType);
    }

    public ProductType updateProductTypeKafka(Map<String, Object> keyPairs) throws ParseException{
        ProductType productType = new ProductType();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                productType.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_type_name")) {
                productType.setProductTypeName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                productType.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                productType.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productTypeRepository.save(productType);
    }


    public int deleteProductTypeKafka(String productTypeName) {
        return productTypeRepository.deleteProductTypeKafka(productTypeName);
    }

    public int truncateProductTypeKafka() {
        return productTypeRepository.truncateProductTypeKafka();
    }
}
