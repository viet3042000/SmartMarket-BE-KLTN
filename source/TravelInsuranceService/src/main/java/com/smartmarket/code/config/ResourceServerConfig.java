package com.smartmarket.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

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

    @Value("${publicKey}")
    private String publicKey;

    @Autowired
    private CustomAuthorizeRequestFilter customAuthorizeRequestFilter;

    @Autowired
    private CustomLogFilter customLogFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(
                c -> c.jwt(
                        j -> j.decoder(jwtDecoder())
                )
        )
//                .exceptionHandling().accessDeniedHandler(new CustomOAuth2AccessDeniedHandler())
        ;

        http.authorizeRequests()
//                .mvcMatchers("/**").access("@webSecurity.checkURL(authentication)")
                .anyRequest().authenticated().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        http.addFilterBefore(customLogFilter, ChannelProcessingFilter.class);
        http.addFilterAfter(customAuthorizeRequestFilter, FilterSecurityInterceptor.class);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] key = Base64.getDecoder().decode(publicKey);

            X509EncodedKeySpec x509 = new X509EncodedKeySpec(key);
            PublicKey rsaKey = (RSAPublicKey) keyFactory.generatePublic(x509);
            return NimbusJwtDecoder.withPublicKey((RSAPublicKey) rsaKey).build();
        } catch (Exception e) {
            throw new RuntimeException("Wrong public key");
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
