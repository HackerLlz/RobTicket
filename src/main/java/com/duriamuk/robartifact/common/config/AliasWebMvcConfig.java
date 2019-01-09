package com.duriamuk.robartifact.common.config;

import com.duriamuk.robartifact.common.paramAlias.AliasModelAttributeMethodProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * @author: DuriaMuk
 * @description: 将属性处理器添加到Spring中，模板引擎会无效
 * @create: 2018-12-14 16:39
 */
//@Configuration
public class AliasWebMvcConfig implements WebMvcConfigurer {
    @Bean
    protected AliasModelAttributeMethodProcessor processor() {
        return new AliasModelAttributeMethodProcessor(true);
    }

    /*
     * 这段代码一定要, 博客里面没有
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(processor());
    }
}
