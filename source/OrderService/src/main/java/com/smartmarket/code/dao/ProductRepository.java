package com.smartmarket.code.dao;

import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.User;
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
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(value = "select * from product where id =:id",nativeQuery = true)
    public Optional<Product> findByProductId(@Param("id") Long id);

    @Query(value = "select * from product where product_name =:product_name",nativeQuery = true)
    public Optional<Product> findByProductName(@Param("product_name") String productName);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM product where id =:id", nativeQuery = true)
    public int deleteProductKafka(@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE product",  nativeQuery = true)
    public int truncateProductKafka() ;

}
