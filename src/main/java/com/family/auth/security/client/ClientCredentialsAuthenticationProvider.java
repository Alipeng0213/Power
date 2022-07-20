/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.client;


import com.family.auth.security.core.SystemCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import com.family.auth.model.System;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClientCredentialsAuthenticationProvider implements AuthenticationProvider {
    private final Map<String, String> clients = new HashMap<>();
    private final SystemCache systemCache;

    public ClientCredentialsAuthenticationProvider(SystemCache systemCache) {
        this.systemCache = systemCache;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientId = (String)authentication.getPrincipal();
        String clientSecret = (String)authentication.getCredentials();
        if(!StringUtils.hasText(clientSecret)) {
            throw new BadCredentialsException("must provide valid client secret");
        }
        if (!clients.containsKey(clientId)) {
            System system = systemCache.getSystem(clientId);
            if (system == null) {
                throw new BadCredentialsException("client: " + clientId + " not found.");
            }

            clients.put(clientId, system.getFclientSecret());
        }
        String correctClientSecret = clients.get(clientId);
        if (!clientSecret.equals(correctClientSecret)) {
            throw new BadCredentialsException("The client secret incorrect");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (ClientCredentialsAuthenticationToken.class.isAssignableFrom(authentication));
    }
}