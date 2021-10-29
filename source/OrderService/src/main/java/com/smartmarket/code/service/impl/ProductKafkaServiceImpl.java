package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.Role;
import com.smartmarket.code.service.ProductKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ProductKafkaServiceImpl implements ProductKafkaService {

    @Autowired
    ProductRepository productRepository;


    public Product createProductKafka(Map<String, Object> keyPairs) throws ParseException {
        Product product = new Product();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                product.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_name")) {
                product.setProductName((String) keyPairs.get(k));
            }
            if (k.equals("type")) {
                product.setType((String) keyPairs.get(k));
            }
            if (k.equals("product_provider")) {
                product.setProductProvider((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                product.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("price")) {
                product.setPrice((String) keyPairs.get(k));
            }
            if (k.equals("state")) {
                product.setState((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                product.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productRepository.save(product);
    }

    public Product updateProductKafka(Map<String, Object> keyPairs) throws ParseException{
        Product product = new Product();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                product.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_name")) {
                product.setProductName((String) keyPairs.get(k));
            }
            if (k.equals("type")) {
                product.setType((String) keyPairs.get(k));
            }
            if (k.equals("product_provider")) {
                product.setProductProvider((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                product.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("price")) {
                product.setPrice((String) keyPairs.get(k));
            }
            if (k.equals("state")) {
                product.setState((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                product.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productRepository.save(product);
    }


    public int deleteProductKafka(String productName) {
        return productRepository.deleteProductKafka(productName);
    }

    public int truncateProductKafka() {
        return productRepository.truncateProductKafka();
    }

}
