package com.family.auth.security.oauth2;

import com.family.auth.core.ApiResult;
import com.family.auth.core.ApiResultFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;


public final class CustomAccessTokenJackson1Serializer extends SerializerBase<OAuth2AccessToken> {

        public CustomAccessTokenJackson1Serializer() {
            super(OAuth2AccessToken.class);
        }

        @Override
        public void serialize(OAuth2AccessToken token, JsonGenerator jgen, SerializerProvider provider) throws IOException,
                JsonGenerationException {
            jgen.writeStartObject();
            ApiResult result = ApiResultFactory.succeed(token);
            jgen.writeNumberField("code", HttpStatus.OK.value());
            jgen.writeStringField(OAuth2AccessToken.ACCESS_TOKEN, token.getValue());
            jgen.writeStringField(OAuth2AccessToken.TOKEN_TYPE, token.getTokenType());
            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null) {
                jgen.writeStringField(OAuth2AccessToken.REFRESH_TOKEN, refreshToken.getValue());
            }
            Date expiration = token.getExpiration();
            if (expiration != null) {
                jgen.writeNumberField(CustomAccessToken.EXPIRATION, expiration.getTime());
            }
            Set<String> scope = token.getScope();
            if (scope != null && !scope.isEmpty()) {
                StringBuffer scopes = new StringBuffer();
                for (String s : scope) {
                    Assert.hasLength(s, "Scopes cannot be null or empty. Got " + scope + "");
                    scopes.append(s);
                    scopes.append(" ");
                }
                jgen.writeStringField(OAuth2AccessToken.SCOPE, scopes.substring(0, scopes.length() - 1));
            }
            Map<String, Object> additionalInformation = token.getAdditionalInformation();
            for (String key : additionalInformation.keySet()) {
                jgen.writeObjectField(key, additionalInformation.get(key));
            }
            jgen.writeEndObject();
        }
    }