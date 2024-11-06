package org.buaa.shortlink.dto.req;

import lombok.Data;

/**
 * 更新用户信息请求参数
 */
@Data
public class UserUpdateReqDTO {

    /**
     * 旧用户名
     */
    private String oldUsername;

    /**
     * 新用户名
     */
    private String newUsername;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String mail;


}
