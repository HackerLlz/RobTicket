package com.duriamuk.robartifact.controller;

import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import com.duriamuk.robartifact.service.AuthCodeService;
import com.duriamuk.robartifact.service.AuthImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 21:53
 */
@Deprecated
@Controller
@RequestMapping("/codeTest")
public class AuthCodeController {
    private static final Logger logger = LoggerFactory.getLogger(AuthCodeController.class);

    @Autowired
    private AuthCodeService authCodeService;

    @Autowired
    private AuthImageService authImageService;

    @RequestMapping(value = "climbMD5", method = RequestMethod.GET)
    @ResponseBody
    public String climbMD5() {
        logger.info("开始爬取验证码图片MD5");
        for (int i = 0; i < 2; i++) {
            MessageConsumerThreadPool.message(new MessageTask(AuthCodeService.class, "climbAuthCode", ""));
        }
        return AjaxMessage.SUCCESS + " at: " + Calendar.getInstance().getTime();
    }

    @RequestMapping(value = "climbFont", method = RequestMethod.GET)
    @ResponseBody
    public String climbFont() {
        logger.info("开始爬取验证码字体");
        MessageConsumerThreadPool.message(new MessageTask(AuthImageService.class, "climbAuthFont", ""));
        return AjaxMessage.SUCCESS + " at: " + Calendar.getInstance().getTime();
    }

    @RequestMapping(value = "climbImage", method = RequestMethod.GET)
    @ResponseBody
    public String climbImage() {
        logger.info("开始爬取验证码切割图");
        MessageConsumerThreadPool.message(new MessageTask(AuthImageService.class, "climbAuthImage", ""));
        return AjaxMessage.SUCCESS + " at: " + Calendar.getInstance().getTime();
    }

    @RequestMapping(value = "identify", method = RequestMethod.GET)
    @ResponseBody
    public String identify() {
        logger.info("开始验证图片验证码");
        MessageConsumerThreadPool.message(new MessageTask(AuthImageService.class, "testIdentifyAuthImage", ""));
        return AjaxMessage.SUCCESS + " at: " + Calendar.getInstance().getTime();
    }

    @RequestMapping(value = "isSame", method = RequestMethod.GET)
    @ResponseBody
    public String isSameImage() {
        logger.info("开始验证图片是否相同");
        authImageService.isSameImage();
        return AjaxMessage.SUCCESS + " at: " + Calendar.getInstance().getTime();
    }
}
