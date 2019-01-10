package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 13:27
 */
public interface UserMapper {
    Integer updateUserInfoByUsername(UserInfoPO loginPO);

    UserInfoPO getUserInfoByUsername(String username);
}
