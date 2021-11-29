package com.smartmarket.code.dao;

import com.smartmarket.code.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}
