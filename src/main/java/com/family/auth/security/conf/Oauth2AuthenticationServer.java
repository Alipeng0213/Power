package com.family.auth.security.conf;


import com.family.auth.security.oauth2.JwtEnhancer;
import com.family.auth.security.oauth2.JwtStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

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

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //配置两个客户端,一个用于password认证一个用于client认证
        clients.inMemory().withClient("client_1")
                .resourceIds(DEMO_RESOURCE_ID)
                .authorizedGrantTypes((String[]) allGrantTypes.toArray())
                .scopes("select")
                .authorities("client")
                .secret("{noop}123456");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(jwtStore);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(new JwtEnhancer());
        tokenServices.setAccessTokenValiditySeconds(5 * 60 * 60 * 12);// token有效期设置
        tokenServices.setRefreshTokenValiditySeconds(5 * 60 * 60 * 12);// Refresh_token
        tokenServices.setAuthenticationManager(authenticationManager);

        endpoints.pathMapping("/oauth/token", "/connect/token")
                .tokenServices(tokenServices)
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }



}
