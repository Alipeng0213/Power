package com.family.auth.mvc.mapper;

import com.family.auth.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    User selectByPrimaryKey(Integer id);

    User selectByUsername(String username);

    List<User> selectAll();

    int updateByPrimaryKey(User record);
}