package com.family.auth.security.token;

import com.alibaba.druid.util.StringUtils;
import com.family.auth.redis.RedisClient;
import com.family.auth.security.authentication.PluginAuthenticationToken;
import com.sun.security.auth.UserPrincipal;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import java.util.*;

//@Component
public class JwtStore implements TokenStore {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken accessTokentoken) {
        Token token = (Token) accessTokentoken.getAdditionalInformation().getOrDefault(Constant.TOKEN, null);
        if (token != null) {
            return convert(token);
        }
        return readAuthentication(accessTokentoken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String encoded) {
        try {
            Token token = JwtUtils.decode(encoded);
            return convert(token);
        } catch (Exception cause) {
            logger.error(cause.getMessage());
            return null;
        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(String encoded) {
        try {
            Token token = JwtUtils.decode(encoded);
            String name = token.getClaims().getSubject();
            String client_id = token.getClaims().get(Constant.CLIENT_ID, String.class);
            OAuth2AccessToken accessToken = (OAuth2AccessToken) RedisClient.getAccessToken(RedisClient.generateRedisKey(name, client_id));
            if(StringUtils.equals(accessToken.getValue(), encoded)) {
                return accessToken;
            }
            throw new InvalidTokenException("invalid token");
        } catch (Exception cause) {
            logger.error(cause.getMessage());
            return null;
        }
    }

    private OAuth2Authentication convert(Token token) {
        Claims claims = token.getClaims();
        Map<String, String> parameters = new HashMap<>();

        String clientId = (String) claims.get(Constant.CLIENT_ID);
        parameters.put(Constant.CLIENT_ID, clientId);
        OAuth2Request request = new OAuth2Request(parameters, clientId, Collections.emptyList(), true, Collections.emptySet(), Collections.emptySet(), null, null, null);
        if (token.getKind() == TokenKind.USER) {
            UserPrincipal principal = new UserPrincipal(claims.getSubject());
            PluginAuthenticationToken authResult = new PluginAuthenticationToken(principal, Collections.emptyList());
            return new OAuth2Authentication(request, authResult);
        } else {
            return new OAuth2Authentication(request, null);
        }
    }

    //region methods not need to impls
    @Override
    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Token token = JwtUtils.decode(accessToken.getValue());
        String name = token.getClaims().getSubject();
        String client_id = token.getClaims().get(Constant.CLIENT_ID, String.class);
        RedisClient.putAccessToken(RedisClient.generateRedisKey(name, client_id), accessToken);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        Token token = JwtUtils.decode(tokenValue);
        return new DefaultExpiringOAuth2RefreshToken(token.getEncoded(), token.getClaims().getExpiration());
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        Token refresh_token = JwtUtils.decode(token.getValue());
        Claims claims = refresh_token.getClaims();
        Map<String, String> parameters = new HashMap<>();
        String name =  claims.getSubject();
        String clientId = claims.get(Constant.CLIENT_ID, String.class);
        parameters.put(Constant.USER, name);
        OAuth2Request request = new OAuth2Request(parameters, clientId, Collections.emptyList(), true, Collections.emptySet(), Collections.emptySet(), null, null, null);
        if (refresh_token.getKind() == TokenKind.USER) {
            UserPrincipal principal = new UserPrincipal(claims.getSubject());
            PluginAuthenticationToken authResult = new PluginAuthenticationToken(principal, Collections.emptyList());
            return new OAuth2Authentication(request, authResult);
        } else {
            return new OAuth2Authentication(request, null);
        }
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        Token token = JwtUtils.decode(refreshToken.getValue());
        String name = token.getClaims().getSubject();
        String client_id = token.getClaims().get(Constant.CLIENT_ID, String.class);
        RedisClient.delete(RedisClient.generateRedisKey(name, client_id));
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        // We don't want to accidentally issue a token, and we have no way to reconstruct the refresh token
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username) {
        return Collections.emptySet();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return Collections.emptySet();
    }
    //endregion
}
