package org.buaa.shortlink.config;

import org.buaa.shortlink.cache.FlowLimitCache;
import org.buaa.shortlink.cache.UserTokenCache;
import org.buaa.shortlink.common.biz.user.LoginCheckFilter;
import org.buaa.shortlink.common.biz.user.RefreshTokenFilter;
import org.buaa.shortlink.common.biz.user.UserFlowRiskControlFilter;
import org.buaa.shortlink.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserConfiguration {

    @Value("${flow-limit.max-access-count}")
    private Long maxAccessCount;

    /**
     * 刷新 Token 过滤器
     */
    @Bean
    public FilterRegistrationBean<RefreshTokenFilter> globalRefreshTokenFilter(UserTokenCache tokenCache) {
        FilterRegistrationBean<RefreshTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RefreshTokenFilter(tokenCache));
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

    /**
     * 用户操作流量风控过滤器
     */
    @Bean
    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(FlowLimitCache flowLimitCache, UserMapper userMapper) {
        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserFlowRiskControlFilter(flowLimitCache, userMapper, maxAccessCount));
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }

}