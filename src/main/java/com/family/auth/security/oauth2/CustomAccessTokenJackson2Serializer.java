package com.family.auth.security.oauth2;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

public final class CustomAccessTokenJackson2Serializer extends StdSerializer<OAuth2AccessToken> {

    public CustomAccessTokenJackson2Serializer() {
        super(OAuth2AccessToken.class);
    }

    @Override
    public void serialize(OAuth2AccessToken token, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        CustomAccessToken customAccessToken = (CustomAccessToken) token;

        ResponseEntity responseEntity = ResponseEntity.builder()
                .access_token(customAccessToken.getValue())
                .token_type(customAccessToken.getTokenType())
                .user(customAccessToken.getUser())
                .client_id(customAccessToken.getClient_id())
                .build();
        OAuth2RefreshToken refreshToken = customAccessToken.getRefreshToken();
        if (refreshToken != null) {
            responseEntity.setRefresh_token(refreshToken.getValue());
        }

        Date expiration = customAccessToken.getExpiration();
        if (expiration != null) {
            responseEntity.setExpiration(expiration.getTime());
        }

        Set<String> scope = customAccessToken.getScope();
        if (scope != null && !scope.isEmpty()) {
            StringBuffer scopes = new StringBuffer();
            for (String s : scope) {
                Assert.hasLength(s, "Scopes cannot be null or empty. Got " + scope + "");
                scopes.append(s);
                scopes.append(" ");
            }
            responseEntity.setScope(scopes.substring(0, scopes.length() - 1));
        }

        jgen.writeNumberField("code", HttpStatus.OK.value());
        jgen.writeObjectField("data", responseEntity);

        jgen.writeEndObject();
    }

    @Data
    @Builder
    static class ResponseEntity {

        String access_token;

        String token_type;

        String refresh_token;

        long expiration;

        String scope;

        String client_id;

        Object user;

    }
}