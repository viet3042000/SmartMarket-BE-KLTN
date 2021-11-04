package com.smartmarket.code.service;

import com.smartmarket.code.model.ProductProvider;

import java.text.ParseException;
import java.util.Map;

public interface ProductProviderKafkaService {
    public ProductProvider createProductProviderKafka(Map<String, Object> keyPairs) throws ParseException;

    public ProductProvider updateProductProviderKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteProductProviderKafka(String productProviderName) ;

    public int truncateProductProviderKafka() ;
}
