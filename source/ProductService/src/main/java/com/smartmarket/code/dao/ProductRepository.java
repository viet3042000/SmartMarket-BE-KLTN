package com.smartmarket.code.dao;

import com.smartmarket.code.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query(value = "Select * FROM product where product_name =:product_name", nativeQuery = true)
    public Optional<Product> findByProductName(@Param("product_name") String productName) ;

    @Query(value = "SELECT * FROM product WHERE product_provider=:user_name order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Product> findByUserName(@Param("user_name") String userName, Pageable pageable);

    @Query(value = "SELECT * FROM product order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Product> getAll(Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE order by state=:state created_logtimestamp DESC",nativeQuery = true)
    public Page<Product> getAllByState(@Param("state") String state, Pageable pageable);
}
