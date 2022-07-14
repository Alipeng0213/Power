package com.family.auth.security.oauth2;


import io.jsonwebtoken.*;

import java.util.Date;
import java.util.Map;

public class JwtUtils {

    public static final String SUBJECT = "Alipeng";

    public static final String APPSECRET = "asdasdqwe123";

    public static final long EXPIRE = 1000 * 60 * 30; //过期时间，毫秒，30分钟

    /**
     * 生成jwt token
     *
     * @return
     */

    public static String encode(String clientId) {
        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("id", 123)
                .setIssuedAt(new Date())
                .claim("clientId", clientId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, APPSECRET).compact();
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