package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 14:44
 */
public interface MailSendService {
    Boolean sendMail(String message);
}
