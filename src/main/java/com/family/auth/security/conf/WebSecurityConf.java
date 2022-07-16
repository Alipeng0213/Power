package com.family.auth.security.conf;

import com.family.auth.core.ExceptionNotifier;
import com.family.auth.security.client.ClientCredentialsAuthenticationProvider;
import com.family.auth.security.core.OAuth2ExceptionApiResultRenderer;
import com.family.auth.security.core.PowerAuthenticationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author hy
 * @Date 2022/7/10
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConf extends WebSecurityConfigurerAdapter {

    private static final String[] PERMIT_PATHS = {
            "/oauth/**",
            "/login**",
            "/sign/**",
            "/logout/**"
    };



    private final static String LOGIN_PAGE = "/sign/login";

    @Resource
    PowerAuthenticationProvider powerAuthenticationProvider;
    @Resource
    ClientCredentialsAuthenticationProvider clientCredentialsAuthenticationProvider;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    ExceptionNotifier exceptionNotifier;


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable().cors()
                .and().requestMatchers().anyRequest()
                .and().authorizeRequests().antMatchers("/oauth/*").permitAll()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().cacheControl();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
        // @formatter:on
    }

    /**
     * 认证信息管理
     * spring5中摒弃了原有的密码存储格式，官方把spring security的密码存储格式改了
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(powerAuthenticationProvider)
                .authenticationProvider(clientCredentialsAuthenticationProvider);
    }


    private AccessDeniedHandler accessDeniedHandler() {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionRenderer(exceptionRenderer());
        return accessDeniedHandler;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionRenderer(exceptionRenderer());
        return authenticationEntryPoint;
    }

    private OAuth2ExceptionRenderer exceptionRenderer() {
        return new OAuth2ExceptionApiResultRenderer(exceptionNotifier);
    }


}
