package com.smartmarket.code.dao;

import com.smartmarket.code.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query(value = "from Client c where c.clientIdCode =:clientIdCode")
	public Optional<Client> findByclientName(@Param("clientIdCode") String clientIdCode);

	@Query(value = "select c.client_id_code,c.client_id from clients c where c.client_id_code =:clientIdCode" , nativeQuery = true)
	public Object[] findByclientName2(@Param("clientIdCode")String clientIdCode);
}