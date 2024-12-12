package org.buaa.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.buaa.shortlink.dao.entity.OrderDO;
import org.buaa.shortlink.dto.resp.OrderCreateResp;

/**
 * 订单接口层
 */
public interface OrderService extends IService<OrderDO> {

    /**
     * 创建订单
     */
    OrderCreateResp createOrder();

    /**
     * 支付订单
     */
    void payOrder(String id, HttpServletResponse httpResponse);

    /**
     * 支付宝回调通知
     */
    void notify(HttpServletRequest request);

}
