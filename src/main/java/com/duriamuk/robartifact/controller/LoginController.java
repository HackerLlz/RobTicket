package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.SessionUtils;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.service.AuthImageService;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.service.impl.LoginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-17 15:56
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "login/view";
    }

    @RequestMapping(value = "code", method = RequestMethod.GET)
    @ResponseBody
    public String getCode() {
        logger.info("开始获得验证码");
        String result = loginService.getCode();
        return result;
    }

    @RequestMapping(value = "checkCode", method = RequestMethod.GET)
    @ResponseBody
    public String checkCode(String answer) {
        logger.info("开始验证验证码, 入参 ：{}", answer);
        String result = loginService.checkCode(answer);
        if (result.startsWith("{") &&
                JSON.parseObject(result).getInteger("result_code") == 4) {
            SessionUtils.set(SessionConstant.AUTH_CODE, true);
        }
        return result;
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    @ResponseBody
    public String doLogin(@RequestBody String payload) {
        logger.info("开始登陆，入参 ：{}", payload);
        boolean isSuccess = loginService.doLogin(payload);
        return isSuccess ? AjaxMessage.SUCCESS : AjaxMessage.FAIL;
    }

    @RequestMapping(value = "uamtkStatic", method = RequestMethod.POST)
    @ResponseBody
    public String uamtkStatic(@RequestBody String payload) {
        logger.info("开始验证uamtk，入参 ：{}", payload);
        String result = loginService.uamtkStatic(payload);
        return result;
    }

    @RequestMapping(value = "uamtk", method = RequestMethod.POST)
    @ResponseBody
    public String uamtk(@RequestBody String payload) {
        logger.info("开始验证并重置uamtk，入参 ：{}", payload);
        String result = loginService.uamtk(payload);
        return result;
    }

    @RequestMapping(value = "uamtkClient", method = RequestMethod.POST)
    @ResponseBody
    public String uamtkClient(@RequestBody String payload) {
        logger.info("开始将TK存入Cookie，入参 ：{}", payload);
        String result = loginService.uamtkClient(payload);
        return result;
    }

    @RequestMapping(value = "isLogin", method = RequestMethod.GET)
    @ResponseBody
    public String isLogin() {
        logger.info("开始验证是否已登陆");
        boolean isLogin = loginService.isLogin();
        return isLogin ? AjaxMessage.SUCCESS : AjaxMessage.FAIL;
    }

    @RequestMapping(value = "createQr", method = RequestMethod.POST)
    @ResponseBody
    public String createQr(@RequestBody String payload) {
        logger.info("开始获得二维码，入参 ：{}", payload);
        String url = UrlConstant.PASS_URL + "web/create-qr64";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @RequestMapping(value = "loginByQr", method = RequestMethod.POST)
    @ResponseBody
    public String loginByQr(@RequestBody String payload) {
        logger.info("开始验证二维码登陆情况，入参 ：{}", payload);
        String result = loginService.loginByQr(payload);
        return result;
    }

    @RequestMapping(value = "testAutoLogin", method = RequestMethod.GET)
    @ResponseBody
    public String testAutoLogin() {
        logger.info("开始测试自动登陆");
        MessageConsumerThreadPool.message(new MessageTask(LoginService.class, "testAutoLogin", ""));
        return AjaxMessage.SUCCESS;
    }
}
