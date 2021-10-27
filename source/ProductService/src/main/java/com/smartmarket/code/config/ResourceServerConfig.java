package com.smartmarket.code.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthorizeRequestFilter customAuthorizeRequestFilter;

    @Autowired
    private CustomEntryPoint customEntryPoint;

    @Autowired
    ConfigurableEnvironment environment;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        congfig Oauth2 resoucre server decode jwt
        http
            .oauth2ResourceServer(
                    oauth2ResourceServer -> {
                        oauth2ResourceServer.jwt(
                                jwt -> jwt.decoder(jwtDecoder())
                        );
                        //Bản chất của việc này là add thêm một filter ở tầng filter
                        oauth2ResourceServer.authenticationEntryPoint(customEntryPoint);
                    }
            );

//        CSRF protection is enabled by default
        http.csrf().disable().authorizeRequests()
                .antMatchers("/actuator/*").permitAll();

        http.authorizeRequests()
                .anyRequest().authenticated().
                and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        http.addFilterAfter(customAuthorizeRequestFilter, FilterSecurityInterceptor.class);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] key = Base64.getDecoder().decode(environment.getRequiredProperty("publicKey"));

            X509EncodedKeySpec x509 = new X509EncodedKeySpec(key);
            PublicKey rsaKey = (RSAPublicKey) keyFactory.generatePublic(x509);
            return NimbusJwtDecoder.withPublicKey((RSAPublicKey) rsaKey).build();
        } catch (Exception e) {
            throw new RuntimeException("Wrong public key");
        }
    }

}