package com.duriamuk.robartifact.common.tool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-19 13:19
 */
public class CookieUtils {
    public static void setCookie(HttpServletResponse response, String name, String value, String path, Integer minute) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        cookie.setPath(path);    // null为"/"
        cookie.setMaxAge(minute == null ? -1 : minute * 60);    // -1为会话级
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return value;
    }

    public static String removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }

            }
        }
        return value;
    }
}
