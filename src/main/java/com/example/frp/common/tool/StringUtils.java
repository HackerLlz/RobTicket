package com.example.frp.common.tool;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-28 11:31
 */
public class StringUtils {
    public static String findVlaue(String name, String afterName, int interval, String endStr, String str) {
        String queryName = name + afterName;
        int start = str.indexOf(queryName) + queryName.length() + interval;
        if (endStr != null) {
            int end = str.indexOf(endStr, start);
            return str.substring(start, end);
        } else {
            return str.substring(start);
        }
    }
}
