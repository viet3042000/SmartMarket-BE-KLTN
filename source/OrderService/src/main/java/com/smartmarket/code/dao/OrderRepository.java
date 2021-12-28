package com.smartmarket.code.dao;

import com.smartmarket.code.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id for update", nativeQuery = true)
    public Optional<Orders> findAndLock(@Param("order_id") String orderId);

    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id", nativeQuery = true)
    public Optional<Orders> findByOrderId(@Param("order_id") String orderId);

//    @Query(value = "SELECT o FROM Orders o WHERE o.orderId=:order_id and o.userName=:user_name")
    @Query(value = "SELECT * FROM orders WHERE order_id=:order_id and user_name=:user_name", nativeQuery = true)
    public Optional<Orders> findByOrderIdAndUserName(@Param("order_id") String orderId,@Param("user_name") String userName);

    @Query(value = "from Orders o where (:userName is null or o.userName =:userName) and (:state is null or o.state =:state)")
    public List<Orders> findListOrder(@Param("userName") String userName, @Param("state") String state);

}
