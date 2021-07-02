package com.smartmarket.code.dao;

import com.smartmarket.code.model.BICTransaction;
import com.smartmarket.code.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface BICTransactionRepository extends JpaRepository<BICTransaction, Long> {
}