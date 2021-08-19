package com.smartmarket.code.dao;

import com.smartmarket.code.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
//Your TravelinsuranceOutbox id data type is int, so your repository Id should be int too.
// Have used string and not encountered any error yet
public interface OutboxRepository extends JpaRepository<Outbox, Long> {
}