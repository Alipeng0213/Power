/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.client;

import com.family.auth.core.ExceptionNotifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

@Configuration
public class ClientCredentialsTokenEndpointFilter extends AbstractAuthenticationProcessingFilter {
    private final static String CLIENT_ID = "client_id";
    private final static String CLIENT_SECRET = "client_secret";
    private AuthenticationEntryPoint authenticationEntryPoint;

    private String credentialsCharset = "UTF-8";
    private boolean allowOnlyPost = true;

    public ClientCredentialsTokenEndpointFilter(AuthenticationManager authenticationManager, WebResponseExceptionTranslator exceptionTranslator, ExceptionNotifier exceptionNotifier) {
        super("/connect/token");
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setTypeName("Form");
        authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.setAuthenticationManager(authenticationManager);
    }

    public void setAllowOnlyPost(boolean allowOnlyPost) {
        this.allowOnlyPost = allowOnlyPost;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            // no-op - just allow filter chain to continue to token endpoint
        });
        setAuthenticationFailureHandler((request, response, exception) -> {
            if (exception instanceof BadCredentialsException) {
                exception = new BadCredentialsException(exception.getMessage());
            }
            authenticationEntryPoint.commence(request, response, exception);
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String clientId;
        String clientSecret;
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Basic ")) {
            if (logger.isDebugEnabled()) {
                logger.debug("cannot found the 'Authorization' header or the Authorization header not start with 'Basic'");
                logger.debug("try to get client_id and client_secret from parameters");
            }

            // the old .NET version support this feature, so this code is for backward compatibility.
            clientId = request.getParameter(CLIENT_ID);
            clientSecret = request.getParameter(CLIENT_SECRET);
        } else {
            String[] tokens = extractAndDecodeHeader(header, request);
            assert tokens.length == 2;

            clientId = tokens[0];
            clientSecret = tokens[1];
        }

        logger.info("获取到的clientId:"+ clientId);

        if (clientId == null) {
            throw new BadCredentialsException("No client credentials presented");
        }

        // why not checking first? for logging the bad request client
        if (allowOnlyPost && !"POST".equalsIgnoreCase(request.getMethod())) {
            // known who client bad request
            logger.error("The client '" + clientId + "' not use POST method, '" + request.getMethod() + "' not supported");

            throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[]{"POST"});
        }

        if (clientSecret == null) {
            clientSecret = "";
        }

        if (logger.isDebugEnabled()) {
            this.logger.debug("Client Authentication Authorization header found for client '" + clientId + "'");
        }


        // If the request is already authenticated we can assume that this
        // filter is not needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication;
        }

        clientId = clientId.trim();
        ClientCredentialsAuthenticationToken authRequest = new ClientCredentialsAuthenticationToken(clientId,
                clientSecret, new ArrayList<>());

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public void setCredentialsCharset(String credentialsCharset) {
        Assert.hasText(credentialsCharset, "credentialsCharset cannot be null or empty");
        this.credentialsCharset = credentialsCharset;
    }

    protected String getCredentialsCharset(HttpServletRequest httpRequest) {
        return this.credentialsCharset;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, getCredentialsCharset(request));

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
