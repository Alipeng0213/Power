package com.family.auth.mvc.mapper;

import com.family.auth.model.ConsumptionType;
import java.util.List;

public interface ConsumptionTypeMapper {
    int deleteByPrimaryKey(Integer typeId);

    int insert(ConsumptionType record);

    ConsumptionType selectByPrimaryKey(Integer typeId);

    List<ConsumptionType> selectAll();

    int updateByPrimaryKey(ConsumptionType record);
}