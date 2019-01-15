package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import com.duriamuk.robartifact.entity.PO.authCode.AuthCodePO;
import com.duriamuk.robartifact.mapper.AuthCodeMapper;
import com.duriamuk.robartifact.service.AuthCodeService;
import com.duriamuk.robartifact.service.LoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 17:03
 */
@Service("authCodeService")
public class AuthCodeServiceImpl implements AuthCodeService {
    private static final Logger logger = LoggerFactory.getLogger(AuthCodeServiceImpl.class);
    private static final List<String> answerList = Arrays.asList("42,46", "115,48", "188,46", "260,43", "258,116", "183,117", "117,117", "37,117");
    private static final List<String> positionList = Arrays.asList("0","1","2","3","4","5","0,1","0,2","0,3","0,4","0,5","1,2","1,3","1,4","1,5","2,3","2,4","2,5","3,4","3,5","4,5","0,1,2","0,1,3","0,1,4","0,1,5","0,2,3","0,2,4","0,2,5","0,3,4","0,3,5","0,4,5","1,2,3","1,2,4","1,2,5","1,3,4","1,3,5","1,4,5","2,3,4","2,3,5","2,4,5","3,4,5","0,1,2,3","0,1,2,4","0,1,2,5","0,1,3,4","0,1,3,5","0,1,4,5","0,2,3,4","0,2,3,5","0,2,4,5","0,3,4,5","1,2,3,4","1,2,3,5","1,2,4,5","1,3,4,5","2,3,4,5","0,1,2,3,4","0,1,2,3,5","0,1,2,4,5","0,1,3,4,5","0,2,3,4,5","1,2,3,4,5","0,1,2,3,4,5");
    private static final int CLIMB_TIMES = 100000;  // 一个线程 一万次，五万条数据 五个小时

    @Autowired
    private AuthCodeMapper authCodeMapper;

    @Autowired
    private LoginService loginService;

    /**
     * 需要放在线程池中运行，用ThreadLocal存取cookie，不然Reponse中的setCoookie不能覆盖会越来越多
     */
    @Override
    public void climbAuthCode(String message) {
        logger.info("爬取验证码图片");
        for (int i = 0; i < CLIMB_TIMES; i ++) {
            String result = loginService.getCode();
            if (result.startsWith("{")) {
                    JSONObject resultJson = JSON.parseObject(result);
                    if (resultJson.getInteger("result_code") == 0) {
                        String md5 = DigestUtils.md5Hex(resultJson.getString("image"));
                        AuthCodePO authCodePO = getAuthCodeByMd5(md5);
                        if (ObjectUtils.isEmpty(authCodePO)) {
                            authCodePO = new AuthCodePO(md5, 0, 0);
                            insertAuthCode(authCodePO);
                        }
                        if (authCodePO.getStatus() == -1) {
                            // 已遍历列表但验证最终未成功
                            continue;
                        }
                        String answer = buildAnswer(authCodePO);
                        result = loginService.checkCode(answer);
                        if (result.startsWith("{")) {
                            resultJson = JSON.parseObject(result);
                            setAuthCodePO(authCodePO, resultJson);
                            // 若验证返回的是html页面，则数据库不做更新
                            updateAuthCode(authCodePO);
                        }
                    }
            }
            // 清空cookie，模拟新请求
            ThreadLocalUtils.set("");
        }
    }

    @Override
    public AuthCodePO getAuthCodeByMd5(String md5) {
        logger.info("用MD5码获得验证码，入参：{}", md5);
        return authCodeMapper.getAuthCodeByMd5(md5);
    }

    @Override
    public void updateAuthCode(AuthCodePO authCodePO) {
        logger.info("更新验证码数据，入参：{}", authCodePO);
        authCodeMapper.updateAuthCode(authCodePO);
    }

    @Override
    public void insertAuthCode(AuthCodePO authCodePO) {
        logger.info("插入验证码数据，入参：{}", authCodePO);
        authCodeMapper.insertAuthCode(authCodePO);
    }

    private String buildAnswer(AuthCodePO authCodePO) {
        String answerIndex = positionList.get(authCodePO.getCheckPosition());
        String[] indexes = answerIndex.split(",");
        StringBuffer sb = new StringBuffer();
        for (String index: indexes) {
            sb.append(",").append(answerList.get(Integer.valueOf(index)));
        }
        return sb.replace(0, 1, "").toString();
    }

    private void setAuthCodePO(AuthCodePO authCodePO, JSONObject resultJson) {
        if (resultJson.getInteger("result_code") == 4) {
            // 验证成功
            authCodePO.setStatus(1);
        }
        if (resultJson.getInteger("result_code") == 5) {
            // 验证失败
            if (authCodePO.getStatus() == 1 || authCodePO.getStatus() == 2) {
                // 已经验证成功的记录下次却再一次验证失败
                authCodePO.setStatus(2);
            } else {
                int position = authCodePO.getCheckPosition();
                if (position < positionList.size() - 1) {
                    authCodePO.setCheckPosition(position + 1);
                    authCodePO.setStatus(0);
                } else {
                    authCodePO.setStatus(-1);
                }
            }
        }
    }
}
