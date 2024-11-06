package org.buaa.shortlink.common.enums;

import org.buaa.shortlink.common.convention.errorcode.IErrorCode;

/**
 * 用户错误码
 */
public enum UserErrorCodeEnum implements IErrorCode {

    USER_NAME_EXIST("A000101", "用户名已存在"),

    USER_EXIST("A000102", "用户记录已存在"),

    USER_SAVE_ERROR("A000103", "用户记录新增失败"),

    USER_CODE_ERROR("A000104", "验证码错误"),

    USER_CODE_NULL("A000105", "验证码为空"),

    USER_CODE_EXPIRED("A000106", "验证码已过期"),

    USER_NAME_NULL("A000201", "用户名不存在"),

    USER_PASSWORD_ERROR("A000202", "密码错误"),

    USER_REPEATED_LOGIN("A000203", "重复登录"),

    USER_TOKEN_NULL("A000204", "用户未登录"),

    USER_NULL("A000301", "用户记录不存在"),

    USER_UPDATE_ERROR("A000302", "用户信息更新失败"),

    GROUP_SIZE_LIMIT("A000401", "分组数量超过限制");


    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
