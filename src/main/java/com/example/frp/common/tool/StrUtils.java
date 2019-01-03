package com.example.frp.common.tool;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-28 11:31
 */
public class StrUtils {
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

    public static String reverseFindVlaue(String name, String beforeName, int interval, String beginStr, String str) {
        String queryName = beforeName + name;
        int end = str.indexOf(queryName) - interval;
        if (end < 0) {
            return null;
        }
        if (beginStr != null) {
            String subStr = str.substring(0, end);
            int start = subStr.lastIndexOf(beginStr) + 1;
            return str.substring(start, end);
        } else {
            return str.substring(0, end);
        }
    }
}
