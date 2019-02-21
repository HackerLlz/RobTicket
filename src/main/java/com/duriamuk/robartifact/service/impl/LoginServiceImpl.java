package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.tool.CookieUtils;
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
    private static final int RETRY_TIMES = 3;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserService userService;

    @Override
    public String getCode() {
        logger.info("获得验证码");
        String url = UrlConstant.PASS_URL + "captcha/captcha-image64?login_site=E&module=login&rand=sjrand";
        String result = HttpUtils.doGet(url, null, true);
        return result;
    }

    @Override
    public String checkCode(String answer) {
        logger.info("验证验证码, 入参 ：{}", answer);
        String url = UrlConstant.PASS_URL + "captcha/captcha-check?answer=" + answer + "&rand=sjrand&login_site=E";
        String result = HttpUtils.doGet(url, null, true);
        return result;
    }

    @Override
    public Boolean doLogin(String payload) {
        logger.info("用户名密码登陆，入参 ：{}", payload);
        String result = login(payload);
        if (isSuccess(result)) {
            boolean isLogin = loginByUamtkSetCookie();
            if (isLogin) {
                return true;
            }
        }
        logger.info("登陆失败");
        return false;
    }

    @Override
    public String loginByQr(String payload) {
        logger.info("二维码扫码登陆，入参：{}", payload);
        String result = checkQr(payload);
        if (result.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject.getInteger("result_code") == 2) {
                boolean isLogin = loginByUamtkSetCookie();
                if (!isLogin) {
                    return buildLoginFailResult(jsonObject);
                }
            }
        }
        return result;
    }

    @Override
    public String login(String payload) {
        logger.info("12306登陆，入参 ：{}", payload);
        for (int i = 0; i < RETRY_TIMES; i++) {
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
        for (int i = 0; i < RETRY_TIMES; i++) {
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
        for (int i = 0; i < RETRY_TIMES; i++) {
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
        for (int i = 0; i < RETRY_TIMES; i++) {
            String url = UrlConstant.WEB_URL + "otn/uamauthclient";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (isSuccess(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public Boolean isLogin() {
        logger.info("验证是否已登陆");
        String result = uamtk(buildUamtkPayload());
        if (isSuccess(result)) {
            HttpUtils.addResponseSetCookieToRequestCookie();
            HttpUtils.updateRequestCookie("tk", getTk(result));
            result = uamtkClient(buildUamtkClientPayload(result));
            if (isSuccess(result)) {
                // 不知道有没有新session
                HttpUtils.addResponseSetCookieToRequestCookie();
                logger.info("已登陆");
                return true;
            }
        }
        logger.info("未登陆");
        return false;
    }

    @Override
    public Boolean keepLogin() {
        logger.info("保持登陆");
        // cookie存在ThreadLocal中
        String result = uamtk(buildUamtkPayload());
        if (isSuccess(result)) {
            result = uamtkClient(buildUamtkClientPayload(result));
            if (isSuccess(result)) {
                logger.info("保持登陆成功");
                return true;
            }
        }
        logger.info("保持登陆失败");
        return false;
    }

    @Override
    public String checkQr(String payload) {
        logger.info("验证二维码，入参 ：{}", payload);
        String url = UrlConstant.PASS_URL + "web/checkqr";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
    }

    private Boolean loginByUamtkSetCookie() {
        HttpUtils.addResponseSetCookieToRequestCookie();
        String result = uamtk(buildUamtkPayload());
        if (isSuccess(result)) {
            String oldTk = CookieUtils.getCookie("tk");
            HttpUtils.addResponseSetCookieToRequestCookie();
            HttpUtils.updateRequestCookie("tk", getTk(result));
            result = uamtkClient(buildUamtkClientPayload(result));
            if (isSuccess(result)) {
                // 不知道有没有新session
                HttpUtils.addResponseSetCookieToRequestCookie();
                String username = userService.getUserNameFrom12306();
                if (!StringUtils.isEmpty(username)) {
                    int insertCount = loginMapper.insertUsername(username);
                    logger.info("登陆成功，是{}用户", insertCount == 0 ? "老" : "新");
                    return true;
                }
            }
            if (!StringUtils.isEmpty(oldTk)) {
                // 抢票时会一直更新tk, 这里就可能注销失败，但没抢票时的登陆还是能保证注销成功，除非接口出问题
                HttpUtils.updateRequestCookie("tk", oldTk);
            }
        }
        userService.logout();
        // 保证第一次登陆失败后，uamtk不会被客户端保存
        CookieUtils.removeCookie("uamtk");
        CookieUtils.removeCookie("tk");
        return false;
    }

    private String buildUamtkPayload() {
        String payload = "{\"appid\": \"otn\"}";
        return payload;
    }

    private String buildUamtkClientPayload(String result) {
        String tk = getTk(result);
        String payload = "{\"tk\": \"" + tk + "\"}";
        return payload;
    }

    private Boolean isSuccess(String result) {
        return (result.startsWith("{") && JSON.parseObject(result).getInteger("result_code") == 0) ? true : false;
    }

    private String getTk(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        String apptk = jsonObject.getString("apptk");
        String newapptk = jsonObject.getString("newapptk");
        String tk = apptk != null ? apptk : newapptk;
        return tk;
    }

    private String buildLoginFailResult(JSONObject resultJson) {
        return "{\"result_message\": \"系统异常\",\"result_code\": \"5\"}";
    }
}
