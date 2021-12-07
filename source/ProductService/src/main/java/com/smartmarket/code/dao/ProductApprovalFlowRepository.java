package com.smartmarket.code.dao;

import com.smartmarket.code.model.ProductApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ProductApprovalFlowRepository extends JpaRepository<ProductApprovalFlow, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM product_approval_flow where product_id =:product_id", nativeQuery = true)
    public int deleteByProductId(@Param("product_id") Long productId) ;

    @Query(value = "Select * FROM product_approval_flow where product_id =:product_id", nativeQuery = true)
    public Optional<ProductApprovalFlow> findByProductId(@Param("product_id") Long productId) ;

    @Query(value = "Select * FROM product_approval_flow where flow_name =:flow_name " +
            "and product_id =:product_id", nativeQuery = true)
    public Optional<ProductApprovalFlow> findProductApprovalFlow(@Param("flow_name") String flowName, @Param("product_id") Long productId) ;
}
