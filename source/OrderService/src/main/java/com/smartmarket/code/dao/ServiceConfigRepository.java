package com.smartmarket.code.dao;


import com.smartmarket.code.model.ServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ServiceConfigRepository extends JpaRepository<ServiceConfig, Long> {

    @Query(value = "select sc.key,sc.value from service_config sc " , nativeQuery = true)
    public List<Object[]> findAllServiceConfigToMap();
}