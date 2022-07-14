/*
 * Copyright (c) 2018-2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.oauth2;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.LinkedHashMap;
import java.util.Map;

public class JwtEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        Map<String, Object> additional = new LinkedHashMap<>(accessToken.getAdditionalInformation());

        OAuth2Request request = authentication.getOAuth2Request();
        additional.put(Constant.CLIENT_ID, request.getClientId());
        result.setAdditionalInformation(additional);
        result.setRefreshToken(null);
        result.setValue(convert(accessToken, authentication));

        return result;
    }

    public String convert(OAuth2AccessToken token, OAuth2Authentication authentication) {
        OAuth2Request clientToken = authentication.getOAuth2Request();

        /*Header header = new Header();
        header.setTtl(token.getExpiresIn());
        header.setIssuedAt(Instant.now().toEpochMilli());
        Payload payload = new Payload();
        Map<String, Object> attachment = new LinkedHashMap<>(token.getAdditionalInformation());

        // client_id for indicating issue to who
        attachment.put(WellKnown.clientId, clientToken.getClientId());

        if (authentication.isClientOnly()) {
            header.setKind(TokenKind.CLIENT);
            header.setSubject(clientToken.getClientId());
        } else {
            header.setSubject(authentication.getUserAuthentication().getName());
            // for backward compatibility
            attachment.put(WellKnown.username, authentication.getName());
        }

        payload.setScopes(token.getScope());
        payload.setAttachment(attachment);*/

        return JwtUtils.encode(clientToken.getClientId());
    }
}