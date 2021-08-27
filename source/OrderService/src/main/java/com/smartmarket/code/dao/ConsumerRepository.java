package com.smartmarket.code.dao;

import com.smartmarket.code.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Transactional
@Repository
public interface ConsumerRepository extends JpaRepository<Consumers, String> {

    @Modifying(clearAutomatically = true)
    // Đây là Native SQL
    @Query(value = "UPDATE consumers set created_at =:createAt where consumer_id =:consumerId", nativeQuery = true)
    public int updateConsumerKafka(@Param("consumerId") String consumerId, @Param("createAt") Date createAt) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM consumers where consumer_id =:consumerId", nativeQuery = true)
    public int deleteConsumerKafka(@Param("consumerId") String consumerId) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE consumers", nativeQuery = true)
    public int truncateConsumerKafka() ;
}
