package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.buaa.shortlink.common.database.BaseDO;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDO extends BaseDO {

    /**
     * id
     */
    private Long id;

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

    /**
     * 支付状态
     */
    private String status;

    /**
     * 支付编号
     */
    private String payNo;

    /**
     * 支付时间
     */
    private String payTime;

}
