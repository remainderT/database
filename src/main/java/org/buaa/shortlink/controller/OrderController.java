package org.buaa.shortlink.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.dto.resp.OrderCreateResp;
import org.buaa.shortlink.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制层
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/api/short-link/order/create")
    public Result<OrderCreateResp> createOrder() {
        return Results.success(orderService.createOrder());
    }

    /**
     * 支付订单
     */
    @GetMapping("/api/short-link/order/pay")
    public void payOrder(String id, HttpServletResponse httpResponse) {
        orderService.payOrder(id, httpResponse);
    }

    /**
     * 支付宝回调通知
     */
    @PostMapping("/api/short-link/order/notify")
    public void notify(HttpServletRequest request) {
        orderService.notify(request);
    }

    @GetMapping("/api/short-link/order/success")
    public void success() {
        System.out.println("here");
    }

}
