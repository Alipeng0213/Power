package com.family.auth.core;

import lombok.Data;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import java.io.IOException;


public final class ApiResultJackson1Serializer extends SerializerBase<ApiResult> {

    public ApiResultJackson1Serializer() {
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