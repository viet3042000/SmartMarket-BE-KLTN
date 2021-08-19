package com.smartmarket.code.dao;

import com.smartmarket.code.model.IntervalHistory;
import com.smartmarket.code.model.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface IntervalHistoryRepository extends JpaRepository<IntervalHistory, Long> {

    @Query(value = "SELECT * FROM interval_history WHERE interval_id=:interval_id and step =:step", nativeQuery = true)
    public IntervalHistory findIntervalHistory(@Param("interval_id") String intervalId, @Param("step") int step);
}
