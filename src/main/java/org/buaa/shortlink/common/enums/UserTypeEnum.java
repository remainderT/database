package org.buaa.shortlink.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户类型
 */
@RequiredArgsConstructor
public enum UserTypeEnum {

    /**
     * 普通用户
     */
    ORDINARY(0),

    /**
     * vip用户
     */
    VIP(1);

    @Getter
    private final int type;

}
