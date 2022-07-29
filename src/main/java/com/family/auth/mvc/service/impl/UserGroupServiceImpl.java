package com.family.auth.mvc.service.impl;

import com.family.auth.model.User;
import com.family.auth.model.UserGroup;
import com.family.auth.model.UserGroupDetail;
import com.family.auth.mvc.mapper.UserGroupDetailMapper;
import com.family.auth.mvc.mapper.UserGroupMapper;
import com.family.auth.mvc.service.UserGroupService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserGroupServiceImpl implements UserGroupService {


    @Resource
    UserGroupMapper userGroupMapper;

    @Resource
    UserGroupDetailMapper userGroupDetailMapper;

    @Override
    public List<UserGroup> getUserGroup(User user) {
        List<UserGroupDetail> userGroupDetails = userGroupDetailMapper.selectByUserId(user.getUserId());
        Set<Integer> idSet = userGroupDetails.stream().map(UserGroupDetail::getGroupId).collect(Collectors.toSet());
        if(!CollectionUtils.isEmpty(idSet)) {
            List<UserGroup> userGroups = userGroupMapper.selectByGroupIdSet(idSet);
            return mappingGroupDetail(userGroups, userGroupDetails);
        }
        return Collections.EMPTY_LIST;
    }

    private List<UserGroup> mappingGroupDetail(List<UserGroup> userGroups, List<UserGroupDetail> userGroupDetails){
        userGroups.stream().forEach(userGroup -> {
            List<UserGroupDetail> mappingDetails = userGroupDetails.stream().filter(detail -> detail.getGroupId().equals(userGroup.getGroupId())).collect(Collectors.toList());
            userGroup.setDetail(mappingDetails);
        });
        return userGroups;
    }

}
