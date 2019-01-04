package com.example.frp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-02 17:03
 */
@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";
    private static String QUERY_URL = "leftTicket/queryZ";

    @Override
    public String listTicket(String payload) {
        logger.info("查询车票， 入参:{}", payload);
        String url = BASE_URL + QUERY_URL;
        String result = HttpUtils.doGet(url, payload, false);
        // 12306会定期更改查询url
        changeQueryUrl(result);
        return result;
    }

    @Override
    public String submitOrderRequest(String payload) {
        logger.info("提交订单请求， 入参:{}", payload);
        String url = BASE_URL + "leftTicket/submitOrderRequest";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    private void changeQueryUrl(String result) {
        if (result.startsWith("{") && result.length() < 100) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (!(Boolean)jsonObject.get("status")) {
                // 单例
                QUERY_URL = jsonObject.getString("c_url");
                logger.info("已改变QUERY_URL为：{}", QUERY_URL);
            }
        }
    }
}
