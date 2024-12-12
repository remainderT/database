package org.buaa.shortlink.service.impl;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.buaa.shortlink.common.biz.user.UserContext;
import org.buaa.shortlink.config.AliPayConfig;
import org.buaa.shortlink.dao.entity.OrderDO;
import org.buaa.shortlink.dao.mapper.OrderMapper;
import org.buaa.shortlink.dto.resp.OrderCreateResp;
import org.buaa.shortlink.service.OrderService;
import org.buaa.shortlink.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单接口实现层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl  extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {

    private final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    private final String CHARSET = "UTF-8";
    private final String SIGN_TYPE = "RSA2";
    private final AliPayConfig aliPayConfig;
    private final UserService userService;

    @Override
    public OrderCreateResp createOrder() {
        String username = UserContext.getUsername();
        OrderDO orderDO = OrderDO.builder()
                .username(username)
                .goodsName("永久vip")
                .amount("1.00")
                .status("未支付")
                .build();
        baseMapper.insert(orderDO);
        return OrderCreateResp.builder()
                .id(String.valueOf(orderDO.getId()))
                .username(orderDO.getUsername())
                .goodsName(orderDO.getGoodsName())
                .amount(orderDO.getAmount())
                .build();
    }

    @Override
    @SneakyThrows
    public void payOrder(String id, HttpServletResponse httpResponse)  {
        OrderDO orderDO = baseMapper.selectById(id);
        if (orderDO == null) {
            return;
        }
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orderDO.getId());
        bizContent.set("total_amount", orderDO.getAmount());
        bizContent.set("subject", orderDO.getGoodsName());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8000/api/short-link/order/success"); // 支付完成后自动跳转到本地页面的路径

        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @Override
    @SneakyThrows
    public void notify(HttpServletRequest request) {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            log.info("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8");
            // 支付宝验签
            if (checkSignature) {
                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");
                OrderDO orderDO = baseMapper.selectById(Long.parseLong(tradeNo));
                orderDO.setStatus("已支付");
                orderDO.setPayTime(gmtPayment);
                orderDO.setPayNo(alipayTradeNo);
                baseMapper.updateById(orderDO);
                userService.upGrade();
            }
        }
    }
}