package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.CookieContant;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.tool.*;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.LoginMapper;
import com.duriamuk.robartifact.mapper.UserMapper;
import com.duriamuk.robartifact.service.CodeCrackService;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.service.PassengerService;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 11:04
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    private static final int RETRY_TIMES = 5;
    private static final int AUTO_LOGIN_RETRY_TIMES = 10;
    private static final int LOGIN_RETRY_TIMES = 10;

    @Value("${constant.authCodeEnable}")
    private Boolean authCodeEnable;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CodeCrackService codeCrackService;

    @Autowired
    private PassengerService passengerService;

    @Override
    public String getCode() {
        logger.info("获得验证码");
        for (int i = 0; i < RETRY_TIMES; i++) {
            String url = UrlConstant.PASS_URL + "captcha/captcha-image64?login_site=E&module=login&rand=sjrand";
            String result = HttpUtils.doGet(url, null, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String checkCode(String answer) {
        logger.info("验证验证码, 入参 ：{}", answer);
        for (int i = 0; i < RETRY_TIMES; i++) {
            String url = UrlConstant.PASS_URL + "captcha/captcha-check?answer=" + answer + "&rand=sjrand&login_site=E";
            String result = HttpUtils.doGet(url, null, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public Boolean doLogin(String payload) {
        logger.info("用户名密码登陆，入参 ：{}", payload);
        if (!isCheckCode()) {
            logger.info("登陆失败，验证码未验证");
            return false;
        }
        UserInfoPO userInfoPO = JSON.parseObject(payload, UserInfoPO.class);
        String username = userService.getUsernameByAlias(userInfoPO.getUsername());
        boolean loginSuccess = false;
        if (!StringUtils.isEmpty(username)) {
            userInfoPO.setUsername(username);
            boolean isExist = userService.existUserByUsernameAndPassword(userInfoPO);
            // 用户若改过12306密码，却用老密码登陆本地系统，能成功登陆但抢票会失败，只能用户自己手动再登一次正确的密码
            if (isExist) {
                logger.info("登陆成功，是老用户");
                loginSuccess = true;
            }
        }
        if (!loginSuccess) {
            // 密码错误，可能修改了12306密码；新用户
            loginSuccess = loginAndSaveUserInfo(payload, userInfoPO);
        }
        if (loginSuccess) {
            setLoginSuccessSession(userInfoPO);
            return true;
        }
        CookieUtils.removeCookie(CookieContant.UAMTK);
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
                HttpUtils.addResponseSetCookieToRequestCookie();
                boolean keepLogin = keepLogin();
                if (keepLogin) {
                    UserInfoPO userInfoPOFrom12306 = userService.getUserInfoPOFrom12306();
                    if (!ObjectUtils.isEmpty(userInfoPOFrom12306)) {
                        String username = userInfoPOFrom12306.getUsername();
                        boolean isUser = userService.existPasswordByUsername(username);
                        if (isUser) {
                            SessionUtils.set(SessionConstant.LOGIN_STAT, SessionConstant.IS_USER);
                            SessionUtils.set(SessionConstant.USERNAME, username);
                            logger.info("二维码登陆成功，状态：权限用户");
                            return result;
                        } else {
                            boolean isUpdate = passengerService.updatePassenger();
                            if (isUpdate) {
                                SessionUtils.set(SessionConstant.LOGIN_STAT, SessionConstant.IS_VISITOR);
                                SessionUtils.set(SessionConstant.USERNAME, username);
                                // 访客插入或更新除密码之外的信息
                                UserInfoPO userInfoPO = buildInsertUserInfoPO(userInfoPOFrom12306);
                                int insertCount = userService.insertUserOnUpdate(userInfoPO);
                                int insertDetailCount = userService.insertUserDetailOnUpdate(userInfoPOFrom12306);
                                logger.info("二维码登陆成功，状态：访客");
                                return result;
                            }
                        }
                    }
                }
                CookieUtils.removeCookie(CookieContant.UAMTK);
                logger.info("二维码登陆失败");
                return buildLoginFailResult();
            }
        }
        return result;
    }

    private boolean isCheckCode() {
        if (!authCodeEnable) {
            return true;
        }
        Boolean authCodeStat = SessionUtils.getBoolean(SessionConstant.AUTH_CODE);
        if (!ObjectUtils.isEmpty(authCodeStat) && authCodeStat) {
            SessionUtils.set(SessionConstant.AUTH_CODE, null);
            return true;
        }
        return false;
    }

    private boolean loginAndSaveUserInfo(String payload, UserInfoPO userInfoPO) {
        String result = login(payload);
        if (isSuccess(result)) {
            HttpUtils.addResponseSetCookieToRequestCookie();
            boolean keepLogin = keepLogin();
            if (keepLogin) {
                UserInfoPO userInfoPOFrom12306 = userService.getUserInfoPOFrom12306();
                if (!ObjectUtils.isEmpty(userInfoPOFrom12306)) {
                    boolean isUpdate = passengerService.updatePassenger();
                    if (isUpdate) {
                        UserInfoPO newUserInfoPO = buildInsertUserInfoPO(userInfoPOFrom12306);
                        newUserInfoPO.setPassword(userInfoPO.getPassword());
                        int insertCount = userService.insertUserOnUpdate(newUserInfoPO);
                        int insertDetailCount = userService.insertUserDetailOnUpdate(userInfoPOFrom12306);
                        userInfoPO.setUsername(userInfoPOFrom12306.getUsername());
                        logger.info("登陆成功，是{}用户", insertCount == 0 ? "老" : "新");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private UserInfoPO buildInsertUserInfoPO(UserInfoPO userInfoPOFrom12306) {
        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setUsername(userInfoPOFrom12306.getUsername());
        userInfoPO.setSendMail(userInfoPOFrom12306.getSendMail());
        userInfoPO.setName(userInfoPOFrom12306.getName());
        return userInfoPO;
    }

    private void setLoginSuccessSession(UserInfoPO userInfoPO) {
        SessionUtils.set(SessionConstant.USERNAME, userInfoPO.getUsername());
        SessionUtils.set(SessionConstant.LOGIN_STAT, SessionConstant.IS_USER);
    }

    @Override
    public String login(String payload) {
        logger.info("12306登陆，入参 ：{}", payload);
        for (int i = 0; i < LOGIN_RETRY_TIMES; i++) {
            String url = UrlConstant.PASS_URL + "web/login";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (result.startsWith("{")) {
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
            if (result.startsWith("{")) {
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
            if (result.startsWith("{")) {
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
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public Boolean isLogin() {
        logger.info("验证是否已登陆");
        String loginStat = SessionUtils.getString(SessionConstant.LOGIN_STAT);
        if (!StringUtils.isEmpty(loginStat)) {
            logger.info("已登陆");
            return true;
        }
        logger.info("未登陆");
        return false;
    }

    @Override
    public Boolean isUser() {
        logger.info("验证是否是权限用户");
        String loginStat = SessionUtils.getString(SessionConstant.LOGIN_STAT);
        if (SessionConstant.IS_USER.equals(loginStat)) {
            logger.info("是权限用户");
            return true;
        }
        logger.info("不是权限用户");
        return false;
    }

    @Override
    public Boolean keepLogin() {
        logger.info("保持登陆");
        // cookie存在ThreadLocal中或request中都行
        String result = uamtk(buildUamtkPayload());
        if (isSuccess(result)) {
            HttpUtils.addResponseSetCookieToRequestCookie();
            result = uamtkClient(buildUamtkClientPayload(result));
            if (isSuccess(result)) {
                HttpUtils.addResponseSetCookieToRequestCookie();
                logger.info("保持登陆成功");
                return true;
            }
        }
        logger.info("保持登陆失败");
        return false;
    }

    @Override
    public Boolean autoLogin(UserInfoPO userInfoPO) {
        logger.info("自动登陆");
        int retryCount = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < AUTO_LOGIN_RETRY_TIMES; i++) {
            ++ retryCount;
            ThreadLocalUtils.set("");
            boolean isCrack = codeCrackService.crackCode();
            if (isCrack) {
                String result = login(getLoginInfo(userInfoPO.getUsername()));
                if (result.startsWith("{")) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("result_code") == 0) {
                        RedisUtils.set(PrefixName.ROB_UAMTK + userInfoPO.getId(),
                                CookieUtils.getFromCookieStr(ThreadLocalUtils.get(), CookieContant.UAMTK));
                        long endTime = System.currentTimeMillis();
                        double time = (endTime - startTime) / 1000.0;
                        logger.info("自动登陆成功，用时：{} s; 次数：{}", time, retryCount);
                        return true;
                    }
                    if (jsonObject.getInteger("result_code") == 1) {
                        // 密码或账号错误
                        logger.warn("自动登陆错误：密码或账号错误");
                        return null;
                    }
                }
            }
        }
        logger.info("自动登陆失败");
        return false;
    }

    @Override
    public String getLoginInfo(String username) {
        UserInfoPO loginInfoPO = loginMapper.getLoginInfoByUsername(username);
        return buildLoginPayload(loginInfoPO);
    }

    @Override
    public void testAutoLogin(String message) {
        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setUsername("username");
        autoLogin(userInfoPO);
    }

    @Override
    public String checkQr(String payload) {
        logger.info("验证二维码，入参 ：{}", payload);
        String url = UrlConstant.PASS_URL + "web/checkqr";
        String result = HttpUtils.doPostForm(url, payload, true);
        return result;
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

    private String buildLoginFailResult() {
        return "{\"result_message\": \"系统异常\",\"result_code\": \"5\"}";
    }

    private String buildLoginPayload(UserInfoPO userInfoPO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userInfoPO.getUsername());
        jsonObject.put("password", userInfoPO.getPassword());
        jsonObject.put("appid", "otn");
        return jsonObject.toJSONString();
    }
}
