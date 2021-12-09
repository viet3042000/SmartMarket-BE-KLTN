package com.smartmarket.code.dao;

import com.smartmarket.code.model.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, String> {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM saga_state where aggregateid =:product_id", nativeQuery = true)
    public int deleteByProductId(@Param("product_id") String productId) ;

    //id = request_id
    @Query(value = "SELECT * FROM saga_state WHERE id=:id", nativeQuery = true)
    public Optional<SagaState> findById(@Param("id") String id);

    //product pending --> only 1 saga current (product_id of product that is pending)
    @Query(value = "Select * FROM saga_state where aggregateid =:aggregateid", nativeQuery = true)
    public Optional<SagaState> findByAggregateId(@Param("aggregateid") String aggregateId) ;
}
