package com.duriamuk.robartifact.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-19 13:19
 * Cookie 的常规操作
 */
public class CookieUtils {
    private static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    /**
     * 根据键值更新Cookie
     * @param cookie
     * @param name
     * @param value
     * @return
     */
    public static String updateCookie(String cookie, String name, String value) {
        if (cookie == null) {
            cookie = "";
        }
        if (cookie.contains(name)) {
            String oldValue = StrUtils.findVlaue(" " + name, "=", 0, ";", cookie);
            if (oldValue != null) {
                cookie = cookie.replaceFirst(oldValue, value);
                logger.info("更新Cookie已替换：{}={}", name, value);
                return cookie;
            }
        }
        String setCookie = "; " + name + "=" + value;
        cookie += setCookie;
        logger.info("更新Cookie已添加：{}={}", name, value);
        return cookie;
    }

    /**
     * 向response中添加默认的setCookie
     * @param name
     * @param value
     */
    public static void setCookie(String name, String value) {
        setCookie(name, value, null, null);
    }

    /**
     * 向response中添加setCookie
     * @param name
     * @param value
     */
    public static void setCookie(String name, String value, String path, Integer minute) {
        Cookie setCookie = null;
        try {
            setCookie = new Cookie(name, URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setCookie.setPath(path == null? "/": path);
        setCookie.setMaxAge(minute == null ? 1800 : minute * 60);    // -1为会话级
        HttpUtils.getReponse().addCookie(setCookie);
        logger.info("已添加Set-Cookie：{}={}; Path={}; MaxAge={}", name, value, setCookie.getPath(), setCookie.getMaxAge());
    }

    /**
     * 获取request中的cookiew
     * @param name
     * @return
     */
    public static String getCookie(String name) {
        String value = null;
        Cookie[] cookies = HttpUtils.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    logger.info("获取Cookie：{}={}", name, value);
                    break;
                }
            }
        }

        return value;
    }

    /**
     * 移除客户端cookie, 无论原来存不存在
     * @param name
     * @return
     */
    public static void removeCookie(String name) {
        setCookie(name, null, null, 0);
    }
}
