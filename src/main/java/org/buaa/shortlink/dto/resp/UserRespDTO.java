package org.buaa.shortlink.dto.resp;

import lombok.Data;

/**
 * 用户返回参数响应
 */
@Data
public class UserRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 邮箱
     */
    private String mail;
}
