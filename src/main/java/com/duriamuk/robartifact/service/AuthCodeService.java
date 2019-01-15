package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.authCode.AuthCodePO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 16:56
 */
public interface AuthCodeService {
    void climbAuthCode(String message);

    AuthCodePO getAuthCodeByMd5(String md5);

    void updateAuthCode(AuthCodePO authCodePO);

    void insertAuthCode(AuthCodePO authCodePO);
}
