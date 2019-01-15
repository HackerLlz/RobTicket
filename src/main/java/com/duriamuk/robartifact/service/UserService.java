package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 21:39
 */
public interface UserService {
    String logout();

    UserInfoPO getUserInfo();

    Boolean updateUserInfo(UserInfoPO loginPO);

    String getUserNameFrom12306();
}