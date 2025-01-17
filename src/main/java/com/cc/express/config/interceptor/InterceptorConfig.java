package com.cc.express.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/user/refresh")
                .addPathPatterns("/user/get/{id}")
                .addPathPatterns("/user/test")// 其他接口token验证
                .excludePathPatterns("/graph/all")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/registration");  // 所有用户都放行
    }
}
