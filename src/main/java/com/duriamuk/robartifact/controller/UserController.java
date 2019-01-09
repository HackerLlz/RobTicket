package com.duriamuk.robartifact.controller;

import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-27 19:01
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "user/view";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        logger.info("开始注销用户，入参：{}");
        String url = UrlConstant.OTN_URL + "login/loginOut";
        HttpUtils.doPostForm(url, null, true);
        return "redirect:/login/view";
    }
}
