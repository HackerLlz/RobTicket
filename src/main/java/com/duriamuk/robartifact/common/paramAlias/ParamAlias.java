package com.duriamuk.robartifact.common.paramAlias;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: DuriaMuk
 * @description: 自定义参数别名注解
 * @create: 2018-12-14 16:14
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamAlias{
    /**
     * 参数别名列表
     */
    String[] value() default "";
}
