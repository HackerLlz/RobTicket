package com.duriamuk.robartifact.service.impl;

import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.controller.TicketController;
import com.duriamuk.robartifact.service.PassengerService;
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
    private static final Logger logger = LoggerFactory.getLogger(PassengerServiceImpl.class);
    private static final int RETRY_TIMES = 2;

    @Override
    public String passengerInfo() {
        logger.info("获得乘客信息");
        for (int i = 0; i < RETRY_TIMES; i ++) {
            String url = UrlConstant.OTN_URL + "confirmPassenger/getPassengerDTOs";
            String result = HttpUtils.doPostForm(url, null, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String checkOrderInfo(String payload) {
        logger.info("验证订单信息， 入参:{}", payload);
        String url = UrlConstant.OTN_URL + "confirmPassenger/checkOrderInfo";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @Override
    public String doOrder(String url,String data) {
        logger.info("确认下单， 入参:{}, {}", url, data);
        url = UrlConstant.OTN_URL + url;
        String result = HttpUtils.doPostForm(url, data, true);
        return result;
    }

    @Override
    public String getQueueCount(String payload) {
        logger.info("获得队列计数， 入参:{}", payload);
        String url = UrlConstant.OTN_URL + "confirmPassenger/getQueueCount";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }
}
