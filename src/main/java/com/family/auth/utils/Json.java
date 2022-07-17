/*
 * Copyright (c) 2018 - 2019. yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.text.SimpleDateFormat;

public final class Json {
    private static volatile ObjectMapper objectMapper;

    private Json() {
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (Json.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
                    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
                    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
                    objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
                }
            }
        }

        return objectMapper;
    }

    public static <T> T deserialize(String json, Class<T> type) {
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Throwable cause) {
            throw new IllegalArgumentException("deserialize json failure", cause);
        }
    }

    public static <T> T deserialize(String json, TypeReference<?> type) {
        try {
            return (T) getObjectMapper().readValue(json, type);
        } catch (Throwable cause) {
            throw new IllegalArgumentException("deserialize json failure", cause);
        }
    }

    public static String serialize(Object value) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (Throwable cause) {
            throw new IllegalArgumentException("serialize json failure", cause);
        }
    }
}