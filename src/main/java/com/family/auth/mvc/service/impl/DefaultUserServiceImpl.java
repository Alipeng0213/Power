package com.family.auth.mvc.service.impl;

import com.family.auth.model.User;
import com.family.auth.model.UserGroup;
import com.family.auth.mvc.mapper.UserMapper;
import com.family.auth.mvc.service.UserGroupService;
import com.family.auth.mvc.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DefaultUserServiceImpl implements UserService {

    @Resource
    UserMapper mapper;

    @Resource
    UserGroupService userGroupService;

    @Override
    public User getCurrentUser(String username) {
        User user = mapper.selectByUsername(username);
        List<UserGroup> userGroups = userGroupService.getUserGroup(user);
        user.setUserGroups(userGroups);
        return user;
    }

    @Override
    public User getByUserName(String username) {
        return mapper.selectByUsername(username);
    }

}
