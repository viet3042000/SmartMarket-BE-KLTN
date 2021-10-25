package com.example.authserver.config;

import java.util.List;

import com.example.authserver.service.Impl.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig
        extends AuthorizationServerConfigurerAdapter {

    @Value("${password}")
    private String password;

    @Value("${privateKey}")
    private String privateKey;

    @Value("${alias}")
    private String alias;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
	private ClientDetailsService customClientDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	clients.withClientDetails(customClientDetailsService).jdbc().passwordEncoder(bCryptPasswordEncoder);
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
       
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		var tokenEnhancers = List.of(new CustomTokenEnhancer(), jwtAccessTokenConverter());
		tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
		
		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
				.tokenEnhancer(tokenEnhancerChain);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        var converter = new JwtAccessTokenConverter();

        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        new ClassPathResource(privateKey),
                        password.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(alias));

        return converter;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("isAuthenticated()");
    }
}
