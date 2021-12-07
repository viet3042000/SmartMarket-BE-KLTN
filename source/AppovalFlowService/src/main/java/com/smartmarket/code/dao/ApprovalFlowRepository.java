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

    @Query(value = "Select * FROM approval_flow where product_provider_name =:product_provider_name and flow_name=:flow_name", nativeQuery = true)
    public Optional<ApprovalFlow> findApprovalFlow(@Param("product_provider_name") String productProviderName,
                                               @Param("flow_name") String flowName) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM approval_flow where product_provider_name =:product_provider_name", nativeQuery = true)
    public int deleteByProductProviderName(@Param("product_provider_name") String productProviderName) ;

}
