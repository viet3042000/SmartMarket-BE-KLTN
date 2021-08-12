package com.smartmarket.code.dao;

import com.smartmarket.code.model.PendingBICTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PendingBICTransactionRepository extends JpaRepository<PendingBICTransaction, Long> {

    @Query(value = "Select * from pending_bic_transaction ORDER BY id LIMIT 5",nativeQuery = true)
    public List<PendingBICTransaction> getPendingBICTransaction();
}
