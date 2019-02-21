package com.duriamuk.robartifact.common.tool;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-10 20:58
 */
public class ClassUtils {
    /**
     * 获取首字母小写的类名
     *
     * @param clazz
     * @return
     */
    public static String getLowerCaseClassName(Class clazz) {
        String className = clazz.getSimpleName();
        String firstLetter = className.substring(0, 1);
        String lowerFirstLetter = firstLetter.toLowerCase();
        return lowerFirstLetter + className.substring(1);
    }
}
