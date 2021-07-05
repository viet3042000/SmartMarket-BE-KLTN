package com.smartmarket.code.dao;

import com.smartmarket.code.model.Consumers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ConsumerRepository extends JpaRepository<Consumers, Integer> {

    @Modifying(clearAutomatically = true)
    // Đây là Native SQL
    @Query(value = "UPDATE consumers set created_at =:createAt where consumer_id_sync = :consumerIdSync", nativeQuery = true)
    public int updateConsumerClientKafka(@Param("consumerIdSync") String consumerIdSync, @Param("createAt") String createAt) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM consumers where consumer_id_sync := consumerIdSync", nativeQuery = true)
    public int deleteConsumerClientKafka(@Param("consumerIdSync") String consumerIdSync) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE consumers", nativeQuery = true)
    public int truncateConsumerClientKafka() ;
}
