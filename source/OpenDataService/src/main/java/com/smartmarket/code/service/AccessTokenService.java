package com.smartmarket.code.service;


import com.smartmarket.code.model.AccessToken;

public interface AccessTokenService extends BaseService<AccessToken> {
    AccessToken findByUsername(String userName);
    int updateTokenByAccessTokenId(Long expireTime ,  Long issueTime ,
                                   String token,  Long id) ;
}
