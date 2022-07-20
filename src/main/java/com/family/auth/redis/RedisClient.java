package com.family.auth.redis;

import com.family.auth.security.token.Token;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.util.HashMap;
import java.util.Map;

public class RedisClient {

    public static final String REFRESH_TOKEN_KEY = "oauth2:grant:refresh_token:%s";

    public static final String ACCESS_TOKEN_KEY = "oauth2:grant:access_token:%s";

    private static final Map<String, Object> tokenCache = new HashMap<String, Object>();

    public static void put(String key, Object token) {
        tokenCache.put(key, token);
    }

    public static void putRefreshToken(String key, OAuth2RefreshToken refreshToken) {
        tokenCache.put(String.format(REFRESH_TOKEN_KEY, key), refreshToken);
    }


    public static Object getRefreshToken(String key) {
        return tokenCache.get(String.format(REFRESH_TOKEN_KEY, key));
    }

    public static void putAccessToken(String key, OAuth2AccessToken accessToken) {
        tokenCache.put(String.format(ACCESS_TOKEN_KEY, key), accessToken);
    }

    public static Object getAccessToken(String key) {
        return tokenCache.get(String.format(ACCESS_TOKEN_KEY, key));
    }


    public static void delete(String key) {
        tokenCache.remove(key);
    }

    public static Object get(String key) {
        return tokenCache.get(key);
    }

    public static String generateRedisKey(String name, String client_id) {
        return name + ":" + client_id;
    }

}
