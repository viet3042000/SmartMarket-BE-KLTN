package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ApprovalFlowRepository;
import com.smartmarket.code.dao.ProductRepository;
import com.smartmarket.code.model.Product;
import com.smartmarket.code.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    ProductRepository productRepository;

    public void createProduct(Map<String, Object> keyPairs) throws ParseException {
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
        productRepository.save(product);
    }

    public void updateProduct(Map<String, Object> keyPairs) throws ParseException{
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
        productRepository.save(product);
    }

    public void deleteProduct(Map<String, Object> keyPairs){
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                Long id = ((Number)keyPairs.get(k)).longValue();
                productRepository.deleteProductKafka(id);
            }
        }
    }

    public void truncateProduct(){
        productRepository.truncateProductKafka();
    }
}
