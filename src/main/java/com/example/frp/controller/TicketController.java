package com.example.frp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-14 15:36
 */
@Controller
@RequestMapping("/ticket")
public class TicketController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";
    private static final String PATH = "ticket/";

    @Autowired
    private TicketService ticketService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return PATH + "view";
    }

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view2() {
        return PATH + "view";
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public String listTicket(@RequestBody String payload) {
        logger.info("开始查询车票， 入参:{}", payload);
        String result = ticketService.listTicket(payload);
        return result;
    }

    @RequestMapping(value = "checkUser", method = RequestMethod.GET)
    @ResponseBody
    public String checkUser() {
        logger.info("开始验证用户， 入参:{}");
        String url = BASE_URL + "login/checkUser";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }

    @RequestMapping(value = "orderRequest", method = RequestMethod.POST)
    @ResponseBody
    public String submitOrderRequest(@RequestBody String payload) {
        logger.info("开始提交订单请求， 入参:{}", payload);
        String url = BASE_URL + "leftTicket/submitOrderRequest";
        String result = HttpUtils.doPostForm(url, payload, true);
        result = changeMessages(result);
        return result;
    }

    @RequestMapping(value = "stationInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getStationInfo(@RequestBody String payload) {
        logger.info("开始获取停靠站信息， 入参:{}", payload);
        String url = BASE_URL + "czxx/queryByTrainNo";
        String result = HttpUtils.doGet(url, payload, false);
        return result;
    }

    @RequestMapping(value = "ticketPrice", method = RequestMethod.POST)
    @ResponseBody
    public String ticketPrice(@RequestBody String payload) {
        logger.info("开始查询票价， 入参:{}", payload);
        JSONObject jsonObject = JSONObject.parseObject(payload, Feature.OrderedField);
        String data = jsonObject.getString("data");
        String url = BASE_URL + jsonObject.getString("url");
        String result = HttpUtils.doGet(url, data, false);
        return result;
    }

    private String changeMessages(String result) {
        if (result.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (!ObjectUtils.isEmpty(jsonObject.get("messages")) && jsonObject.get("messages").toString().startsWith("[\"您还有未处理的订单")) {
                logger.info("开始改变messages：{}", jsonObject.get("messages"));
                String[] mgs = new String[]{"您还有未处理的订单，请您到<a href=\"https://kyfw.12306.cn/otn/view/train_order.html\" target=\"_blank\">[未完成订单]</a>进行处理!"};
                jsonObject.put("messages", mgs);
                logger.info("完成改变messages：{}", jsonObject.get("messages"));
                return JSON.toJSONString(jsonObject);
            }
        }
        return result;
    }
}
