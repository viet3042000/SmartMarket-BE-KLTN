package com.smartmarket.code.config;

import com.smartmarket.code.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.NimbusAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)

public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private CustomEntryPoint customEntryPoint;


    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
//    accessTokenResponseClient() {
//
////        return new NimbusAuthorizationCodeTokenResponseClient();
//        return new DefaultAuthorizationCodeTokenResponseClient();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = new JwtIssuerAuthenticationManagerResolver
                ("https://accounts.google.com");

        http
            .oauth2ResourceServer(oauth2 -> oauth2
                    .authenticationManagerResolver(authenticationManagerResolver)
                    .authenticationEntryPoint(customEntryPoint)
            );


        //http://localhost:8085/oauth2/authorization/google
        http.csrf().disable().authorizeRequests()
                .antMatchers("/beforelogin").permitAll()
                .and()

                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

//                .formLogin().permitAll()
//                .and()

                .oauth2Login()
                .authorizationEndpoint()
//                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()

//                .redirectionEndpoint()
//                    .baseUri("/oauth2/callback/*")
//                .and()

                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                .and()

//                .tokenEndpoint()
//                    .accessTokenResponseClient(accessTokenResponseClient())
//                .and()

                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

    }

}
