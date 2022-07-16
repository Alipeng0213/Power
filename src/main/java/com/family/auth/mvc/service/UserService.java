package com.family.auth.mvc.service;

import com.family.auth.model.User;

public interface UserService {

    User getByUserName(String username);

}
