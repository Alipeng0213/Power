package com.family.auth.security.oauth2;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = CustomAccessTokenJackson1Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = CustomAccessTokenJackson2Serializer.class)
public class CustomAccessToken extends DefaultOAuth2AccessToken {

    private String client_id;

    private Object user;

    protected String getClient_id() {
        return client_id;
    }

    protected void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    protected Object getUser() {
        return user;
    }

    protected void setUser(Object user) {
        this.user = user;
    }

    public CustomAccessToken(String value) {
        super(value);
    }

    public CustomAccessToken(OAuth2AccessToken accessToken) {
        super(accessToken);
    }

}
