package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import org.buaa.shortlink.common.database.BaseDO;
import lombok.Data;

/**
 * 用户持久层实体
 */
@Data
@Builder
@TableName("t_user")
public class UserDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 用户身份
     */
    private int isVip;

    /**
     * 注销时间戳
     */
    private Long deletionTime;
}
