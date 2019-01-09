package com.duriamuk.robartifact.controller;

import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.service.LoginService;
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
    LoginService loginService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "login/view";
    }

    @RequestMapping(value = "code", method = RequestMethod.GET)
    @ResponseBody
    public String getCode() {
        logger.info("开始获得验证码");
        String url = UrlConstant.PASS_URL + "captcha/captcha-image64?login_site=E&module=login&rand=sjrand";
        String result = HttpUtils.doGet(url, null, true);
        return result;
    }

    @RequestMapping(value = "checkCode", method = RequestMethod.GET)
    @ResponseBody
    public String checkCode(String answer) {
        logger.info("开始验证验证码, 入参 ：{}", answer);
        String url = UrlConstant.PASS_URL + "captcha/captcha-check?answer=" + answer + "&rand=sjrand&login_site=E";
        String result = HttpUtils.doGet(url, null, true);
        return result;
    }

    @RequestMapping(value = "in", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody String payload) {
        logger.info("开始登陆，入参 ：{}", payload);
        String result = loginService.login(payload);
        return result;
    }

    @RequestMapping(value = "uamtkStatic", method = RequestMethod.POST)
    @ResponseBody
    public String uamtkStatic(@RequestBody String payload) {
        logger.info("开始验证uamtk，入参 ：{}", payload);
        String url = UrlConstant.PASS_URL + "web/auth/uamtk-static";
        String result = HttpUtils.doPostForm(url, payload, true);
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
}