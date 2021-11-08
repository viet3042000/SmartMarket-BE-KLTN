package com.smartmarket.code.dao;

import com.smartmarket.code.model.ProductProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ProductProviderRepository extends JpaRepository<ProductProvider, String> {
    @Query(value = "select id from product_provider where product_provider_name=:product_provider_name",nativeQuery = true)
    public Long getId(@Param("product_provider_name") String productProviderName);

    @Query(value = "Select * FROM product_provider where product_provider_name =:product_provider_name", nativeQuery = true)
    public Optional<ProductProvider> findByProductTypeName(@Param("product_provider_name") String productProviderName) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM product_provider where id =:id", nativeQuery = true)
    public int deleteProductProviderKafka(@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE product_provider",  nativeQuery = true)
    public int truncateProductProviderKafka() ;
}
