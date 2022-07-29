package com.family.auth.security.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class OAuth2ResponseExceptionJackson2Serializer extends StdSerializer<OAuth2Exception> {

    public OAuth2ResponseExceptionJackson2Serializer() {
        super(OAuth2Exception.class);
    }

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
