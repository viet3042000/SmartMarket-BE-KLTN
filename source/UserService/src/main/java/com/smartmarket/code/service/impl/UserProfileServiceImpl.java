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
import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Override
    public UserProfile create(BaseDetail<CreateUserRequest> createUserRequestBaseDetail) {
        UserProfile userProfileCreate = new UserProfile();

        userProfileCreate.setFullName(createUserRequestBaseDetail.getDetail().getFullName());
        userProfileCreate.setBirthDate(createUserRequestBaseDetail.getDetail().getBirthDate());
        userProfileCreate.setIdentifyNumber(createUserRequestBaseDetail.getDetail().getIdentifyNumber());
        userProfileCreate.setGender(createUserRequestBaseDetail.getDetail().getGender());
        userProfileCreate.setAddress(createUserRequestBaseDetail.getDetail().getAddress());
        userProfileCreate.setPhoneNumber(createUserRequestBaseDetail.getDetail().getPhoneNumber());
        userProfileCreate.setEmail(createUserRequestBaseDetail.getDetail().getEmail());
        userProfileCreate.setUserName(createUserRequestBaseDetail.getDetail().getUserName());
        userProfileCreate.setCreateDate(new Date());
        userProfileCreate.setEnabled(createUserRequestBaseDetail.getDetail().getEnabled());

        return userProfileRepository.save(userProfileCreate);
    }

    @Override
    public UserProfile createProviderAdminUser(BaseDetail<CreateProviderUserRequest> createProviderAdminUserRequestBaseDetail) {
        UserProfile userProfileCreate = new UserProfile();

        userProfileCreate.setFullName(createProviderAdminUserRequestBaseDetail.getDetail().getFullName());
        userProfileCreate.setBirthDate(createProviderAdminUserRequestBaseDetail.getDetail().getBirthDate());
        userProfileCreate.setIdentifyNumber(createProviderAdminUserRequestBaseDetail.getDetail().getIdentifyNumber());
        userProfileCreate.setGender(createProviderAdminUserRequestBaseDetail.getDetail().getGender());
        userProfileCreate.setAddress(createProviderAdminUserRequestBaseDetail.getDetail().getAddress());
        userProfileCreate.setPhoneNumber(createProviderAdminUserRequestBaseDetail.getDetail().getPhoneNumber());
        userProfileCreate.setEmail(createProviderAdminUserRequestBaseDetail.getDetail().getEmail());
        userProfileCreate.setUserName(createProviderAdminUserRequestBaseDetail.getDetail().getUserName());
        userProfileCreate.setCreateDate(new Date());
        userProfileCreate.setEnabled(createProviderAdminUserRequestBaseDetail.getDetail().getEnabled());

        return userProfileRepository.save(userProfileCreate);
    }

    @Override
    public UserProfile update(UserProfile userProfile, Map<String, Object> keyPairs) {
        for (String k : keyPairs.keySet()) {
            if (k.equals("email")) {
                userProfile.setEmail((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                userProfile.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("phoneNumber")) {
                userProfile.setPhoneNumber((String) keyPairs.get(k));
            }
            if (k.equals("address")) {
                userProfile.setAddress((String) keyPairs.get(k));
            }
            if (k.equals("gender")) {
                userProfile.setGender(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("identifyNumber")) {
                userProfile.setIdentifyNumber((String) keyPairs.get(k));
            }
            if (k.equals("birthDate")) {
                userProfile.setBirthDate((String) keyPairs.get(k));
            }
            if (k.equals("fullName")) {
                userProfile.setFullName((String) keyPairs.get(k));
            }
        }
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
