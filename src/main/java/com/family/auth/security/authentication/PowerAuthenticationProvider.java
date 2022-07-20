package com.family.auth.security.authentication;


import com.alibaba.druid.util.StringUtils;
import com.family.auth.model.User;
import com.family.auth.mvc.service.UserService;
import com.sun.security.auth.UserPrincipal;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Resource;
import java.util.Collections;


@Configuration
public class PowerAuthenticationProvider implements AuthenticationProvider {

    @Resource
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        User user = userService.getByUserName(username);
        if (user == null){
            throw new BadCredentialsException("用户【" + username + "】不存在！");
        }
        if (!StringUtils.equals(password, user.getPassword())){
            throw new BadCredentialsException("用户【" + username + "】密码错误！");
        }

        UserPrincipal userPrincipal = new UserPrincipal(username);
        return new PluginAuthenticationToken(userPrincipal, Collections.EMPTY_LIST);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
