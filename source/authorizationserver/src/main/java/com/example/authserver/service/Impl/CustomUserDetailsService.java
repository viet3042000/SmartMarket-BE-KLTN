package com.example.authserver.service.Impl;

import com.example.authserver.entities.User;
import com.example.authserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepository;	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userFromDB = userRepository.findByUserName(username);
		UserDetails user;
        if (userFromDB != null && userFromDB.getId() > 0){
        	user = org.springframework.security.core.userdetails.User.withUsername(userFromDB.getUserName())
                    .password(userFromDB.getPassword())
                    .authorities("read")
                    .build();
            return user;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
	}

}
