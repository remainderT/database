package org.buaa.shortlink.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.buaa.shortlink.cache.FlowLimitCache;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.result.Results;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_FLOW_LIMIT;

/**
 * 用户操作流量风控过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class UserFlowRiskControlFilter implements Filter {

    private final FlowLimitCache flowLimitCache;

    private final Long maxAccessCount = 2L;

    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/user/login",
            "/api/short-link/user/send-code"
    );


    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (needCheck(request)) {
            String username = UserContext.getUsername();
            int count = flowLimitCache.incrAndGet(username);
            if (count > maxAccessCount) {
                log.error("用户请求流量超限: " + username);
                sendFlowLimitResponse((HttpServletResponse) httpResponse);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public Boolean needCheck(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!IGNORE_URI.contains(requestURI) && !(requestURI.length() == 7)) {
            String method = httpServletRequest.getMethod();
            if (!(Objects.equals(requestURI, "/api/short-link/user") && Objects.equals(method, "POST"))) {
                return true;
            }
        }
        return false;
    }

    private void sendFlowLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(Results.failure(new ClientException(USER_FLOW_LIMIT))));
        writer.flush();
        writer.close();
    }

}
