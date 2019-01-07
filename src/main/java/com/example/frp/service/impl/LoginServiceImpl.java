package com.example.frp.service.impl;

import com.example.frp.common.constant.UrlConstant;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.controller.TicketController;
import com.example.frp.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 11:04
 */
@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Override
    public String uamtk(String payload) {
        logger.info("验证并重置uamtk，入参 ：{}", payload);
        String url = UrlConstant.PASS_URL + "web/auth/uamtk";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @Override
    public String uamtkClient(String payload) {
        logger.info("将TK存入Cookie，入参 ：{}", payload);
        String url = UrlConstant.WEB_URL + "otn/uamauthclient";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }
}
