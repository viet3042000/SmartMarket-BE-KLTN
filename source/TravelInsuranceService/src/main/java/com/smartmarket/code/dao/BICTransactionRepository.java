package com.smartmarket.code.dao;

import com.smartmarket.code.model.BICTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface BICTransactionRepository extends JpaRepository<BICTransaction, Long> {

    @Query(value = "SELECT * FROM bic_transaction bict where bict.order_id = :orderId" +
            "    and bict.result_code= '000'" +
            "    and bict.type = 'CREATE'" +
            "    and bict.bic_result_code= '200 OK' " , nativeQuery = true)
    public Optional<BICTransaction> findBICTransactionSuccessByOrderID(@Param("orderId") String orderId);


    @Query(value = "SELECT * FROM bic_transaction bict where bict.order_reference = :orderReference" +
            "    and bict.result_code= '000'" +
            "    and bict.type = 'CREATE'" +
            "    and bict.bic_result_code= '200 OK' " , nativeQuery = true)
    public Optional<BICTransaction> findBICTransactionSuccessByOrderRef(@Param("orderReference") String orderReference);


    @Query(value = "SELECT * FROM bic_transaction bict where bict.order_id = :orderId" +
            "    and bict.order_reference =:order_reference" +
            "    and bict.request_id =:request_id" +
            "    and (bict.result_code = '068' or bict.result_code = '005')" +
            "    and bict.bic_result_code != '200 OK'" , nativeQuery = true)
    public Optional<BICTransaction> findBICTransactionPending(@Param("orderId") String orderId, @Param("order_reference") String orderReference,
                                           @Param("request_id") String requestId);

}