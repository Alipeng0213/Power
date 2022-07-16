package com.family.auth.mvc.controller;

import com.family.auth.model.User;
import com.family.auth.mvc.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping
    public User getProduct(@PathVariable String username) {
        return userService.getByUserName(username);
    }



}
