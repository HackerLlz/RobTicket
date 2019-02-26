package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.service.TicketService;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
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
    private static String QUERY_URL = "leftTicket/queryX";

    @Override
    public String checkUser() {
        logger.info("验证用户， 入参:{}");
        String url = UrlConstant.OTN_URL + "login/checkUser";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }

    @Override
    public String listTicket(String payload) {
        logger.info("查询车票， 入参:{}", payload);
        String url = UrlConstant.OTN_URL + QUERY_URL;
        String result = HttpUtils.doGet(url, payload, false);
        // 12306会定期更改查询url
        changeQueryUrl(result);
        return result;
    }

    @Override
    public String submitOrderRequest(String payload) {
        logger.info("提交订单请求， 入参:{}", payload);
        String url = UrlConstant.OTN_URL + "leftTicket/submitOrderRequest";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    private void changeQueryUrl(String result) {
        if (result.startsWith("{") && result.length() < 100) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (!(Boolean) jsonObject.get("status")) {
                // 单例
                QUERY_URL = jsonObject.getString("c_url");
                logger.info("已改变QUERY_URL为：{}", QUERY_URL);
            }
        }
    }
}
