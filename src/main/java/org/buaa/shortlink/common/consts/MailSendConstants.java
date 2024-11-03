package org.buaa.shortlink.common.consts;

public class MailSendConstants {
    /**
     * 内容
     */
    public static final String TEXT = "[短链接] 邮箱注册验证码： %s，5分钟有效，请勿泄漏";

    /**
     * 主题
     */
    public static final String SUBJECT = "验证码";

    /**
     * 邮箱后缀
     */
    public static final String EMAIL_SUFFIX = "@buaa.edu.cn";

    /**
     * 验证码过期时间
     */
    public static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;      // 5分钟

}
