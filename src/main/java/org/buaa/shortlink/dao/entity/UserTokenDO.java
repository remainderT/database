package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 用户token持久层实体
 */
@Data
@Builder
@TableName("t_user_token")
public class UserTokenDO {

    /**
    * id
    */
    private Long id;

    /**
    * 用户名
    */
    private String username;

    /**
    * token
    */
    private String token;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
    * 过期时间
    */
    private Date expireTime;

}
