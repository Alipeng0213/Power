package com.family.auth.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserGroup {

    private Integer groupId;

    private String groupName;

    private String description;

    private String createBy;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private String members;

    private List<UserGroupDetail> detail;

}