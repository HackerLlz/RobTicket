package com.example.frp.common.paramAlias;


import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.lang.reflect.Field;

/**
 * @author: DuriaMuk
 * @description: 自定义的数据绑定器
 * @create: 2018-12-14 16:16
 */
public class AliasDataBinder extends ExtendedServletRequestDataBinder {
    public AliasDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    /**
     * 复写addBindValues方法
     * @param mpvs 这里面存的就是请求参数的key-value对
     * @param request 请求本身, 这里没有用到
     */
    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);
        // 处理要绑定参数的对象
        Class<?> targetClass = getTarget().getClass();
        // 获取对象的所有字段(拿到Test类的字段)
        Field[] fields = targetClass.getDeclaredFields();
        // 处理所有字段
        for (Field field : fields) {
            // 原始字段上的注解
            ParamAlias paramAliasAnnotation = field.getAnnotation(ParamAlias.class);
            // 若参数中包含原始字段或者字段没有别名注解, 则跳过该字段
            if (ObjectUtils.isEmpty(paramAliasAnnotation) || mpvs.contains(field.getName()) ) {
                continue;
            }
            // 参数中没有原始字段且字段上有别名注解, 则依次取别名列表中的别名, 在参数中最先找到的别名的值赋值给原始字段
            for (String alias : paramAliasAnnotation.value()) {
                if (mpvs.contains(alias)) {
                    mpvs.add(field.getName(), mpvs.getPropertyValue(alias).getValue());
                    // 跳出循环防止取其它别名
                    break;
                }
            }
        }
    }
}
