package com.family.auth.security.core;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = CustomAccessTokenJackson1Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = CustomAccessTokenJackson2Serializer.class)
public class CustomAccessToken extends DefaultOAuth2AccessToken {

    private String client_id;

    private Object user;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public CustomAccessToken(String value) {
        super(value);
    }

    public CustomAccessToken(OAuth2AccessToken accessToken) {
        super(accessToken);
    }

}
