package com.family.auth.mvc.service;

import com.family.auth.model.User;
import com.family.auth.model.UserGroup;

import java.util.List;


public interface UserGroupService {

    List<UserGroup> getUserGroup(User user);

}
