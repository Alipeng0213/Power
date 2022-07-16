package com.family.auth.mvc.service.impl;

import com.family.auth.model.User;
import com.family.auth.mvc.mapper.UserMapper;
import com.family.auth.mvc.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DefaultUserServiceImpl implements UserService {

    @Resource
    UserMapper mapper;

    @Override
    public User getByUserName(String username) {
        return mapper.selectByUsername(username);
    }

}
