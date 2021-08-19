package com.smartmarket.code.dao;

import com.smartmarket.code.model.IntervalHistory;
import com.smartmarket.code.model.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface IntervalHistoryRepository extends JpaRepository<IntervalHistory, Long> {
}
