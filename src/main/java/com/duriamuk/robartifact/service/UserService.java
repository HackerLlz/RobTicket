package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 21:39
 */
public interface UserService {
    String logout();

    UserInfoPO getUserInfoBySessionUsername();

    UserInfoPO getUserInfoPO(UserInfoPO userInfoPO);

    Boolean updateUserInfoByUsername(UserInfoPO loginPO);

    Integer insertUserOnUpdate(UserInfoPO userInfoPO);

    Integer insertUserDetailOnUpdate(UserInfoPO userInfoPO);

    UserInfoPO getUserInfoPOFrom12306();

    Boolean existUserByUsernameAndPassword(UserInfoPO userInfoPO);

    String getUsernameByAlias(String alias);

    Boolean existPasswordByUsername(String username);
}
