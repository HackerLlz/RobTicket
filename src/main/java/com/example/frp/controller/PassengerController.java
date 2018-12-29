package com.example.frp.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-24 11:30
 */
@Controller
@RequestMapping("/passenger")
public class PassengerController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";
    private static final String WEB_URL = "https://kyfw.12306.cn";
    private static final String PATH = "passenger/";

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view2() {
        return "redirect:/ticket/view";
    }

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view(@RequestBody String payload, Model model) {
        logger.info("开始跳转到乘客界面， 入参:{}", payload);
        String url = StringUtils.findVlaue("url", "=", 0, null, payload);
        try {
            url = BASE_URL + URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.doPostForm(url, null, true);
        buildModel(result, model);
        return PATH + "view";
    }

    @RequestMapping(value = "passengerInfo", method = RequestMethod.GET)
    @ResponseBody
    public String passengerInfo() {
        logger.info("开始获得乘客信息， 入参:{}");
        String url = BASE_URL + "confirmPassenger/getPassengerDTOs";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }

    @RequestMapping(value = "checkOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    public String checkOrderInfo(@RequestBody String payload) {
        logger.info("开始验证订单信息， 入参:{}", payload);
        String url = BASE_URL + "confirmPassenger/checkOrderInfo";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @RequestMapping(value = "getQueueCount", method = RequestMethod.POST)
    @ResponseBody
    public String getQueueCount(@RequestBody String payload) {
        logger.info("开始获得队列计数， 入参:{}", payload);
        String url = BASE_URL + "confirmPassenger/getQueueCount";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @RequestMapping(value = "doOrder", method = RequestMethod.POST)
    @ResponseBody
    public String doOrder(@RequestBody String payload) {
        logger.info("开始确认下单， 入参:{}", payload);
        JSONObject jsonObject = JSONObject.parseObject(payload);
        String data = jsonObject.getString("data");
        String url = WEB_URL + jsonObject.getString("url");
        String result = HttpUtils.doPostForm(url, data, true);
        return result;
    }

    @RequestMapping(value = "queryOrderWaitTime", method = RequestMethod.POST)
    @ResponseBody
    public String queryOrderWaitTime(@RequestBody String payload) {
        logger.info("开始查询订单等待时间， 入参:{}", payload);
        String url = BASE_URL + "confirmPassenger/queryOrderWaitTime";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    private void buildModel(String result, Model model) {
        addValueToModel("ticket_seat_codeMap", "=", 0, ";", result, model);
        addValueToModel("ticketInfoForPassengerForm", "=", 0, ";", result, model);
        addValueToModel("orderRequestDTO", "=", 0, ";", result, model);
        addValueToModel("init_seatTypes", "=", 0, ";", result, model);
        addValueToModel("defaultTicketTypes", "=", 0, ";", result, model);
        addValueToModel("init_cardTypes", "=", 0, ";", result, model);
        addValueToModel("member_tourFlag", " = '", 0, "'", result, model);
//        addValueToModel("init_limit_ticket_num", "='", 0, "'", result, model);
        addValueToModel("globalRepeatSubmitToken", " = '", 0, "'", result, model);
    }

    private void addValueToModel(String name, String afterName, int interval, String endStr, String result, Model model) {
        String value = StringUtils.findVlaue(name, afterName, interval, endStr, result);
        model.addAttribute(name, value);
        logger.info("已添加属性到Model, {}: {}", name, value);
    }
}
