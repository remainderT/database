package org.buaa.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.consts.MailSendConstants;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.dao.entity.MailCodeDO;
import org.buaa.shortlink.dao.entity.UserDO;
import org.buaa.shortlink.dao.mapper.MailCodeMapper;
import org.buaa.shortlink.dao.mapper.UserMapper;
import org.buaa.shortlink.dto.resp.UserRespDTO;
import org.buaa.shortlink.service.UserService;
import org.buaa.shortlink.toolkit.RandomGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.buaa.shortlink.common.consts.MailSendConstants.CODE_EXPIRE_TIME;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.MAIL_SEND_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_NULL;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final JavaMailSender mailSender;
    private final MailCodeMapper mailCodeMapper;

    @Value("${spring.mail.username}")
    private String from;


    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ServiceException(USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean sendCode(String mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        String code = RandomGenerator.generateSixDigitCode();
        message.setFrom(from);
        message.setText(String.format(MailSendConstants.TEXT, code));
        message.setTo(mail);
        message.setSubject(MailSendConstants.SUBJECT);
        try {
            mailSender.send(message);
            MailCodeDO mailCodeDO = new MailCodeDO();
            mailCodeDO.setMail(mail);
            mailCodeDO.setCode(code);
            mailCodeDO.setCreateTime(new Date());
            mailCodeDO.setExpireTime(new Date(System.currentTimeMillis() + CODE_EXPIRE_TIME));
            mailCodeMapper.insert(mailCodeDO);
            return true;
        } catch (Exception e) {
            throw new ServiceException(MAIL_SEND_ERROR);
        }
    }




}
