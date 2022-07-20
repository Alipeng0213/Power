/*
 * Copyright (c) 2018-2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.token;

import com.family.auth.security.core.CustomAccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.*;

public class JwtEnhancer extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        CustomAccessToken resultAccessToken = new CustomAccessToken(accessToken);
        Token token = convert(accessToken, authentication);
        Authentication user = authentication.getUserAuthentication();
        resultAccessToken.setUser(user.getPrincipal());
        resultAccessToken.setClient_id(token.getClaims().get(Constant.CLIENT_ID, String.class));
        resultAccessToken.setValue(token.getEncoded());
        return resultAccessToken;
    }

    public Token convert(OAuth2AccessToken token, OAuth2Authentication authentication) {
        OAuth2Request clientToken = authentication.getOAuth2Request();
        Map<String, Object> map = new HashMap<>(token.getAdditionalInformation());
        map.put(Constant.CLIENT_ID, clientToken.getClientId());
        return JwtUtils.encode(map, token.getExpiresIn());
    }
}