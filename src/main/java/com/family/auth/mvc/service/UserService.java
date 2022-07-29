package com.family.auth.mvc.service;

import com.family.auth.model.User;

public interface UserService {


    User getCurrentUser(String username);

    User getByUserName(String username);

}
