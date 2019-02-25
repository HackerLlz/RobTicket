package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.SessionConstant;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.SessionUtils;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.UserMapper;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 21:39
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final int RETRY_TIMES = 3;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String logout() {
        logger.info("注销用户");
        String url = UrlConstant.OTN_URL + "login/loginOut";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }

    @Override
    public UserInfoPO getUserInfo() {
        logger.info("获取用户信息");
        String username = SessionUtils.getString(SessionConstant.USERNAME);
        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setUsername(username);
        return userMapper.getUserInfo(userInfoPO);
    }

    @Override
    public Boolean updateUserInfoByUsername(UserInfoPO loginPO) {
        logger.info("更新用户信息");
        String username = SessionUtils.getString(SessionConstant.USERNAME);
        loginPO.setUsername(username);
        int updateCount = userMapper.updateUserInfoByUsername(loginPO);
        if (updateCount > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Integer insertUserOnUpdate(UserInfoPO userInfoPO) {
        return userMapper.insertUserOnUpdate(userInfoPO);
    }

    @Override
    public Integer insertUserDetailOnUpdate(UserInfoPO userInfoPO) {
        return userMapper.insertUserDetailOnUpdate(userInfoPO);
    }

    @Override
    public UserInfoPO getUserInfoPOFrom12306() {
        logger.info("获取用户信息");
        for (int i = 0; i < RETRY_TIMES; i++) {
            String result = getUserInfoFrom12306();
            if (result.startsWith("{")) {
                UserInfoPO userInfoPO = new UserInfoPO();
                JSONObject jsonObject = JSON.parseObject(result).getJSONObject("data").getJSONObject("userDTO");
                JSONObject jsonObject2 = jsonObject.getJSONObject("loginUserDTO");
                String username = jsonObject2.getString("user_name");
                String name = jsonObject2.getString("name");
                String phoneNumber = jsonObject.getString("mobile_no");
                String mail = jsonObject.getString("email");
                userInfoPO.setUsername(username);
                userInfoPO.setName(name);
                userInfoPO.setSendMail(mail);
                userInfoPO.setPhoneNumber(phoneNumber);
                return userInfoPO;
            }
        }
        logger.info("用户信息获取失败");
        return null;
    }

    @Override
    public Boolean existUserByUsernameAndPassword(UserInfoPO userInfoPO) {
        int count = userMapper.countUserByUsernameAndPassword(userInfoPO);
        return count > 0 ? true : false;
    }

    @Override
    public String getUsernameByAlias(String alias) {
        return userMapper.getUsernameByAlias(alias);
    }

    @Override
    public Boolean existPasswordByUsername(String username) {
        int count = userMapper.getPasswordCountByUsername(username);
        return count == 1 ? true : false;
    }

    private String getUserInfoFrom12306() {
        String url = UrlConstant.OTN_URL + "modifyUser/initQueryUserInfoApi";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }
}
