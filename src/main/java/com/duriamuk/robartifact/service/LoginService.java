package com.duriamuk.robartifact.service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 11:04
 */
public interface LoginService {
    Boolean doLogin(String payload);

    String login(String payload);

    String uamtkStatic(String payload);

    String uamtk(String payload);

    String uamtkClient(String payload);

    Boolean isLogin();

    Boolean keepLogin();
}
