package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.UserMapper;
import com.duriamuk.robartifact.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public String logout(){
        logger.info("注销用户");
        String url = UrlConstant.OTN_URL + "login/loginOut";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }

    @Override
    public UserInfoPO getUserInfo() {
        logger.info("获取用户信息");
        String username = getUserNameFrom12306();
        if (!StringUtils.isEmpty(username)) {
            UserInfoPO userInfoPO = userMapper.getUserInfoByUsername(username);
            return userInfoPO;
        }
        return null;
    }

    @Override
    public Boolean updateUserInfo(UserInfoPO loginPO) {
        logger.info("更新用户信息");
        String username = getUserNameFrom12306();
        if (!StringUtils.isEmpty(username)) {
            loginPO.setUsername(username);
            int updateCount = userMapper.updateUserInfoByUsername(loginPO);
            if (updateCount > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUserNameFrom12306() {
        logger.info("获取用户名");
        for (int i = 0; i < RETRY_TIMES; i ++) {
            String result = getUserInfoFrom12306();
            if (result.startsWith("{")) {
                String username = JSON.parseObject(result).getJSONObject("data").getJSONObject("userDTO")
                        .getJSONObject("loginUserDTO").getString("user_name");
                return username;
            }
        }
        return "";
    }

    private String getUserInfoFrom12306() {
        String url = UrlConstant.OTN_URL + "modifyUser/initQueryUserInfoApi";
        String result = HttpUtils.doPostForm(url, null, true);
        return result;
    }
}
