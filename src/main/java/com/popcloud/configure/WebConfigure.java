package com.popcloud.configure;

import com.popcloud.interceptor.LoginRequiredInterceptor;
import com.popcloud.interceptor.PassportInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Component
public class WebConfigure implements WebMvcConfigurer {
    @Resource
    private PassportInterceptor passportInterceptor;

    @Resource
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/msg*");
    }
}
