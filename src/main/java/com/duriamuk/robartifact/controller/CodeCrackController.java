package com.duriamuk.robartifact.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.AjaxMessage;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.entity.DTO.code.CodeImage;
import com.duriamuk.robartifact.service.CodeCrackService;
import com.duriamuk.robartifact.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-18 15:13
 */
@Controller
@RequestMapping("/code")
public class CodeCrackController {
    private static final Logger logger = LoggerFactory.getLogger(CodeCrackController.class);
    private static final int RETRY_TIMES = 10;

    @Autowired
    private CodeCrackService codeCrackService;

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view() {
        return "code/view";
    }

    @RequestMapping(value = "getCode", method = RequestMethod.GET)
    @ResponseBody
    public String getCode() {
        logger.info("开始获得OCR成功的验证码");
        HttpUtils.clearRequestCookie();
        for(int i = 0;i < RETRY_TIMES;i ++) {
            CodeImage codeImage = codeCrackService.getOCRCode();
            if (!ObjectUtils.isEmpty(codeImage)) {
                return JSON.toJSONString(codeImage);
            }
        }
        return null;
    }

    @RequestMapping(value = "checkCode", method = RequestMethod.POST)
    @ResponseBody
    public String checkCode(@RequestBody String payload) {
        logger.info("开始验证并添加到图库，入参：{}", payload);
        CodeImage codeImage = JSON.parseObject(payload, CodeImage.class);
        for(int i = 0;i < RETRY_TIMES;i ++) {
            String result = loginService.checkCode(codeImage.getAnswer());
            if (result.startsWith("{")) {
                JSONObject resultJson = JSON.parseObject(result);
                if (resultJson.getInteger("result_code") == 4) {
                    int insertCount = codeCrackService.addCodeImage(codeImage);
                    return String.valueOf(insertCount);
                }
                break;
            }
        }
        return AjaxMessage.FAIL;
    }
}
