package com.family.auth.security.token;

import com.family.auth.security.authentication.PluginAuthenticationToken;
import com.sun.security.auth.UserPrincipal;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
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
            Claims claims = token.getClaims();
            DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(encoded);
            accessToken.setExpiration(claims.getExpiration());
            Map<String, Object> info = new HashMap<>(claims);
            info.put(Constant.TOKEN, token);
            accessToken.setAdditionalInformation(info);
            return accessToken;
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
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {

        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        // Approvals (if any) should only be removed when Refresh Tokens are removed (or expired)
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
