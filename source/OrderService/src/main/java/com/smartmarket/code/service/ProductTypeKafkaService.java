package com.smartmarket.code.service;

import com.smartmarket.code.model.ProductType;

import java.text.ParseException;
import java.util.Map;

public interface ProductTypeKafkaService {
    public ProductType createProductTypeKafka(Map<String, Object> keyPairs) throws ParseException;

    public ProductType updateProductTypeKafka(Map<String, Object> keyPairs) throws ParseException;

    public int deleteProductTypeKafka(String productTypeName) ;

    public int truncateProductTypeKafka() ;
}
