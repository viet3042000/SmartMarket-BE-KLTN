package com.smartmarket.code.dao;

import com.smartmarket.code.model.OrdersServiceEntity;
import com.smartmarket.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<OrdersServiceEntity, String> {
    @Query(value = "SELECT * FROM orders WHERE id=:id", nativeQuery = true)
    public OrdersServiceEntity findById(@Param("id") Long id);
}
