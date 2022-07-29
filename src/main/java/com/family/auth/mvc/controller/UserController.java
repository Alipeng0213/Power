package com.family.auth.mvc.controller;

import com.family.auth.model.User;
import com.family.auth.mvc.dto.DtoMapper;
import com.family.auth.mvc.dto.UserDto;
import com.family.auth.mvc.service.UserService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/me")
    public UserDto getCurrentUser(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        User currentUser = userService.getCurrentUser(request.getUserPrincipal().getName());
        return DtoMapper.getUserDto(currentUser);
    }


}
