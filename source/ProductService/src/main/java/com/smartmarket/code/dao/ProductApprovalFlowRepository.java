package com.smartmarket.code.dao;

import com.smartmarket.code.model.ProductApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ProductApprovalFlowRepository extends JpaRepository<ProductApprovalFlow, Long> {

    @Query(value = "Select * FROM product_approval_flow where flow_name =:flow_name " +
            "and product_id =:product_id", nativeQuery = true)
    public Optional<ProductApprovalFlow> findProductApprovalFlow(@Param("flow_name") String flowName, @Param("product_id") Long productId) ;
}
