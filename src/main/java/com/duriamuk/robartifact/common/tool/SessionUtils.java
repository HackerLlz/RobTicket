package com.duriamuk.robartifact.common.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-21 17:13
 */
public class SessionUtils {
    public static Object get(String key){
        HttpServletRequest request = HttpUtils.getRequest();
        HttpSession session = request.getSession();
        return session.getAttribute(key);
    }

    public static void set(String key, Object val){
        HttpServletRequest request = HttpUtils.getRequest();
        HttpSession session = request.getSession();
        session.setAttribute(key, val);
    }

    public static void remove(String key){
        HttpServletRequest request = HttpUtils.getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute(key);
    }

    public static String getString(String key) {
        return (String) get(key);
    }

    public static Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }
}
