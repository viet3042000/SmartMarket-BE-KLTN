package com.smartmarket.code.dao;

import com.smartmarket.code.model.Product;
import com.smartmarket.code.model.ProductType;
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
public interface ProductTypeRepository extends JpaRepository<ProductType, String> {
    @Query(value = "Select * FROM product_type where product_type_name =:product_type_name", nativeQuery = true)
    public Optional<ProductType> findByProductTypeName(@Param("product_type_name") String productTypeName) ;

    @Query(value = "SELECT * FROM product_type order by created_logtimestamp DESC",nativeQuery = true)
    public Page<ProductType> getAll(Pageable pageable);
}
