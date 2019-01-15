package com.duriamuk.robartifact.common.tool;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-28 11:31
 */
public class StrUtils {
    /**
     * 查找字符串中第一个某个特征属性后面的值
     * @param name
     * @param afterName
     * @param interval
     * @param endStr
     * @param str
     * @return
     */
    public static String findVlaue(String name, String afterName, int interval, String endStr, String str) {
        String queryName = name + afterName;
        int queryIndex = str.indexOf(queryName);
        if (queryIndex < 0) {
            return null;
        }
        int start = queryIndex + queryName.length() + interval;
        if (endStr != null) {
            int end = str.indexOf(endStr, start);
            if  (end  > 0) {
                // start和end若为负数会变为0
                return str.substring(start, end);
            }
        }
        return str.substring(start);
    }

    /**
     * 查找字符串中第一个某个特征属性前面的值
     * @param name
     * @param beforeName
     * @param interval
     * @param beginStr
     * @param str
     * @return
     */
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
        }
        return str.substring(0, end);
    }
}
