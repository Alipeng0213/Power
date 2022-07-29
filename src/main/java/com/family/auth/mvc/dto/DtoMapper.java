package com.family.auth.mvc.dto;

import com.family.auth.model.User;
import com.family.auth.model.UserGroup;
import com.family.auth.model.UserGroupDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DtoMapper {


    public static UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        List<UserGroup> userGroups = user.getUserGroups();
        if(!CollectionUtils.isEmpty(userGroups)) {
            List<UserGroupDto> groupDtos = new ArrayList<>();
            userGroups.stream().forEach(userGroup -> {
                UserGroupDto groupDto = new UserGroupDto();
                BeanUtils.copyProperties(userGroup, groupDto);
                UserGroupDetail detail = userGroup.getDetail().get(0);
                groupDto.setNickname(detail.getNickname());
                groupDto.setGroupOrder(detail.getGroupOrder());
                groupDtos.add(groupDto);
            });
            userDto.setUserGroups(groupDtos);
        }
        return userDto;
    }

}
