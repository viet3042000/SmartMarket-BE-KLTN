package com.smartmarket.code.dao;


import com.smartmarket.code.model.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, String> {

    @Query(value = "SELECT * FROM job_history WHERE interval_id=:interval_id", nativeQuery = true)
    public Optional<JobHistory> findByIntervalId(@Param("interval_id") String intervalId);
}
