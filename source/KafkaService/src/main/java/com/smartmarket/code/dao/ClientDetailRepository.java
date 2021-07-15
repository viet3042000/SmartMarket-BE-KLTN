package com.smartmarket.code.dao;

import com.smartmarket.code.model.ClientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ClientDetailRepository extends JpaRepository<ClientDetail, Long> {

	@Query(value = "from ClientDetail c where c.clientId =:clientId")
	public Optional<ClientDetail> findByclientIdName(@Param("clientId") String clientId);


}