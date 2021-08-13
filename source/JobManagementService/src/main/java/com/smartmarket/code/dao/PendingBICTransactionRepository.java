package com.smartmarket.code.dao;

import com.smartmarket.code.model.PendingBICTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PendingBICTransactionRepository extends JpaRepository<PendingBICTransaction, Long> {

    @Query(value = "Select * from pending_bic_transaction ORDER BY id LIMIT 10",nativeQuery = true)
    public List<PendingBICTransaction> getPendingBICTransaction();

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM pending_bic_transaction where id =:id", nativeQuery = true)
    public int deletePendingBICTransactionByID(@Param("id") Long id) ;
}
