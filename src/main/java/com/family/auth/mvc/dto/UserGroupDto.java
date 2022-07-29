package com.family.auth.mvc.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserGroupDto {

    private Integer groupId;

    private String groupName;

    private String description;

    private String members;

    private String createBy;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private String nickname;

    private Integer groupOrder;

}