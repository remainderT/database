package org.buaa.shortlink.common.enums;

import org.buaa.shortlink.common.convention.errorcode.IErrorCode;

public enum ServiceErrorCodeEnum implements IErrorCode {

    MAIL_SEND_ERROR("B000101", "邮件发送错误"),

    GROUP_GENERATE_ERROR("B000102", "短链接分组生成频繁"),

    SHORT_LINK_GENERATE_ERROR("B000103", "短链接生成频繁"),

    SHORT_LINK_GO_TO_ERROR("B000104", "短链接跳转错误");

    private final String code;

    private final String message;

    ServiceErrorCodeEnum(String code, String message) {
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
