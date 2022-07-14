/*
 * Copyright (c) 2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.core;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PluginAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final UserPrincipal userPrincipal;
    private final Object credentials;

    public PluginAuthenticationToken(UserPrincipal userPrincipal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userPrincipal = userPrincipal;
        this.credentials = "PROTECTED";
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return this.userPrincipal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
