package com.smartmarket.code.dao;

import com.smartmarket.code.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query(value = "from Client c where c.clientIdName =:clientIdName")
	public Optional<Client> findByclientName(@Param("clientIdName") String clientIdName);

	@Query(value = "select c.client_id_name,c.client_id from clients c where c.client_id_name =:clientIdName" , nativeQuery = true)
	public Object[] findByclientName2(@Param("clientIdName")String clientIdName);
}