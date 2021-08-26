package com.smartmarket.code.dao;

import com.smartmarket.code.model.OrderProduct;
import com.smartmarket.code.model.OrdersServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, String> {
    @Query(value = "SELECT * FROM order_product WHERE order_id=:order_id", nativeQuery = true)
    public Optional<OrderProduct> findByOrderId(@Param("order_id") String orderId);

}
