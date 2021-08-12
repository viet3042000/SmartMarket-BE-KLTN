package com.smartmarket.code.dao;

import com.smartmarket.code.model.JobManagementOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobManagementOutboxRepository extends JpaRepository<JobManagementOutbox, Long> {
}
