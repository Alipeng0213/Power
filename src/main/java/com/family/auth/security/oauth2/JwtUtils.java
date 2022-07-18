package com.family.auth.security.oauth2;


import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    public static final String SUBJECT = "Alipeng";

    public static final String APPSECRET = "6675CB7039B34EEBB5F0DD3B458F924B";

    /**
     * 生成jwt token
     *
     * @return
     */

    public static Token encode(Map<String, Object> map, long expire) {
        Claims claims = new DefaultClaims(map);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + expire));
        claims.setSubject(SUBJECT);
        String value = Jwts.builder().setSubject(SUBJECT)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, APPSECRET).compact();
        Token token = new Token(value, claims);
        return token;
    }

    /**
     * 校验jwt token
     *
     * @param token
     * @return
     */

    public static Token decode(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(token).getBody();
        return new Token(token, claims);
    }
}