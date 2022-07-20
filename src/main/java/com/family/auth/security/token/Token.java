package com.family.auth.security.token;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class Token {

    TokenKind kind;

    Claims claims;

    String encoded;

    public Token(String encoded, Claims claims) {
        this.encoded = encoded;
        this.claims = claims;
        kind = TokenKind.USER;
    }

}
