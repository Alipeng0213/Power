package com.family.auth.mvc.mapper;

import com.family.auth.model.UserGroupDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserGroupDetailMapper {

    List<UserGroupDetail> selectByUserId(Integer userId);
}