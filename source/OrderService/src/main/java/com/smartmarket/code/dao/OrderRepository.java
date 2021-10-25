package com.smartmarket.code.dao;

import com.smartmarket.code.model.Orders;
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
public interface OrderRepository extends JpaRepository<Orders, String> {

//    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id", nativeQuery = true)
//    public OrdersServiceEntity findByOrderId(@Param("order_id") UUID orderId);

    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id", nativeQuery = true)
    public Optional<Orders> findByOrderId(@Param("order_id") String orderId);

    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id and user_name=:user_name", nativeQuery = true)
    public Optional<Orders> findByOrderIdAndUserName(@Param("order_id") String orderId,@Param("user_name") String userName);

    @Query(value = "SELECT * FROM orders WHERE user_name=:user_name and type=:type order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Orders> findByUserNameAndType(@Param("user_name") String userName, @Param("type") String type, Pageable pageable);

    @Query(value = "SELECT * FROM orders WHERE user_name=:user_name order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Orders> findByUserName(@Param("user_name") String userName, Pageable pageable);

    @Query(value = "SELECT * FROM orders order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Orders> getAll(Pageable pageable);

    @Query(value = "SELECT * FROM orders WHERE type=:type order by created_logtimestamp DESC",nativeQuery = true)
    public Page<Orders> getAllByType(@Param("type") String type,Pageable pageable);

}
