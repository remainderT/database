package org.buaa.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_mail_code")
public class MailCodeDO {

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 是否已使用 0：未使用 1：已使用
     */
    private int used;

}
