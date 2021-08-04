package com.example.authserver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.authserver.entities.UserEntity;
import com.example.authserver.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepository;	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userFromDB = userRepository.findByUserName(username);		
		UserDetails user;
        if (userFromDB != null && userFromDB.getId() > 0){
        	user = User.withUsername(userFromDB.getUserName())
                    .password(userFromDB.getUserPassword())
                    .authorities("read")
                    .build();
            return user;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
	}

}
