package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 13:27
 */
public interface UserMapper {
    Integer updateUserInfoByUsername(UserInfoPO loginPO);

    UserInfoPO getUserInfo(UserInfoPO userInfoPO);

    Integer insertUserOnUpdate(UserInfoPO userInfoPO);

    Integer insertUserDetailOnUpdate(UserInfoPO userInfoPO);

    Integer countUserByUsernameAndPassword(UserInfoPO userInfoPO);

    String getUsernameByAlias(String alias);

    Integer getPasswordCountByUsername(String username);
}
