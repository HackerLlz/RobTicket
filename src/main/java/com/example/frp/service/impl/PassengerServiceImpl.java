package com.example.frp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.controller.TicketController;
import com.example.frp.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:35
 */
@Service
public class PassengerServiceImpl implements PassengerService {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";

    @Override
    public String checkOrderInfo(String payload) {
        logger.info("验证订单信息， 入参:{}", payload);
        String url = BASE_URL + "confirmPassenger/checkOrderInfo";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @Override
    public String doOrder(String url,String data) {
        logger.info("确认下单， 入参:{}, {}", url, data);
        url = BASE_URL + url;
        String result = HttpUtils.doPostForm(url, data, true);
        return result;
    }

    @Override
    public String getQueueCount(String payload) {
        logger.info("获得队列计数， 入参:{}", payload);
        String url = BASE_URL + "confirmPassenger/getQueueCount";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }
}
