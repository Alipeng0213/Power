/*
 * Copyright (c) 2018 - 2019. yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.security.core;

import com.family.auth.model.System;
import com.family.auth.mvc.mapper.SystemMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SystemCache {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LoadingCache<String, System> systemCache;
    private final SystemMapper systemMapper;

    public SystemCache(SystemMapper systemMapper) {
        this.systemMapper = systemMapper;
        this.systemCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, System>() {
                    @Override
                    public System load(String clientId) {
                        return systemMapper.findByClientId(clientId);
                    }
                });
    }

    public System getSystem(String clientId) {
        try {
            return systemCache.get(clientId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return systemMapper.findByClientId(clientId);
        }
    }


    public void invalidate(String clientId) {
        systemCache.invalidate(clientId);
    }
}
