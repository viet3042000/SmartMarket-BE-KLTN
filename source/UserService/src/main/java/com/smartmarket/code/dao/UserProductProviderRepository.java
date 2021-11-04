package com.smartmarket.code.dao;


import com.smartmarket.code.model.UserProductProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserProductProviderRepository extends JpaRepository<UserProductProvider, String> {
    @Query(value = "Select * FROM user_product_provider where product_provider_name =:product_provider_name", nativeQuery = true)
    public Optional<UserProductProvider> findByProductProviderName(@Param("product_provider_name") String productProviderName) ;

    @Query(value = "Select * FROM user_product_provider where user_name =:user_name " +
            "and product_provider_name =:product_provider_name", nativeQuery = true)
    public Optional<UserProductProvider> findUser(@Param("user_name") String userName, @Param("product_provider_name") String productProviderName) ;
}
