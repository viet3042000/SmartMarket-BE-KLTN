package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ProductProviderRepository;
import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.service.ProductProviderKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ProductProviderKafkaServiceImpl implements ProductProviderKafkaService {

    @Autowired
    ProductProviderRepository productProviderRepository;


    public ProductProvider createProductProviderKafka(Map<String, Object> keyPairs) throws ParseException {
        ProductProvider productProvider = new ProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                productProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_name")) {
                productProvider.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                productProvider.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                productProvider.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productProviderRepository.save(productProvider);
    }

    public ProductProvider updateProductProviderKafka(Map<String, Object> keyPairs) throws ParseException{
        ProductProvider productProvider = new ProductProvider();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                productProvider.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_name")) {
                productProvider.setProductProviderName((String) keyPairs.get(k));
            }
            if (k.equals("description")) {
                productProvider.setDesc((String) keyPairs.get(k));
            }
            if (k.equals("created_logtimestamp")) {
                String createAt = (String)keyPairs.get(k);
                productProvider.setCreatedLogtimestamp(formatter.parse(createAt));
            }
        }
        return productProviderRepository.save(productProvider);
    }


    public int deleteProductProviderKafka(Long id) {
        return productProviderRepository.deleteProductProviderKafka(id);
    }

    public int truncateProductProviderKafka() {
        return productProviderRepository.truncateProductProviderKafka();
    }
}
