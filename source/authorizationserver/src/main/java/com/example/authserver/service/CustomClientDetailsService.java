package com.example.authserver.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import com.example.authserver.entities.ClientEntity;
import com.example.authserver.repository.ClientRepository;

@Service
public class CustomClientDetailsService implements ClientDetailsService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		ClientEntity client = clientRepository.findByClientId(clientId);
		
	    var baseClientDetails = new BaseClientDetails();                   
	    baseClientDetails.setClientId(client.getClientId());                           
	    baseClientDetails.setClientSecret(client.getClientSecret());                       
	    baseClientDetails.setAuthorizedGrantTypes(List.of(client.getAuthorizedGrantTypes().split("##")));
	    baseClientDetails.setRegisteredRedirectUri(Set.of(client.getRedirectUri()));
	    baseClientDetails.setScope(List.of("read"));
	    baseClientDetails.setAccessTokenValiditySeconds(client.getExpirationTime());
	    return baseClientDetails;
	  }
}
