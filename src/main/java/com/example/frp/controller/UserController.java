package com.example.frp.controller;

import com.example.frp.common.tool.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-27 19:01
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "user/view";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        logger.info("开始注销用户，入参：{}");
        String url = BASE_URL + "login/loginOut";
        HttpUtils.doPostForm(url, null, true);
        return "redirect:/login/view";
    }
}
