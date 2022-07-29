package com.family.auth.security.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;


@Configuration
public class OAuth2ResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {


    @Override
    public ResponseEntity translate(Exception e) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<>(new OAuth2ResponseException(e.getMessage(), e), headers,
                HttpStatus.OK);
    }

}
