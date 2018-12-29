package com.example.frp.controller;

import com.example.frp.common.tool.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "rob/view";
    }

    @RequestMapping(value = "doRob", method = RequestMethod.POST)
    @ResponseBody
    public String doRob(@RequestBody String payload) {
        logger.info("开始抢票，入参：{}", payload);

        String url = BASE_URL + "confirmPassenger/confirmSingleForQueue";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }
}
