package com.smartmarket.code.dao;

import com.smartmarket.code.model.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, String> {
    //id = request_id
    @Query(value = "SELECT * FROM saga_state WHERE id=:id", nativeQuery = true)
    public Optional<SagaState> findById(@Param("id") String id);

//    @Query(value = "SELECT * FROM saga_state WHERE order_id=:order_id", nativeQuery = true)
//    public SagaState findByOrderId(@Param("order_id") UUID orderId);

}
