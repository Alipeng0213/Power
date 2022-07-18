package com.family.auth.security.conf;

import com.family.auth.core.ExceptionNotifier;
import com.family.auth.security.core.OAuth2ExceptionApiResultRenderer;
import com.family.auth.security.core.OAuth2ResponseExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.annotation.Resource;

@Configuration
@EnableResourceServer
public class Oauth2ResourceServer extends ResourceServerConfigurerAdapter {


    @Resource
    ExceptionNotifier exceptionNotifier;

    @Resource
    OAuth2ResponseExceptionTranslator exceptionTranslator;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().requestMatchers().anyRequest()
                .and().anonymous()
                .and().authorizeRequests()
                .anyRequest().authenticated()
        ;
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resource) throws Exception {
        resource.authenticationEntryPoint(authenticationEntryPoint());
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
        return authenticationEntryPoint;
    }
/*


    private AccessDeniedHandler accessDeniedHandler() {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionRenderer(exceptionRenderer());
        return accessDeniedHandler;
    }
    private OAuth2ExceptionRenderer exceptionRenderer() {
        return new OAuth2ExceptionApiResultRenderer(exceptionNotifier);
    }
*/

}
