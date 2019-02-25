package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.tool.SessionUtils;
import com.duriamuk.robartifact.common.validate.EntityValidator;
import com.duriamuk.robartifact.common.validate.ValidateResult;
import com.duriamuk.robartifact.common.validate.group.Update;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.service.RobService;
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

import java.util.List;

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

    @Autowired
    private RobService robService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view(Model model) {
        logger.info("开始跳转到用户信息界面");
        UserInfoPO userInfoPO = userService.getUserInfo();
        List<RobParamsDTO> robParamsList = robService.listRobRecordByUserId(userInfoPO.getId());
        model.addAttribute("userInfoPO", userInfoPO);
        model.addAttribute("robParamsList", robParamsList);
        return PATH + "view";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        logger.info("开始注销用户，入参：{}");
//        userService.logout();
        SessionUtils.remove(SessionConstant.LOGIN_STAT);
        SessionUtils.remove(SessionConstant.USERNAME);
        return "redirect:/login/view";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public String update(@RequestBody String payload) {
        logger.info("开始更新用户，入参：{}", payload);
        UserInfoPO userInfoPO = JSON.parseObject(payload, UserInfoPO.class);
        ValidateResult validateResult = EntityValidator.validate(userInfoPO, Update.class);
        if (validateResult.hasError()) {
            return validateResult.getErrorMessages();
        }
        boolean isUpdate = userService.updateUserInfoByUsername(userInfoPO);
        return isUpdate ? AjaxMessage.SUCCESS : AjaxMessage.FAIL;
    }

    @RequestMapping(value = "staticInfo", method = RequestMethod.GET)
    @ResponseBody
    public String getStaticInfo() {
        logger.info("开始获得用户静态信息");
        UserInfoPO userInfoPO = userService.getUserInfo();
        return userInfoPO.getName();
    }
}
