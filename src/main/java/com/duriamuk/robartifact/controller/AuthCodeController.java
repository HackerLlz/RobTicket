package com.duriamuk.robartifact.controller;

import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.service.AuthCodeService;
import com.duriamuk.robartifact.service.impl.AuthCodeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 21:53
 */
@Controller
@RequestMapping("/code")
public class AuthCodeController {
    private static final Logger logger = LoggerFactory.getLogger(AuthCodeController.class);

    @Autowired
    private AuthCodeService authCodeService;

    @RequestMapping(value = "climb", method = RequestMethod.GET)
    @ResponseBody
    public String climb() {
        logger.info("开始爬取验证码图片");
        MessageConsumerThreadPool.message(new MessageTask(AuthCodeService.class, "climbAuthCode", ""));
        return AjaxMessage.SUCCESS;
    }
}
