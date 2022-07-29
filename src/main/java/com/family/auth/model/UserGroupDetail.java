package com.family.auth.model;

import lombok.Data;

@Data
public class UserGroupDetail {

    private Integer groupId;

    private Integer userId;

    private String nickname;

    private Integer groupOrder;

}