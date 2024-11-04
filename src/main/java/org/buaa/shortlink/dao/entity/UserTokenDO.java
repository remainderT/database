package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.buaa.shortlink.common.database.BaseDO;

import java.util.Date;

/**
 * 用户token持久层实体
 */
@Data
@Builder
@TableName("t_user_token")
public class UserTokenDO extends BaseDO {

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
    * 过期时间
    */
    private Date expireTime;

}
