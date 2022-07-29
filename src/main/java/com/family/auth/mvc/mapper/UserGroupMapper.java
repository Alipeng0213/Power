package com.family.auth.mvc.mapper;

import com.family.auth.model.UserGroup;
import com.family.auth.model.UserGroupDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserGroupMapper {

    List<UserGroup> selectByGroupIdSet(Set<Integer> idSet);

}