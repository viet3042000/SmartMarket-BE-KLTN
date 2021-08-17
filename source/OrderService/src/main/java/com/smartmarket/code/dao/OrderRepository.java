package com.smartmarket.code.dao;

import com.smartmarket.code.model.OrdersServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<OrdersServiceEntity, String> {
    @Query(value = "SELECT * FROM orders WHERE id=:id", nativeQuery = true)
    public OrdersServiceEntity findById(@Param("id") Long id);

//    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id", nativeQuery = true)
//    public OrdersServiceEntity findByOrderId(@Param("order_id") UUID orderId);

    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id", nativeQuery = true)
    public OrdersServiceEntity findByOrderId(@Param("order_id") String orderId);

    @Query(value = "SELECT * FROM orders WHERE user_name=:user_name",nativeQuery = true)
    public Page<OrdersServiceEntity> findByUserName(@Param("user_name") String userName, Pageable pageable);
}
