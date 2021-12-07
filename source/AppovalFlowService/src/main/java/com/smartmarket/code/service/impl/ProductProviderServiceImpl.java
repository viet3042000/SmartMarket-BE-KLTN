package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ApprovalFlowRepository;
import com.smartmarket.code.dao.ProductProviderRepository;
import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.service.ProductProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ProductProviderServiceImpl implements ProductProviderService {
    @Autowired
    ProductProviderRepository productProviderRepository;

    @Autowired
    ApprovalFlowRepository approvalFlowRepository;

    public void createProductProvider(Map<String, Object> keyPairs) throws ParseException {
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
        productProviderRepository.save(productProvider);
    }

    public void updateProductProvider(Map<String, Object> keyPairs) throws ParseException{
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
        productProviderRepository.save(productProvider);
    }

    public void deleteProductProvider(Map<String, Object> keyPairs){
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                productProviderRepository.deleteProductProviderKafka(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("product_provider_name")) {
                approvalFlowRepository.deleteByProductProviderName((String) keyPairs.get(k));
            }
        }
    }

    public void truncateProductProvider(){
        productProviderRepository.truncateProductProviderKafka();
    }
}
