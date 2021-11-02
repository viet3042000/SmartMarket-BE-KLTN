package com.smartmarket.code.service;

import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;

import java.util.Optional;


public interface ProductProviderService {
    ProductProvider create(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) ;

//    public ProductProvider update(ProductProvider productProvider,BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail) ;

//    Optional<ProductProvider> findByProductProviderName(String productProviderName);
}
