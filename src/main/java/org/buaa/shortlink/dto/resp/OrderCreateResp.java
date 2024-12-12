package org.buaa.shortlink.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单创建响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResp {

    /**
     * id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 价格
     */
    private String amount;

}
