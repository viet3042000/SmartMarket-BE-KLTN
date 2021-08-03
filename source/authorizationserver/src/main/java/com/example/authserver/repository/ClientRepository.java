package com.example.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.authserver.entities.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
	
    ClientEntity findByClientId(String clientId);
}
