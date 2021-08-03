package com.example.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authserver.entities.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	UserEntity findByUserName(String username);
}

