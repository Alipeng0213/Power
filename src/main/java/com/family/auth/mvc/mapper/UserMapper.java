package com.family.auth.mvc.mapper;

import com.family.auth.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectByUsername(String user);

    int insert(User record);

    List<User> selectAll();
}