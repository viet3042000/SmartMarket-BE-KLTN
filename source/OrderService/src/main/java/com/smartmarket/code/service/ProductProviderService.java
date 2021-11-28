package com.smartmarket.code.service;

import java.text.ParseException;
import java.util.Map;

public interface ProductProviderService {

    public void createProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void updateProductProvider(Map<String, Object> keyPairs) throws ParseException;
    public void deleteProductProvider(Map<String, Object> keyPairs);
    public void truncateProductProvider();
}
