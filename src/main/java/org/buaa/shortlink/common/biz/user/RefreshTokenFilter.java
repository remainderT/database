package org.buaa.shortlink.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.buaa.shortlink.dao.entity.UserTokenDO;
import org.buaa.shortlink.dao.mapper.UserTokenMapper;

import java.io.IOException;
import java.util.Date;

import static org.buaa.shortlink.common.consts.UserConstants.TOKEN_EXPIRE_TIME;

/**
 * 刷新 Token 过滤器
 */
@RequiredArgsConstructor
public class RefreshTokenFilter implements Filter {

    private final UserTokenMapper userTokenMapper;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        if (StrUtil.isBlank(token) || StrUtil.isBlank(username)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        LambdaQueryWrapper<UserTokenDO> queryWrapper = Wrappers.lambdaQuery(UserTokenDO.class)
                .eq(UserTokenDO::getUsername, username)
                .eq(UserTokenDO::getToken, token)
                .eq(UserTokenDO::getDelFlag, 0)
                .gt(UserTokenDO::getExpireTime, new Date());
        UserTokenDO userTokenDO = userTokenMapper.selectOne(queryWrapper);
        if (userTokenDO == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        UserInfoDTO userInfoDTO = JSON.parseObject(JSON.toJSONString(userTokenDO), UserInfoDTO.class);
        userInfoDTO.setToken(token);
        UserContext.setUser(userInfoDTO);
        userTokenDO.setExpireTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME));
        userTokenMapper.updateById(userTokenDO);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

}