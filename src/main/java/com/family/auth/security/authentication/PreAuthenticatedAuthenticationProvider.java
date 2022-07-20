package com.family.auth.security.authentication;

import com.family.auth.model.User;
import com.family.auth.mvc.service.UserService;
import com.sun.security.auth.UserPrincipal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class PreAuthenticatedAuthenticationProvider implements AuthenticationProvider {
    private static final Log logger = LogFactory.getLog(PreAuthenticatedAuthenticationProvider.class);

    @Resource
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (this.supports(authentication.getClass())) {
            if (logger.isDebugEnabled()) {
                logger.debug("PreAuthenticated authentication request: " + authentication);
            }
            if (authentication.getPrincipal() == null) {
                logger.debug("No pre-authenticated principal found in request.");
            } else if (authentication.getCredentials() == null) {
                logger.debug("No pre-authenticated credentials found in request.");
            } else {
                PluginAuthenticationToken principal = (PluginAuthenticationToken) authentication.getPrincipal();
                UserPrincipal userPrincipal = principal.getPrincipal();
                User user = userService.getByUserName(userPrincipal.getName());
                if (user != null) {
                    PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(userPrincipal, authentication.getCredentials(), Collections.EMPTY_LIST);
                    result.setDetails(authentication.getDetails());
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
