package com.family.auth.security.core;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.*;

@org.codehaus.jackson.map.annotate.JsonSerialize(using = OAuth2ResponseExceptionJackson1Serializer.class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = OAuth2ResponseExceptionJackson2Serializer.class)
public class OAuth2ResponseException extends OAuth2Exception {

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OAuth2ResponseException(String msg, Throwable t) {
        super(msg, t);
        this.code = HttpStatus.UNAUTHORIZED.value();
    }

    public OAuth2ResponseException(String msg) {
        super(msg);
        this.code = HttpStatus.UNAUTHORIZED.value();
    }

}
