package com.smartmarket.code.dao;

import com.smartmarket.code.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

	@Query(value = "from Client c where c.clientId =:clientId")
	public Optional<Client> findByclientName(@Param("clientId") String clientId);

	@Query(value = "select c.id,c.client_id from clients c where c.client_id =:clientId" , nativeQuery = true)
	public Object[] findByclientName2(@Param("clientId") String clientId);


	@Modifying(clearAutomatically = true)
	// Đây là Native SQL
	@Query(value = "UPDATE clients set client_secret =:secret, id =:id, " +
			" consumer_id = :consumerId " +
			"where client_id =:clientId", nativeQuery = true)
	public int updateConsumerClientKafka(@Param("id") Long id, @Param("clientId") String clientId,
										 @Param("secret") String secret ,@Param("consumerId") String consumerId) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM clients where client_id =:clientId", nativeQuery = true)
	public int deleteConsumerClientKafka(@Param("clientId") String clientId) ;

	@Modifying(clearAutomatically = true)
	@Query(value = "TRUNCATE TABLE clients",  nativeQuery = true)
	public int truncateConsumerClientKafka() ;
}