package com.smartmarket.code.dao;

import com.smartmarket.code.model.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Long> {

//    @Query(value = "Select * FROM approval_flow where product_provider_id =:product_provider_id and flow_name=:flow_name", nativeQuery = true)
//    public List<ApprovalFlow> findApprovalFlow(@Param("product_provider_id") Long productProviderId,
//                                               @Param("flow_name") String flowName) ;

    @Query(value = "Select * FROM approval_flow where product_name =:product_name and " +
            "product_provider_id=:product_provider_id and flow_name=:flow_name", nativeQuery = true)
    public Optional<ApprovalFlow> findApprovalFlowOfProduct(@Param("product_name") String productName,
                                                            @Param("product_provider_id") Long productProviderId,
                                                            @Param("flow_name") String flowName) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM approval_flow where product_provider_id =:product_provider_id", nativeQuery = true)
    public int deleteByProductProviderId(@Param("product_provider_id") Long productProviderId) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM approval_flow where product_id =:product_id", nativeQuery = true)
    public int deleteByProductId(@Param("product_id") Long productId) ;
}
