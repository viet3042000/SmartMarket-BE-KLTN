package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateProviderUserRequest;
import com.smartmarket.code.request.CreateUserRequest;
import com.smartmarket.code.request.UpdateUserRequest;
import com.smartmarket.code.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Override
    public UserProfile create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) {
        UserProfile userProfileCreate = new UserProfile();

        userProfileCreate.setFullName(createUserRequestBaseDetail.getDetail().getUser().getFullName());
        userProfileCreate.setBirthDate(createUserRequestBaseDetail.getDetail().getUser().getBirthDate());
        userProfileCreate.setIdentifyNumber(createUserRequestBaseDetail.getDetail().getUser().getIdentifyNumber());
        userProfileCreate.setGender(createUserRequestBaseDetail.getDetail().getUser().getGender());
        userProfileCreate.setAddress(createUserRequestBaseDetail.getDetail().getUser().getAddress());
        userProfileCreate.setPhoneNumber(createUserRequestBaseDetail.getDetail().getUser().getPhoneNumber());
        userProfileCreate.setEmail(createUserRequestBaseDetail.getDetail().getUser().getEmail());
        userProfileCreate.setUserName(createUserRequestBaseDetail.getDetail().getUser().getUserName());
        userProfileCreate.setCreateDate(new Date());
        userProfileCreate.setEnabled(createUserRequestBaseDetail.getDetail().getUser().getEnabled());

        return userProfileRepository.save(userProfileCreate);
    }

    @Override
    public UserProfile createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) {
        UserProfile userProfileCreate = new UserProfile();

        userProfileCreate.setFullName(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getFullName());
        userProfileCreate.setBirthDate(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getBirthDate());
        userProfileCreate.setIdentifyNumber(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getIdentifyNumber());
        userProfileCreate.setGender(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getGender());
        userProfileCreate.setAddress(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getAddress());
        userProfileCreate.setPhoneNumber(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getPhoneNumber());
        userProfileCreate.setEmail(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getEmail());
        userProfileCreate.setUserName(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getUserName());
        userProfileCreate.setCreateDate(new Date());
        userProfileCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getUser().getEnabled());

        return userProfileRepository.save(userProfileCreate);
    }

    @Override
    public UserProfile update(UserProfile userProfile,BaseDetail<UpdateUserRequest> updateUserRequestBaseDetail) {
        userProfile.setEnabled(updateUserRequestBaseDetail.getDetail().getUser().getEnabled());
        userProfile.setEmail(updateUserRequestBaseDetail.getDetail().getUser().getEmail());
        userProfile.setPhoneNumber(updateUserRequestBaseDetail.getDetail().getUser().getPhoneNumber());
        userProfile.setAddress(updateUserRequestBaseDetail.getDetail().getUser().getAddress());
        userProfile.setGender(updateUserRequestBaseDetail.getDetail().getUser().getGender());
        userProfile.setBirthDate(updateUserRequestBaseDetail.getDetail().getUser().getBirthDate());
        userProfile.setFullName(updateUserRequestBaseDetail.getDetail().getUser().getFullName());
        userProfile.setIdentifyNumber(updateUserRequestBaseDetail.getDetail().getUser().getIdentifyNumber());
        userProfileRepository.save(userProfile);
        return userProfile;
    }

//    @Override
//    public UserProfile deleteByUserName(String userName) {
//        UserProfile userDelete = userProfileRepository.findByUsername(userName).orElse(null);
//        if (userDelete != null) {
//            userProfileRepository.delete(userDelete);
//        }
//        return userDelete;
//    }

    @Override
    public Optional<UserProfile> findByUsername(String username) {
        return userProfileRepository.findByUsername(username);
    }


//    @Override
//    public Long findUserIdByUsername(String username) {
//        return userProfileRepository.findUserIdByUsername(username);
//    }
}
