package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface ProductService {
    public void createProduct(Map<String, Object> keyPairs) throws ParseException;
    public void updateProduct(Map<String, Object> keyPairs) throws ParseException;
    public void deleteProduct(Map<String, Object> keyPairs);
    public void truncateProduct();
}
