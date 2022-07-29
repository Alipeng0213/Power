package com.family.auth.security.core;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class OAuth2ResponseExceptionJackson1Serializer extends JsonSerializer<OAuth2Exception> {

    @Override
    public void serialize(OAuth2Exception value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        OAuth2ResponseException exception =  (OAuth2ResponseException) value;

        jgen.writeStartObject();
        jgen.writeNumberField("code", exception.getCode());
        String errorMessage = exception.getMessage();
        if (errorMessage != null) {
            errorMessage = HtmlUtils.htmlEscape(errorMessage);
        }
        jgen.writeStringField("message", errorMessage);
        jgen.writeEndObject();
    }

}
