/*
 * Copyright (c) 2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.client;

import com.family.auth.core.ApiResultException;
import com.family.auth.core.ApiResultFactory;
import com.family.auth.mvc.mapper.SystemMapper;
import com.family.auth.utils.Json;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import com.family.auth.model.System;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
public class OAuth2ClientDetailsService implements ClientDetailsService {
    private final List<String> allGrantTypes = Arrays.asList("authorization_code", "implicit", "password", "client_credentials", "refresh_token");
    private final List<String> defaultScopes = Arrays.asList("profile", "openid", "user_info", "lmcore");

    private final SystemCache systemCache;
    private final SystemMapper systemMapper;

    private static LoadingCache<String, ClientDetails> clients;

    public OAuth2ClientDetailsService(SystemCache systemCache, SystemMapper systemMapper) {
        this.systemCache = systemCache;
        this.systemMapper = systemMapper;

        buildingCache();
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        try {
            return clients.get(clientId);
        } catch (Throwable cause) {
            throw new ClientRegistrationException(String.format("cannot load client by clientId: %s", clientId), cause);
        }
    }

    private void buildingCache() {
        clients = CacheBuilder.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ClientDetails>() {
                    @Override
                    public ClientDetails load(String clientId) {
                        System system = systemCache.getSystem(clientId);
                        if (system == null) {
                            throw new ApiResultException(ApiResultFactory.badClientId(String.format("The provided client_id: '%s' not found.", clientId)));
                        }

                        List<String> redirectUrls = new ArrayList<>();
                        String json = system.getFredirectUrls();
                        if (StringUtils.hasText(json)) {
                            if (json.startsWith("[")) {
                                String[] items = Json.deserialize(json, String[].class);
                                for (String item : items) {
                                    if (StringUtils.hasText(item)) {
                                        redirectUrls.add(item);
                                    }
                                }
                            } else {
                                redirectUrls.add(json);
                            }
                        }

                        // using system url if have none
                        if (redirectUrls.isEmpty()) {
                            redirectUrls.add(system.getFurl());
                        }

                        List<String> scopes = new ArrayList<>();

                        // add default scopes
                        scopes.addAll(defaultScopes);

                        // add the configured scopes
                        /*List<SystemScope> systemScopes = systemScopeDao.findAll(system.getId());
                        if (systemScopes != null && !systemScopes.isEmpty()) {
                            scopes.addAll(systemScopes.stream().map(SystemScope::getScope).collect(Collectors.toList()));
                        }*/

                        return new ClientBuilder(clientId)
                                .secret(system.getFclientSecret())
                                .redirectUris(redirectUrls)
                                .authorizedGrantTypes(allGrantTypes)
                                .scopes(scopes)
                                .autoApprove(true)
                                .build();
                    }
                });
    }

    protected final class ClientBuilder {
        private final String clientId;

        private Collection<String> authorizedGrantTypes = new LinkedHashSet<>();

        private Collection<String> authorities = new LinkedHashSet<>();

        private Integer accessTokenValiditySeconds;

        private Integer refreshTokenValiditySeconds;

        private Collection<String> scopes = new LinkedHashSet<>();

        private Collection<String> autoApproveScopes = new HashSet<>();

        private String secret;

        private Set<String> registeredRedirectUris = new HashSet<>();

        private Set<String> resourceIds = new HashSet<>();

        private boolean autoApprove;

        private Map<String, Object> additionalInformation = new LinkedHashMap<>();

        private ClientDetails build() {
            BaseClientDetails result = new BaseClientDetails();
            result.setClientId(clientId);
            result.setAuthorizedGrantTypes(authorizedGrantTypes);
            result.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
            result.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
            result.setRegisteredRedirectUri(registeredRedirectUris);
            result.setClientSecret(secret);
            result.setScope(scopes);
            result.setAuthorities(AuthorityUtils.createAuthorityList(authorities.toArray(new String[authorities.size()])));
            result.setResourceIds(resourceIds);
            result.setAdditionalInformation(additionalInformation);
            if (autoApprove) {
                result.setAutoApproveScopes(scopes);
            } else {
                result.setAutoApproveScopes(autoApproveScopes);
            }
            return result;
        }

        public ClientBuilder resourceIds(String... resourceIds) {
            for (String resourceId : resourceIds) {
                this.resourceIds.add(resourceId);
            }
            return this;
        }

        public ClientBuilder redirectUris(List<String> redirectUrls) {
            for (String redirectUri : redirectUrls) {
                this.registeredRedirectUris.add(redirectUri);
            }
            return this;
        }

        public ClientBuilder authorizedGrantTypes(List<String> authorizedGrantTypes) {
            for (String grant : authorizedGrantTypes) {
                this.authorizedGrantTypes.add(grant);
            }
            return this;
        }

        public ClientBuilder accessTokenValiditySeconds(int accessTokenValiditySeconds) {
            this.accessTokenValiditySeconds = accessTokenValiditySeconds;
            return this;
        }

        public ClientBuilder refreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
            this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
            return this;
        }

        public ClientBuilder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public ClientBuilder scopes(List<String> scopes) {
            for (String scope : scopes) {
                this.scopes.add(scope);
            }
            return this;
        }

        public ClientBuilder authorities(String... authorities) {
            for (String authority : authorities) {
                this.authorities.add(authority);
            }
            return this;
        }

        public ClientBuilder autoApprove(boolean autoApprove) {
            this.autoApprove = autoApprove;
            return this;
        }

        public ClientBuilder autoApprove(String... scopes) {
            for (String scope : scopes) {
                this.autoApproveScopes.add(scope);
            }
            return this;
        }

        public ClientBuilder additionalInformation(Map<String, ?> map) {
            this.additionalInformation.putAll(map);
            return this;
        }

        public ClientBuilder additionalInformation(String... pairs) {
            for (String pair : pairs) {
                String separator = ":";
                if (!pair.contains(separator) && pair.contains("=")) {
                    separator = "=";
                }
                int index = pair.indexOf(separator);
                String key = pair.substring(0, index > 0 ? index : pair.length());
                String value = index > 0 ? pair.substring(index + 1) : null;
                this.additionalInformation.put(key, value);
            }
            return this;
        }

        private ClientBuilder(String clientId) {
            this.clientId = clientId;
        }
    }
}