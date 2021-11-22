package com.example.authserver.service.Impl;

import com.example.authserver.entities.User;
import com.example.authserver.entities.UserRole;
import com.example.authserver.repository.UserRepository;
import com.example.authserver.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userFromDB = userRepository.findByUserName(username);
//		UserRole userRole = userRoleRepository.findByUserName(username);
		UserDetails user;
        if (userFromDB != null && userFromDB.getId() > 0){
        	user = org.springframework.security.core.userdetails.User.withUsername(userFromDB.getUserName())
                    .password(userFromDB.getPassword())
                    .authorities("read")
//                    .authorities(userRole.getRoleName())
                    .build();
            return user;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
	}

}
