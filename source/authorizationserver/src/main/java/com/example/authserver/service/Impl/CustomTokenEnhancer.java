package com.example.authserver.service.Impl;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;


@Service
public class CustomTokenEnhancer implements TokenEnhancer {
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
		var token = new DefaultOAuth2AccessToken(oAuth2AccessToken);

		ArrayList<String> roles = new ArrayList<String>();
		roles.add("CUSTOMER");
		roles.add("ADMIN");
		roles.add("PROVIDER");
		roles.add("PROVIDER1");
		roles.add("PROVIDER3");
		roles.add("PROVIDER_ADMIN");

		Map<String, Object> info = Map.of("iss", "http://smartmarket.com","roles", roles);
		token.setAdditionalInformation(info);
		return token;
	}
}
