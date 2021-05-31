package com.smartmarket.code.service;

//import com.smartmarket.code.dao.RoleRepository;
import com.smartmarket.code.dao.UserRepository;
//import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

//    @Autowired
//    RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //find user by name
        User appUser =
                userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No existe user_id on tbl_role_user"));

        //mapping list authority to userdetails
        List grantList = new ArrayList();
//        Set<Role> roleSet = roleRepository.findRoleUserByUserIdActive(appUser.getId());
//        for (Role role : roleSet) {
//            // ROLE_USER, ROLE_ADMIN,..
//            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
//            grantList.add(grantedAuthority);
//        }

        //set user detail
        UserDetails user = (UserDetails) new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), grantList);
        return user;
    }


    public User save(UserDTO user) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(newUser);
    }
}