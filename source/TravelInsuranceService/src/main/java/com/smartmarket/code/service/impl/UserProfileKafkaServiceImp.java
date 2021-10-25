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
                userProfile.setGender(((Number)keyPairs.get(k)).intValue());
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
                userProfile.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userProfile.setCreateDate(formatter.parse(createAt));
            }
        }
        return userProfileRepository.save(userProfile);
    }

    public UserProfile updateUserProfileKafka(Map<String, Object> keyPairs) throws ParseException {
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
                userProfile.setGender(((Number)keyPairs.get(k)).intValue());
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
                userProfile.setEnabled(((Number)keyPairs.get(k)).intValue());
            }
            if (k.equals("create_date")) {
                String createAt = (String)keyPairs.get(k);
                userProfile.setCreateDate(formatter.parse(createAt));
            }
        }
        return userProfileRepository.save(userProfile);
    }


    public int deleteUserProfileKafka(String username) {
        return userProfileRepository.deleteUserProfileByUserName(username);
    }


    public int truncateUserProfileKafka() {
        return userProfileRepository.truncateUserProfileKafka();
    }

}
