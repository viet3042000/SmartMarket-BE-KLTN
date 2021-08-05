package com.smartmarket.code.service.impl;

import com.smartmarket.code.constants.Constant;
import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.model.User;
import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.util.Date;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;



    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private Long gender;

    @Column(name = "identify_number")
    private String identifyNumber;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "enabled")
    private Long enabled;
    @Override
    public UserProfile create(UserProfile object) {

        object.setEnabled(Constant.STATUS.ACTIVE);
        object.setUsername(object.getUsername());
        object.setEmail(object.getEmail());
        object.setPhoneNumber(object.getPhoneNumber());
        object.setAddress(object.getAddress());
        object.setGender(object.getGender());
        object.setBirthDate(object.getBirthDate());
        object.setFullName(object.getFullName());
        object.setCreateDate(new Date());

        return userProfileRepository.save(object);
    }

    @Override
    public UserProfile update(UserProfile object) {
        UserProfile userUpdate = userProfileRepository.(object.getId()).orElse(null);
        if (userUpdate != null) {
            object.setEnabled(object.getEnabled());
            object.setPassword(object.getPassword());
            object.setUserName(object.getUserName());
        }
        userProfileRepository.save(object);
        return userUpdate;

    }

    @Override
    public UserProfile delete(Long id) {
        UserProfile userDelete = userProfileRepository.findByUserId(id).orElse(null);
        if (userDelete != null) {
            userProfileRepository.delete(userDelete);
        }
        return userDelete;
    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        return userProfileRepository.findByUsername(username);
    }

    @Override
    public Long findUserIdByUsername(String username) {
        return userProfileRepository.findUserIdByUsername(username);
    }
}
