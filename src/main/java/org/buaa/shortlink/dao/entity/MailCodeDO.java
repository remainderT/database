package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.buaa.shortlink.common.database.BaseDO;

import java.util.Date;

/**
 * 邮箱验证码持久层实体
 */
@Data
@Builder
@TableName("t_mail_code")
public class MailCodeDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 邮箱地址
     */
    private String mail;

    /**
     * 验证码
     */
    private String code;

    /**
     * 过期时间
     */
    private Date expireTime;

}
