package org.buaa.shortlink.common.biz.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户身份
     */
    private int isVip;

    /**
     * Token
     */
    private String token;
}