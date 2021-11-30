package com.smartmarket.code.security;

import com.smartmarket.code.request.BaseDetail;
import com.smartmarket.code.request.CreateUserRequest;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    JavaMailSender javaMailSender;

//    @Autowired
//    BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Autowired
//    private UserRepository userRepository;

//    fetch the user’s details from the OAuth2 provider, is called after an access token is obtained from the OAuth2 provider
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
//
//        try {
//            return processOAuth2User(oAuth2UserRequest, oAuth2User);
//        } catch (AuthenticationException ex) {
//            throw ex;
//        } catch (Exception ex) {
//            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
//            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
//        }
//    }


//    fetch the user’s details from the OAuth2 provider, is called after an access_token is obtained from the OAuth2 provider
    @SneakyThrows
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  null;
        try{
            user =  super.loadUser(userRequest);
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), user.getAttributes());

            CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setRole("CUSTOMER");
            createUserRequest.setUserName(oAuth2UserInfo.getEmail());
            createUserRequest.setPassword(this.generatePassayPassword());
            createUserRequest.setEmail(oAuth2UserInfo.getEmail());
            createUserRequest.setEnabled(1);
            createUserRequest.setFullName(oAuth2UserInfo.getName());
            createUserRequest.setProvider(userRequest.getClientRegistration().getRegistrationId());

            BaseDetail<CreateUserRequest> createUserRequestBaseDetail = new BaseDetail<>();
            createUserRequestBaseDetail.setRequestId(UUID.randomUUID().toString());
            createUserRequestBaseDetail.setRequestTime("test");
            createUserRequestBaseDetail.setTargetId("userservice");
            createUserRequestBaseDetail.setDetail(createUserRequest);

            //SET TIMEOUT
            //set Time out get create api BIC
            SimpleClientHttpRequestFactory clientHttpRequestFactoryCreateBIC = new SimpleClientHttpRequestFactory();
            //Connect timeout
            clientHttpRequestFactoryCreateBIC.setConnectTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));
            //Read timeout
            clientHttpRequestFactoryCreateBIC.setReadTimeout(Integer.parseInt(environment.getRequiredProperty("timeout.api.createTravelBIC")));

            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactoryCreateBIC);

            HttpEntity<BaseDetail> entity = new HttpEntity<BaseDetail>(createUserRequestBaseDetail);
//            ResponseEntity<String> jsonResultCreateUser = restTemplate.exchange("http://localhost:8081/user/user-service/v1/register-user", HttpMethod.POST, entity, String.class);
            ResponseEntity<String> jsonResultCreateUser = restTemplate.exchange("http://10.14.101.202:31441/dev/user/user-service/v1/register-user", HttpMethod.POST, entity, String.class);

            if (jsonResultCreateUser.getStatusCodeValue() == 200) {
//                //send password to email
//                SimpleMailMessage message = new SimpleMailMessage();
//                message.setFrom("viet3042000@gmail.com");
//                message.setSubject("Your Password");
//                message.setTo(oAuth2UserInfo.getEmail());
//                message.setText(userCreate.getPassword());
//                javaMailSender.send(message);
//
                return new CustomOAuth2User(user);
            } else {
                throw new Exception();
            }
        }catch (HttpClientErrorException ex){
            String detailErrorMessage = ex.getResponseBodyAsString();
            JSONObject detail = new JSONObject(detailErrorMessage);
            if(detail.getString("detailErrorMessage").equals("UserName has already existed")){
                return new CustomOAuth2User(user);
            }else {
                throw ex;
            }
        }catch (Exception ex){
            throw ex;
        }
    }

    public String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return this.getErrorCode();
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }


//    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
//        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
//
//        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
//            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
//        }
//
//        Optional<User> userOptional = userRepository.findByUsername(oAuth2UserInfo.getEmail());
//        User user;
//        if(userOptional.isPresent()) {
//            user = userOptional.get();
//            if(!user.getProvider().equals(oAuth2UserRequest.getClientRegistration().getRegistrationId())) {
//                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
//                        user.getProvider() + " account. Please use your " + user.getProvider() +
//                        " account to login.");
//            }
//            user = updateExistingUser(user, oAuth2UserInfo);
//        } else {
//            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
//        }
//
//        return UserPrincipal.create(user, oAuth2User.getAttributes());
//    }
//
//    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
//        User user = new User();
//
//        user.setProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
//        user.setEmail(oAuth2UserInfo.getEmail());
//        user.setUserName(oAuth2UserInfo.getEmail());
//        user.setEnabled(1);
//        return userRepository.save(user);
//    }
//
//    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
////        existingUser.setName(oAuth2UserInfo.getName());
////        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
//        return userRepository.save(existingUser);
//    }

}