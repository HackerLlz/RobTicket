package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.controller.TicketController;
import com.duriamuk.robartifact.mapper.LoginMapper;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 11:04
 */
@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    private static final int RETRY_TIMES = 2;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserService userService;

    @Override
    public Boolean doLogin(String payload) {
        logger.info("登陆，入参 ：{}", payload);
        String result = login(payload);
        if (isSuccess(result)) {
            HttpUtils.addReponseSetCookieToRequestCookie();
            result = uamtk(buildUamtkPayload());
            if (isSuccess(result)) {
                HttpUtils.addRequestCookie("tk", getTk(result));
                String username = userService.getUserNameFrom12306();
                if (!StringUtils.isEmpty(username)) {
                    HttpUtils.addReponseSetCookie("tk", getTk(result));
                    int insertCount = loginMapper.insertUsername(username);
                    logger.info("登陆成功，是{}用户", insertCount == 0? "老": "新");
                    return true;
                }
            }
            userService.logout();
        }
        logger.info("登陆失败");
        return false;
    }

    @Override
    public String login(String payload) {
        logger.info("12306登陆，入参 ：{}", payload);
        for (int i = 0; i < RETRY_TIMES; i ++) {
            String url = UrlConstant.PASS_URL + "web/login";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (isSuccess(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String uamtkStatic(String payload) {
        logger.info("验证uamtk，入参 ：{}", payload);
        for (int i = 0; i < RETRY_TIMES; i ++) {
            String url = UrlConstant.PASS_URL + "web/auth/uamtk-static";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (isSuccess(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String uamtk(String payload) {
        logger.info("验证并重置uamtk，入参 ：{}", payload);
        for (int i = 0; i < RETRY_TIMES; i ++) {
            String url = UrlConstant.PASS_URL + "web/auth/uamtk";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (isSuccess(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String uamtkClient(String payload) {
        logger.info("将TK存入Cookie，入参 ：{}", payload);
        String url = UrlConstant.WEB_URL + "otn/uamauthclient";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    @Override
    public Boolean isLogin() {
        logger.info("验证是否已登陆");
        String result = uamtk(buildUamtkPayload());
        if (isSuccess(result)) {
            // 假设登陆失败时的注销一定会成功
            HttpUtils.addReponseSetCookie("tk", getTk(result));
            logger.info("已登陆");
            return true;
        }
        logger.info("未登陆");
        return false;
    }

    @Override
    public String buildUamtkPayload() {
        String payload = "{\"appid\": \"otn\"}";
        return payload;
    }

    @Override
    public String buildUamtkClientPayload(String result) {
        String tk = getTk(result);
        String payload = "{\"tk\": \"" + tk + "\"}";
        return payload;
    }

    @Override
    public Boolean isSuccess(String result) {
        return (result.startsWith("{") && JSON.parseObject(result).getInteger("result_code") == 0)? true: false;
    }

    private String getTk(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        String apptk = jsonObject.getString("apptk");
        String newapptk = jsonObject.getString("newapptk");
        String tk = apptk != null? apptk: newapptk;
        return tk;
    }
}
