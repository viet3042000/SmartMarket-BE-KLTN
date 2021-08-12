package com.smartmarket.code.dao;

import com.smartmarket.code.model.PendingBICTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingBICTransactionRepository extends JpaRepository<PendingBICTransaction, Long> {

}
