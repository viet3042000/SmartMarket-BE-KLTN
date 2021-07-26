package com.smartmarket.code.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.code.dao.AccessTokenRepository;
import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.AccessToken;
import com.smartmarket.code.model.AccessUser;
import com.smartmarket.code.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AccessTokenServiceImp implements AccessTokenService {
    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public AccessToken create(AccessToken object) {
        return  accessTokenRepository.save(object);
    }

    @Override
    public AccessToken update(AccessToken object) {
        AccessToken accessToken = accessTokenRepository.findById(object.getId()).orElse(null);
        accessToken.setIssueTime(object.getIssueTime());
        accessToken.setExpireTime(object.getExpireTime());
        accessToken.setToken(object.getToken());

        return accessTokenRepository.save(accessToken);
    }

    @Override
    public AccessToken delete(Long id) {
        AccessToken accessToken = accessTokenRepository.findById(id).orElse(null);
        if(accessToken != null){
        	accessTokenRepository.delete(accessToken);
        }
        return accessToken;
    }

    @Override
    public AccessToken getDetail(Long id) {
        return accessTokenRepository.findById(id).get();
    }

    @Override
    public AccessToken findByUsername(String userName) {
        AccessToken accessToken = accessTokenRepository.findByUsername(userName) ;
        return accessToken ;
    }

    @Override
    public int updateTokenByAccessTokenId(Long expireTime, Long issueTime, String token, Long id) {
        return accessTokenRepository.updateTokenByAccessTokenId(expireTime,issueTime,token,id);
    }


    //Cache
    @Cacheable(cacheNames = "accesstoken", key = "#userNameCache")
    public AccessToken createCache(String userNameCache, String token, Long timeIssue ,Long timeExpire) {
        AccessToken accessTokenCache = new AccessToken();
        accessTokenCache.setUserName(userNameCache);
        accessTokenCache.setIssueTime(timeIssue);
        accessTokenCache.setExpireTime(timeExpire);
        accessTokenCache.setToken(token);
        return accessTokenCache;
    }


    @CachePut(cacheNames = "accesstoken", key = "#userNameCache")
    public AccessToken updateCache(String userNameCache, String token , Long timeIssue ,Long timeExpire) {
        AccessToken accessTokenCache = new AccessToken();

        accessTokenCache.setUserName(userNameCache);
        accessTokenCache.setIssueTime(timeIssue);
        accessTokenCache.setExpireTime(timeExpire);
        accessTokenCache.setToken(token);

        return accessTokenCache;
    }
}
