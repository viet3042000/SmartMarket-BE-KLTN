package com.smartmarket.code.dao;

import com.smartmarket.code.model.OrderProduct;
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
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query(value = "SELECT * FROM order_product WHERE order_id=:order_id order by index", nativeQuery = true)
    public List<OrderProduct> findByOrderId(@Param("order_id") String orderId);

    @Query(value = "SELECT COUNT(order_id) FROM order_product where order_id =:order_id and state=:state", nativeQuery = true)
    public int countByState(@Param("order_id") String orderId, @Param("state") String state);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM order_product where order_id =:order_id", nativeQuery = true)
    public int deleteOrderProduct(@Param("order_id") String orderId) ;

    @Modifying
    @Query(value = "UPDATE order_product set state=:state where order_id =:order_id and index=:index", nativeQuery = true)
    public int updateOrderProduct(@Param("order_id") String orderId, @Param("state") String state,@Param("index") int index) ;

    @Query(value = "SELECT * FROM order_product WHERE order_id=:order_id and index=:index", nativeQuery = true)
    public Optional<OrderProduct> findOrderProduct(@Param("order_id") String orderId,@Param("index") int index);

}
