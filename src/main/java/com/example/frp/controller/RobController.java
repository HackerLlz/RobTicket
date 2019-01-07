package com.example.frp.controller;

import com.example.frp.common.constant.AjaxMessage;
import com.example.frp.common.schedule.RobScheduledThreadPool;
import com.example.frp.common.schedule.RobTask;
import com.example.frp.entity.DTO.RobRequestDTO;
import com.example.frp.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    RobService robService;

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view(RobRequestDTO robRequestDTO, Model model) {
        logger.info("开始跳转到抢票界面，入参：{}", robRequestDTO.toString());
        model.addAttribute("isGD", isGD(robRequestDTO));
        model.addAttribute("robRequestData", robRequestDTO);
        return "rob/view";
    }

    @RequestMapping(value = "doRob", method = RequestMethod.POST)
    @ResponseBody
    public String doRob(@RequestBody String payload){
        logger.info("开始抢票，入参：{}", payload);
        RobScheduledThreadPool.schedule(new RobTask(payload, 1));
        return AjaxMessage.SUCCESS;
    }

    private Boolean isGD(RobRequestDTO robRequestDTO) {
        String trainNumber = robRequestDTO.getTrainNumber();
        return (trainNumber.contains("G") || trainNumber.contains("D"))? true: false;
    }
}
