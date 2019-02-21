package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.schedule.RobScheduledThreadPool;
import com.duriamuk.robartifact.common.schedule.RobTask;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.validate.EntityValidator;
import com.duriamuk.robartifact.common.validate.ValidateResult;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
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

import java.util.concurrent.TimeUnit;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-29 13:26
 */
@Controller
@RequestMapping("/rob")
public class RobController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private RobService robService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        logger.info("开始跳转到抢票界面");
        return "rob/view";
    }

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view(RobParamsDTO robParamsDTO, Model model) {
        logger.info("开始跳转到抢票界面，入参：{}", robParamsDTO.toString());
        model.addAttribute("robParamsDTO", robParamsDTO);
        return "rob/view";
    }

    @RequestMapping(value = "doRob", method = RequestMethod.POST)
    @ResponseBody
    public String doRob(@RequestBody String payload) {
        logger.info("开始抢票，入参：{}", payload);
        JSONObject jsonObject = JSON.parseObject(payload);
        RobParamsDTO robParamsDTO = JSON.parseObject(jsonObject.getString("robParamsData"), RobParamsDTO.class);
        ValidateResult validateResult = EntityValidator.validate(robParamsDTO);
        if (validateResult.hasError()) {
            return validateResult.getErrorMessages();
        }
        UserInfoPO userInfoPO = userService.getUserInfo();
        if (ObjectUtils.isEmpty(userInfoPO)) {
            return AjaxMessage.FAIL;
        }
        robParamsDTO.setUserId(userInfoPO.getId());
        boolean isInsert = robService.insertRobRecord(robParamsDTO);
        if (isInsert) {
            long id = robParamsDTO.getId();
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, true, 30, TimeUnit.DAYS);
            RobScheduledThreadPool.schedule(new RobTask(payload, 1, id));
            return AjaxMessage.SUCCESS;
        }
        return "进行中的抢票任务过多";
    }

    @RequestMapping(value = "stopRobTask", method = RequestMethod.GET)
    public String stopRobTask(Long id) {
        logger.info("开始停止抢票任务，入参：{}", id);
        RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, null, 0);
        return "redirect:/user/view";
    }

    @RequestMapping(value = "deleteRobTask", method = RequestMethod.GET)
    public String deleteRobTask(Long id) {
        logger.info("开始删除抢票任务，入参：{}", id);
        RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, null, 0);
        robService.deleteRobRecordById(id);
        return "redirect:/user/view";
    }

    @RequestMapping(value = "restartRobTask", method = RequestMethod.GET)
    public String restartRobTask(Long id, Model model) {
        logger.info("开始重新开始抢票任务，入参：{}", id);
        RobParamsDTO robParamsDTO = robService.getRobRecordById(id);
        model.addAttribute("robParamsDTO", robParamsDTO);
        return "rob/view";
    }
}
