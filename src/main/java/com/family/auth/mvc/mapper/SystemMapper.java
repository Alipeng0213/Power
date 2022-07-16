package com.family.auth.mvc.mapper;

import com.family.auth.model.System;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemMapper {


    int deleteByPrimaryKey(Integer fid);

    int insert(System record);

    System selectByPrimaryKey(Integer fid);

    List<System> selectAll();

    int updateByPrimaryKey(System record);

    System findByClientId(String clientId);


}