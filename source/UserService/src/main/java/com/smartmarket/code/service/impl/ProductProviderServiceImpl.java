package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.ProductProviderRepository;
import com.smartmarket.code.model.ProductProvider;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.service.ProductProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductProviderServiceImpl implements ProductProviderService {
    @Autowired
    ProductProviderRepository productProviderRepository;

    @Override
    public ProductProvider create(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) {
        ProductProvider productProvider = new ProductProvider();
        productProvider.setProductProviderName(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getProductProviderName());
        productProvider.setCreatedLogtimestamp(new Date());
        productProvider.setDesc(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getDesc());
        return productProviderRepository.save(productProvider);
    }

//    @Override
//    public ProductProvider update(ProductProvider productProvider,BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail) {
//        productProvider.setDesc();
//        productProviderRepository.save(productProvider);
//    }


//    public Optional<ProductProvider> findByProductProviderName(String productProviderName){
//        return productProviderRepository.findByProductProviderName(productProviderName);
//    }

}
