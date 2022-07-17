package com.family.auth.security.oauth2;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = CustomAccessTokenJackson1Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = CustomAccessTokenJackson2Serializer.class)
public class CustomAccessToken extends DefaultOAuth2AccessToken {


    static final String EXPIRATION = "expiration";

    public CustomAccessToken(String value) {
        super(value);
    }

    public CustomAccessToken(OAuth2AccessToken accessToken) {
        super(accessToken);
    }

}
