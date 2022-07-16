package com.family.auth.security.exception;

import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class OAuth2ResponseException extends OAuth2Exception {

    @Getter
    private String dataMsg;

    public OAuth2ResponseException(String msg) {
        super(msg);
    }

    public OAuth2ResponseException(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuth2ResponseException(String msg, String dataMsg) {
        super(msg);
        this.dataMsg = dataMsg;

    }

}