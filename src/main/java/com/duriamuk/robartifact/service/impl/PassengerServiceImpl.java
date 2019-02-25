package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.tool.SessionUtils;
import com.duriamuk.robartifact.controller.TicketController;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.entity.PO.passenger.PassengerPO;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.LoginMapper;
import com.duriamuk.robartifact.mapper.PassengerMapper;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.service.PassengerService;
import com.duriamuk.robartifact.service.RobService;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;

import java.util.List;
import java.util.logging.LogManager;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:35
 */
@Service
public class PassengerServiceImpl implements PassengerService {
    private static final Logger logger = LoggerFactory.getLogger(PassengerServiceImpl.class);
    private static final int RETRY_TIMES = 3;

    @Autowired
    private PassengerMapper passengerMapper;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private RobService robService;

    @Override
    public String passengerInfo() {
        logger.info("获得乘客信息");
        for (int i = 0; i < RETRY_TIMES; i++) {
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
        for (int i = 0; i < RETRY_TIMES; i++) {
            String url = UrlConstant.OTN_URL + "confirmPassenger/checkOrderInfo";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String doOrder(String url, String data) {
        logger.info("确认下单， 入参:{}, {}", url, data);
        for (int i = 0; i < RETRY_TIMES; i++) {
            url = UrlConstant.OTN_URL + url;
            String result = HttpUtils.doPostForm(url, data, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public String getQueueCount(String payload) {
        logger.info("获得队列计数， 入参:{}", payload);
        for (int i = 0; i < RETRY_TIMES; i++) {
            String url = UrlConstant.OTN_URL + "confirmPassenger/getQueueCount";
            String result = HttpUtils.doPostForm(url, payload, true);
            if (result.startsWith("{")) {
                return result;
            }
        }
        return "";
    }

    @Override
    public Boolean updatePassenger() {
        logger.info("更新乘客信息");
        String payload = passengerInfo();
        if (!StringUtils.isEmpty(payload) && payload.startsWith("{")) {
            JSONObject dataJson = JSONObject.parseObject(payload).getJSONObject("data");
            if (StringUtils.isEmpty(dataJson.getString("exMsg"))) {
                List<PassengerPO> passengerPOList = buildPassengerList(dataJson);
                if (!ListUtils.isEmpty(passengerPOList)) {
                    int insertCount = passengerMapper.insertPassengerList(passengerPOList);
                    logger.info("更新乘客信息成功");
                    return true;
                }
            }
        }
        logger.info("更新乘客信息失败");
        return false;
    }

    @Override
    public List<PassengerPO> listPassengerByUsername(String username) {
        logger.info("获取乘客信息");
        return passengerMapper.listPassengerByUsername(username);
    }

    @Override
    public Boolean sync12306Passenger(String payload) {
        logger.info("同步12306乘客信息");
        String result = loginService.checkCode(payload);
        if (result.startsWith("{") &&
                JSON.parseObject(result).getInteger("result_code") == 4) {
            stopAllRobTask();
            result = loginService.login(loginService.getLoginInfo(SessionUtils.getString(SessionConstant.USERNAME)));
            if (result.startsWith("{")) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getInteger("result_code") == 0) {
                    HttpUtils.addResponseSetCookieToRequestCookie();
                    boolean keepLogin = loginService.keepLogin();
                    if (keepLogin) {
                        boolean isUpdate = updatePassenger();
                        if (isUpdate) {
                            logger.info("同步12306乘客信息成功");
                            return true;
                        }
                    }
                }
                if (jsonObject.getInteger("result_code") == 1) {
                    logger.warn("同步12306乘客信息失败，账号或密码错误");
                    return false;
                }
            }
        }
        logger.info("同步12306乘客信息失败");
        return false;
    }

    private List<PassengerPO> buildPassengerList(JSONObject dataJson) {
        JSONArray jsonArray = dataJson.getJSONArray("normal_passengers");
        List<PassengerPO> passengerPOList = jsonArray.toJavaList(PassengerPO.class);
        String username = getUsername();
        if (!StringUtils.isEmpty(username)) {
            String recordCount = String.valueOf(passengerPOList.size());
            for (PassengerPO passengerPO : passengerPOList) {
                passengerPO.setUsername(username);
                passengerPO.setRecordCount(recordCount);
            }
            logger.info("建立乘客信息List：{}", passengerPOList.toString());
            return passengerPOList;
        }
        logger.info("建立乘客信息List失败：获取12306用户信息失败");
        return null;
    }

    private String getUsername() {
        String username = SessionUtils.getString(SessionConstant.USERNAME);
        if (StringUtils.isEmpty(username)) {
            UserInfoPO UserInfoPOFrom12306 = userService.getUserInfoPOFrom12306();
            username = UserInfoPOFrom12306.getUsername();
        }
        return username;
    }

    private void stopAllRobTask(){
        UserInfoPO userInfoPO = userService.getUserInfo();
        List<RobParamsDTO> robParamsDTOList = robService.listRobRecordByUserId(userInfoPO.getId());
        for (RobParamsDTO robParamsDTO : robParamsDTOList) {
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + robParamsDTO.getId(), null, 0);
        }
    }
}
