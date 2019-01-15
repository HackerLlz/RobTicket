package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.PO.authCode.AuthCodePO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 17:02
 */
public interface AuthCodeMapper {
    AuthCodePO getAuthCodeByMd5(String md5);

    void updateAuthCode(AuthCodePO authCodePO);

    void insertAuthCode(AuthCodePO authCodePO);
}
