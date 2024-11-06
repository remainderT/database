package org.buaa.shortlink.config;

import org.buaa.shortlink.common.biz.user.LoginCheckFilter;
import org.buaa.shortlink.common.biz.user.RefreshTokenFilter;
import org.buaa.shortlink.dao.mapper.UserTokenMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    /**
     * 刷新 Token 过滤器
     */
    @Bean
    public FilterRegistrationBean<RefreshTokenFilter> globalRefreshTokenFilter(UserTokenMapper userTokenMapper) {
        FilterRegistrationBean<RefreshTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RefreshTokenFilter(userTokenMapper));
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }

    /**
     * 登录校验拦截器
     */
    @Bean
    public FilterRegistrationBean<LoginCheckFilter> globalLogicCheckFilter() {
        FilterRegistrationBean<LoginCheckFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoginCheckFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

}