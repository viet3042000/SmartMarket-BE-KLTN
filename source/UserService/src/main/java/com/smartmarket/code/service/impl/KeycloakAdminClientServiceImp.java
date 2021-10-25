//package com.smartmarket.code.service.impl;
//
//import com.smartmarket.code.model.User;
//import com.smartmarket.code.service.KeycloakAdminClientService;
//import com.smartmarket.code.service.KeycloakConfigService;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.resource.UsersResource;
//
//import org.keycloak.representations.idm.CredentialRepresentation;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.ws.rs.core.Response;
//import java.util.Collections;
//import java.util.List;
//
//@Service
//public class KeycloakAdminClientServiceImp implements KeycloakAdminClientService {
//
//    @Autowired
//    KeycloakConfigService keycloakConfigService;
//
////    Make sure the admin account is created under 'myrealm'.
////    You cannot use the default admin account (master realm) to create the user for 'myrealm'
//    public void addUser(User user, String password){
//        UsersResource usersResource = keycloakConfigService.getInstance().realm(keycloakConfigService.realm).users();
//        CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
//
//        UserRepresentation kcUser = new UserRepresentation();
//        kcUser.setUsername(user.getUserName());
//        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
//        kcUser.setEmail(user.getEmail());
//        kcUser.setEnabled(true);
//        kcUser.setEmailVerified(false);
//        kcUser.setRequiredActions(null);
//        Response responseKeycloak= usersResource.create(kcUser);
//        responseKeycloak.close();
//    }
//
//    public void changePassword(User user, String password){
//        UsersResource usersResource = keycloakConfigService.getInstance().realm(keycloakConfigService.realm).users();
//        CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
//        List<UserRepresentation> userRepresentationList = usersResource.search(user.getUserName(),true);
//        UserRepresentation kcUser = userRepresentationList.get(0);
//        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
//        usersResource.get(kcUser.getId()).update(kcUser);
//    }
//
//    public List inquiryUser(String username){
//        UsersResource usersResource = keycloakConfigService.getInstance().realm(keycloakConfigService.realm).users();
//        List<UserRepresentation> userRepresentationList = usersResource.search(username,true);
//        return userRepresentationList;
//    }
//
//    public void deleteUser(String username){
//        UsersResource usersResource = keycloakConfigService.getInstance().realm(keycloakConfigService.realm).users();
//        List<UserRepresentation> userRepresentationList = usersResource.search(username,true);
//        UserRepresentation kcUser = userRepresentationList.get(0);
//        usersResource.delete(kcUser.getId());
//    }
//
//    private static CredentialRepresentation  createPasswordCredentials(String password) {
//        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
//        passwordCredentials.setTemporary(false);
//        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
//        passwordCredentials.setValue(password);
//        return passwordCredentials;
//    }
//}
