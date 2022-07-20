package com.family.auth.core;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Data;
import java.io.IOException;


public final class ApiResultJackson2Serializer extends StdSerializer<ApiResult> {

    public ApiResultJackson2Serializer() {
        super(ApiResult.class);
    }

    @Override
    public void serialize(ApiResult result, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("code", result.getCode());
        jgen.writeStringField("message", result.getMessage());
        if(result.getData() != null) {
            jgen.writeObjectField("data", result.getData());
        }
        jgen.writeEndObject();
    }

}