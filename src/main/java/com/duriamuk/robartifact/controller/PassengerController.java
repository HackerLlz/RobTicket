package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.SessionUtils;
import com.duriamuk.robartifact.common.tool.StrUtils;
import com.duriamuk.robartifact.entity.PO.passenger.PassengerPO;
import com.duriamuk.robartifact.service.PassengerService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-24 11:30
 */
@Controller
@RequestMapping("/passenger")
public class PassengerController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String PATH = "passenger/";
    private static final List<String> TWO_ISOPENCLICK = Arrays.asList("93", "95", "97", "99");
    private static final List<String> OTHER_ISOPENCLICK = Arrays.asList("91", "93", "98", "99", "95", "97");

    @Autowired
    private PassengerService passengerService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view2() {
        return "redirect:/ticket/view";
    }

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view(@RequestBody String payload, Model model) {
        logger.info("开始跳转到乘客界面， 入参:{}", payload);
        String url = StrUtils.findVlaue("url", "=", 0, null, payload);
        try {
            url = UrlConstant.OTN_URL + URLDecoder.decode(url, "UTF-8");
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
        logger.info("开始获得乘客信息");
        List<PassengerPO> passengerPOList = passengerService.listPassengerByUsername(SessionUtils.getString(SessionConstant.USERNAME));
        return buildPassengerResult(passengerPOList);
    }

    @RequestMapping(value = "syncPassenger", method = RequestMethod.POST)
    @ResponseBody
    public String syncPassenger(@RequestBody String payload) {
        logger.info("开始同步12306乘客信息，入参：{}", payload);
        boolean isSync = passengerService.sync12306Passenger(payload);
        return isSync ? AjaxMessage.SUCCESS : AjaxMessage.FAIL;
    }

    @RequestMapping(value = "sync", method = RequestMethod.GET)
    public String sync() {
        return PATH + "sync";
    }

    @RequestMapping(value = "checkOrderInfo", method = RequestMethod.POST)
    @ResponseBody
    public String checkOrderInfo(@RequestBody String payload) {
        logger.info("开始验证订单信息， 入参:{}", payload);
        String result = passengerService.checkOrderInfo(payload);
        return result;
    }

    @RequestMapping(value = "getQueueCount", method = RequestMethod.POST)
    @ResponseBody
    public String getQueueCount(@RequestBody String payload) {
        logger.info("开始获得队列计数， 入参:{}", payload);
        String result = passengerService.getQueueCount(payload);
        return result;
    }

    @RequestMapping(value = "doOrder", method = RequestMethod.POST)
    @ResponseBody
    public String doOrder(@RequestBody String payload) {
        logger.info("开始确认下单， 入参:{}", payload);
        JSONObject jsonObject = JSONObject.parseObject(payload);
        String data = jsonObject.getString("data");
        String url = UrlConstant.OTN_URL + jsonObject.getString("url");
        String result = passengerService.doOrder(url, data);
        return result;
    }

    @RequestMapping(value = "queryOrderWaitTime", method = RequestMethod.POST)
    @ResponseBody
    public String queryOrderWaitTime(@RequestBody String payload) {
        logger.info("开始查询订单等待时间， 入参:{}", payload);
        String url = UrlConstant.OTN_URL + "confirmPassenger/queryOrderWaitTime";
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
        String value = StrUtils.findVlaue(name, afterName, interval, endStr, result);
        model.addAttribute(name, value);
        logger.info("已添加属性到Model, {}: {}", name, value);
    }

    private String buildPassengerResult(List<PassengerPO> passengerPOList) {
        JSONObject dataJson = new JSONObject();
        dataJson.put("normal_passengers", passengerPOList);
        dataJson.put("isExist", true);
        dataJson.put("two_isOpenClick", TWO_ISOPENCLICK);
        dataJson.put("other_isOpenClick", OTHER_ISOPENCLICK);
        dataJson.put("dj_passengers", new ArrayList<>());
        JSONObject resultJson = new JSONObject();
        resultJson.put("data", dataJson);
        resultJson.put("status", true);
        String result = resultJson.toJSONString();
        logger.info("建立乘客信息json：{}", result);
        return result;
    }
}
