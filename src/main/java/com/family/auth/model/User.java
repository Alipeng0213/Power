package com.family.auth.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {

    private Integer userId;

    private String username;

    private String nickname;

    private String qq;

    private String wechat;

    private String fullName;

    private String email;

    private String phone;

    private String password;

    private String sign;

    private Integer status;

    private String avatarUrl;

    private String createBy;

    private Date createTime;

    private String modifierBy;

    private Date modifierTime;

    List<UserGroup> userGroups;

}