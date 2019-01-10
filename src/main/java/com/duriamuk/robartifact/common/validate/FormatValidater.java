package com.duriamuk.robartifact.common.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 15:14
 */
public class FormatValidater {
    private static final Logger logger = LoggerFactory.getLogger(FormatValidater.class);
    private static final String EMAIL_FORMAT = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    public static Boolean emailValidate(String data) {
        return doValidate(data, EMAIL_FORMAT);
    }

    private static Boolean doValidate(String data, String format) {
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(data);
        boolean isValidate = matcher.matches();
        logger.info("{}格式校验{}", data, isValidate ? "成功" : "失败");
        return isValidate;
    }
}
