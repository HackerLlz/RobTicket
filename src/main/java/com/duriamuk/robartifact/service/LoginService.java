package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 11:04
 */
public interface LoginService {
    String getCode();

    String checkCode(String answer);

    Boolean doLogin(String payload);

    String loginByQr(String payload);

    String login(String payload);

    String uamtkStatic(String payload);

    String uamtk(String payload);

    String uamtkClient(String payload);

    Boolean isLogin();

    Boolean isUser();

    Boolean keepLogin();

    String checkQr(String payload);

    Boolean autoLogin(UserInfoPO userInfoPO);

    String getLoginInfo(String username);

    void testAutoLogin(String message);
}
