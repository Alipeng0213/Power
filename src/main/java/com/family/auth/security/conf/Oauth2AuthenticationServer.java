package com.family.auth.security.conf;


import com.family.auth.core.ExceptionNotifier;
import com.family.auth.security.client.ClientCredentialsTokenEndpointFilter;
import com.family.auth.security.client.OAuth2ClientDetailsService;
import com.family.auth.security.core.OAuth2ExceptionApiResultRenderer;
import com.family.auth.security.core.OAuth2ResponseExceptionTranslator;
import com.family.auth.security.core.OAuth2TokenService;
import com.family.auth.security.oauth2.JwtEnhancer;
import com.family.auth.security.oauth2.JwtStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAuthorizationServer
class Oauth2AuthenticationServer extends AuthorizationServerConfigurerAdapter {

    private final List<String> allGrantTypes = Arrays.asList("authorization_code", "implicit", "password", "client_credentials", "refresh_token");

    private static final String DEMO_RESOURCE_ID = "order";

    @Resource
    AuthenticationManager authenticationManager;

    @Resource
    JwtStore jwtStore;

    @Resource
    ExceptionNotifier exceptionNotifier;

    @Resource
    OAuth2ResponseExceptionTranslator exceptionTranslator;

    @Resource
    OAuth2ClientDetailsService oAuth2ClientDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(oAuth2ClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        OAuth2TokenService tokenServices = new OAuth2TokenService(jwtStore, new JwtEnhancer());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setAccessTokenValiditySeconds(2 * 60 * 60 * 1000);// token有效期设置
        tokenServices.setRefreshTokenValiditySeconds(24 * 60 * 60 * 1000);// Refresh_token
        tokenServices.setAuthenticationManager(authenticationManager);

        endpoints.pathMapping("/oauth/token", "/connect/token")
                .tokenServices(tokenServices)
                .authenticationManager(authenticationManager)
                .exceptionTranslator(exceptionTranslator)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");

        security.addTokenEndpointAuthenticationFilter(clientCredentialsTokenEndpointFilter());
    }

    ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter(){
        ClientCredentialsTokenEndpointFilter endpointFilter = new ClientCredentialsTokenEndpointFilter(authenticationManager, exceptionTranslator , exceptionNotifier);
        endpointFilter.afterPropertiesSet();
        return endpointFilter;
    }

}
