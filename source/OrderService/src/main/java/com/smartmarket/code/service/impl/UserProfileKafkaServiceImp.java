package com.smartmarket.code.service.impl;

import com.smartmarket.code.dao.UserProfileRepository;
import com.smartmarket.code.model.UserProfile;
import com.smartmarket.code.service.UserProfileKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;

@Service
public class UserProfileKafkaServiceImp implements UserProfileKafkaService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile createUserProfileKafka(Map<String, Object> keyPairs) throws ParseException {
        UserProfile userProfile = new UserProfile();

        //convert string --> date with formart tương ứng
        //2021-08-27 17:21:52.132+07
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                userProfile.setId(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("user_name")) {
                userProfile.setUserName((String) keyPairs.get(k));
            }
            if (k.equals("email")) {
                userProfile.setEmail((String) keyPairs.get(k));
            }
            if (k.equals("phone_number")) {
                userProfile.setPhoneNumber((String) keyPairs.get(k));
            }
            if (k.equals("address")) {
                userProfile.setAddress((String) keyPairs.get(k));
            }
            if (k.equals("gender")) {
                userProfile.setGender(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("identify_number")) {
                userProfile.setIdentifyNumber((String) keyPairs.get(k));
            }
            if (k.equals("birth_date")) {
                userProfile.setBirthDate((String) keyPairs.get(k));
            }
            if (k.equals("full_name")) {
                userProfile.setFullName((String) keyPairs.get(k));
            }
            if (k.equals("enabled")) {
                userProfile.setEnabled(((Number)keyPairs.get(k)).longValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userProfile.setCreateDate(formatter.parse(createAt));
            }
        }
        return userProfileRepository.save(userProfile);
    }

    public int updateUserProfileKafka(Map<String, Object> keyPairs) throws ParseException{
        Long id = 0L;
        String userName="";
        String email="";
        Long enabled =0L;
        String phoneNumber="";
        String address="";
        Long gender=0L;
        String identifyNumber = "";
        String birthDate="";
        String fullName="";

        //convert string --> date with formart tương ứng
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (String k : keyPairs.keySet()) {
            if (k.equals("id")) {
                id = ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("user_name")) {
                userName = (String) keyPairs.get(k);
            }
            if (k.equals("email")) {
                email = (String) keyPairs.get(k);
            }
            if (k.equals("phone_number")) {
                phoneNumber = (String) keyPairs.get(k);
            }
            if (k.equals("address")) {
                address = (String) keyPairs.get(k);
            }
            if (k.equals("gender")) {
                gender= ((Number)keyPairs.get(k)).longValue();
            }
            if (k.equals("identify_number")) {
                identifyNumber = (String) keyPairs.get(k);
            }
            if (k.equals("birth_date")) {
                birthDate = (String) keyPairs.get(k);
            }
            if (k.equals("full_name")) {
                fullName = (String) keyPairs.get(k);
            }
            if (k.equals("enabled")) {
                enabled = ((Number)keyPairs.get(k)).longValue();
            }
        }
        return userProfileRepository.updateUserProfileKafka(userName,email,gender, enabled,id, phoneNumber,
                address,identifyNumber, birthDate,fullName);
    }


    public int deleteUserProfileKafka(Long id) {
        return userProfileRepository.deleteUserProfileById(id);
    }


    public int truncateUserProfileKafka() {
        return userProfileRepository.truncateUserProfileKafka();
    }

}
