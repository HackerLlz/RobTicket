package com.example.frp.common.tool;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-04 10:23
 */
public class ThreadLocalUtils {
    // 将自己作为弱引用，和添加的值一起放入线程的ThreadLocalMap中，一个ThreadLocal维护一个值
    private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

    public static void set(String data) {
        threadLocal.set(data);
    }

    public static String get() {
        return (String) threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
