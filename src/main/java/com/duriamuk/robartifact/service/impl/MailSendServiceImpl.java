package com.duriamuk.robartifact.service.impl;

import com.duriamuk.robartifact.common.tool.MailSendUtils;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.service.MailSendService;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 14:44
 */
@Service("mailSendService")
public class MailSendServiceImpl implements MailSendService {
    private static final Logger logger = LoggerFactory.getLogger(MailSendServiceImpl.class);
    private static final int RETRY_TIMES = 2;

    @Autowired
    private UserService userService;

    @Override
    public Boolean sendMail(String message) {
        logger.info("开始发送邮件");
        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setId(Long.valueOf(message));
        UserInfoPO userInfo = userService.getUserInfoPO(userInfoPO);
        String username = userInfo.getUsername();
        String mail = userInfo.getSendMail();
        String subject = "抢票成功";
        String content = "恭喜" + username + "用户！抢票成功";
        for (int i = 0; i < RETRY_TIMES; i++) {
            boolean isSend = retrySendMail(mail, subject, content);
            if (isSend) {
                logger.info("邮件最终发送成功：{}", content);
                return true;
            }
        }
        logger.info("邮件最终发送失败：{}", content);
        return false;
    }

    private Boolean retrySendMail(String mail, String subject, String content) {
        try {
            MailSendUtils.sendHtmlMessage(mail, subject, content);
        } catch (Exception e) {
            logger.info("邮件发送失败：{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
