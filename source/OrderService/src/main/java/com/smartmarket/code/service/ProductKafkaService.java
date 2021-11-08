package com.smartmarket.code.service;

import com.smartmarket.code.model.Product;

import java.text.ParseException;
import java.util.Map;

public interface ProductKafkaService {
    public Product createProductKafka(Map<String, Object> keyPairs) throws ParseException;

    public Product updateProductKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteProductKafka(Long id) ;

    public int truncateProductKafka() ;
}
