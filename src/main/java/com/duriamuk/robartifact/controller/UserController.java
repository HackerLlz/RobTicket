package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.validate.FormatValidater;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
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
    private static final String PATH = "user/";

    @Autowired
    private UserService userService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view(Model model) {
        logger.info("开始跳转到用户信息界面");
        model.addAttribute("userInfoPO", userService.getUserInfo());
        return PATH + "view";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        logger.info("开始注销用户，入参：{}");
        String url = UrlConstant.OTN_URL + "login/loginOut";
        String result = HttpUtils.doPostForm(url, null, true);
        return "redirect:/login/view";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody String payload) {
        logger.info("开始更新用户，入参：{}", payload);
        UserInfoPO userInfoPO = JSON.parseObject(payload, UserInfoPO.class);
        boolean isUpdate = false;
        if (!ObjectUtils.isEmpty(userInfoPO)) {
            boolean isValidate = FormatValidater.emailValidate(userInfoPO.getSendMail());
            if (isValidate) {
                isUpdate = userService.updateUserInfo(userInfoPO);
            }
        }
        return isUpdate? AjaxMessage.SUCCESS: AjaxMessage.FAIL;
    }
}
