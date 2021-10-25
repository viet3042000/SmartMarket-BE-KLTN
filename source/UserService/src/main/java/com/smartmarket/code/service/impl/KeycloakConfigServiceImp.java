//package com.smartmarket.code.service.impl;
//
//import com.smartmarket.code.service.KeycloakConfigService;
//import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
//import org.keycloak.OAuth2Constants;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KeycloakConfigServiceImp implements KeycloakConfigService {
//    static Keycloak keycloak = null;
//
//    public Keycloak getInstance(){
//        if(keycloak == null){
//
//            keycloak = KeycloakBuilder.builder()
//                    .serverUrl(serverUrl)
//                    .realm(realm)
//                    .grantType(OAuth2Constants.PASSWORD)
//                    .username(userName)
//                    .password(password)
//                    .clientId(clientId)
////                    .clientSecret(clientSecret)
//                    .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
//                    .build();
//        }
//        return keycloak;
//    }
//}
