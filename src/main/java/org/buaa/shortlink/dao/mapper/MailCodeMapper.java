package org.buaa.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.buaa.shortlink.dao.entity.MailCodeDO;

public interface MailCodeMapper extends BaseMapper<MailCodeDO> {

    /**
     * 根据邮箱查询验证码
     *
     * @param mail 邮箱
     * @return 验证码
     */
    @Select("SELECT code FROM t_mail_code WHERE mail = #{mail} ORDER BY create_time DESC LIMIT 1")
    String selectCodeByMail(String mail);


    /**
     * 根据邮箱查询验证码是否过期
     *
     * @param mail 邮箱
     * @return true:过期 false:未过期
     */
    @Select("SELECT IFNULL((SELECT 1 FROM t_mail_code WHERE mail = #{mail} AND expire_time <= NOW() ORDER BY create_time DESC LIMIT 1), 0)")
    Boolean selectCodeIsExpired(String mail);

}
