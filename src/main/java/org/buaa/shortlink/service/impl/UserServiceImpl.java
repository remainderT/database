package org.buaa.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.biz.user.UserContext;
import org.buaa.shortlink.common.consts.MailSendConstants;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.dao.entity.MailCodeDO;
import org.buaa.shortlink.dao.entity.UserDO;
import org.buaa.shortlink.dao.entity.UserTokenDO;
import org.buaa.shortlink.dao.mapper.MailCodeMapper;
import org.buaa.shortlink.dao.mapper.UserMapper;
import org.buaa.shortlink.dao.mapper.UserTokenMapper;
import org.buaa.shortlink.dto.req.UserLoginReqDTO;
import org.buaa.shortlink.dto.req.UserRegisterReqDTO;
import org.buaa.shortlink.dto.req.UserUpdateReqDTO;
import org.buaa.shortlink.dto.resp.UserLoginRespDTO;
import org.buaa.shortlink.dto.resp.UserRespDTO;
import org.buaa.shortlink.service.GroupService;
import org.buaa.shortlink.service.UserService;
import org.buaa.shortlink.toolkit.RandomGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static org.buaa.shortlink.common.consts.MailSendConstants.CODE_EXPIRE_TIME;
import static org.buaa.shortlink.common.consts.UserConstants.DEFAULT_GROUP_NAME;
import static org.buaa.shortlink.common.consts.UserConstants.TOKEN_EXPIRE_TIME;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.MAIL_SEND_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_CODE_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_CODE_EXPIRED;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_CODE_NULL;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_EXIST;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_NULL;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_PASSWORD_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_REPEATED_LOGIN;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_TOKEN_NULL;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_UPDATE_ERROR;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final JavaMailSender mailSender;
    private final MailCodeMapper mailCodeMapper;
    private final UserTokenMapper userTokenMapper;
    private final GroupService groupService;


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
    public Boolean hasUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        return userDO != null;
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
        } catch (Exception e) {
            throw new ServiceException(MAIL_SEND_ERROR);
        }
        MailCodeDO mailCodeDO = MailCodeDO.builder()
                .mail(mail)
                .code(code)
                .expireTime(new Date(System.currentTimeMillis() + CODE_EXPIRE_TIME))
                .build();
        mailCodeMapper.insert(mailCodeDO);
        return true;
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        String code = requestParam.getCode();
        if (code == null) {
            throw new ClientException(USER_CODE_NULL);
        }
        String cacheCode = mailCodeMapper.selectCodeByMail(requestParam.getMail());
        if (!Objects.equals(code, cacheCode)) {
            throw new ClientException(USER_CODE_ERROR);
        }
        if (mailCodeMapper.selectCodeIsExpired(requestParam.getMail())) {
            throw new ClientException(USER_CODE_EXPIRED);
        }
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        try {
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if (inserted < 1) {
                throw new ClientException(USER_SAVE_ERROR);
            }
            groupService.saveGroup(DEFAULT_GROUP_NAME);
        } catch (DuplicateKeyException ex) {
            throw new ClientException(USER_EXIST);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NULL);
        }
        LambdaQueryWrapper<UserDO> userQueryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        UserDO userDO = baseMapper.selectOne(userQueryWrapper);
        if (!Objects.equals(userDO.getPassword(), requestParam.getPassword())) {
            throw new ClientException(USER_PASSWORD_ERROR);
        }
        LambdaQueryWrapper<UserTokenDO> tokenQueryWrapper = Wrappers.lambdaQuery(UserTokenDO.class)
                .eq(UserTokenDO::getUsername, requestParam.getUsername())
                .gt(UserTokenDO::getExpireTime, new Date());
        UserTokenDO tokenDO = userTokenMapper.selectOne(tokenQueryWrapper);
        if (tokenDO != null) {
            throw new ClientException(USER_REPEATED_LOGIN);
        }
        String uuid = UUID.randomUUID().toString();
        UserTokenDO userTokenDO = UserTokenDO.builder()
                .username(requestParam.getUsername())
                .token(uuid)
                .expireTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME))
                .build();
        userTokenMapper.insert(userTokenDO);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            LambdaUpdateWrapper<UserTokenDO> updateWrapper = Wrappers.lambdaUpdate(UserTokenDO.class)
                    .eq(UserTokenDO::getUsername, username)
                    .eq(UserTokenDO::getToken, token)
                    .gt(UserTokenDO::getExpireTime, new Date())
                    .eq(UserTokenDO::getDelFlag, 0);
            UserTokenDO userTokenDO= UserTokenDO.builder()
                    .build();
            userTokenDO.setDelFlag(1);
            userTokenMapper.update(userTokenDO, updateWrapper);
            return;
        }
        throw new ClientException(USER_TOKEN_NULL);
    }


    @Override
    public Boolean checkLogin(String username, String token) {
        LambdaQueryWrapper<UserTokenDO> tokenQueryWrapper = Wrappers.lambdaQuery(UserTokenDO.class)
                .eq(UserTokenDO::getUsername, username)
                .eq(UserTokenDO::getToken, token)
                .gt(UserTokenDO::getExpireTime, new Date())
                .eq(UserTokenDO::getDelFlag, 0);
        UserTokenDO tokenDO = userTokenMapper.selectOne(tokenQueryWrapper);
        if (tokenDO != null) {
            return true;
        }
        return false;
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        if (!Objects.equals(requestParam.getOldUsername(), UserContext.getUsername())) {
            throw new ClientException(USER_UPDATE_ERROR);
        }
        if (!requestParam.getOldUsername().equals(requestParam.getNewUsername()) && hasUsername(requestParam.getNewUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        UserDO userDO = UserDO.builder().
                username(requestParam.getNewUsername()).
                password(requestParam.getPassword()).
                mail(requestParam.getMail()).
                build();
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
            .eq(UserDO::getUsername, requestParam.getOldUsername());
        baseMapper.update(userDO, updateWrapper);
        // 修改token表中的username
        if (!Objects.equals(requestParam.getOldUsername(), requestParam.getNewUsername())) {
            LambdaUpdateWrapper<UserTokenDO> tokenUpdateWrapper = Wrappers.lambdaUpdate(UserTokenDO.class)
                    .eq(UserTokenDO::getUsername, requestParam.getOldUsername())
                    .eq(UserTokenDO::getDelFlag, 0)
                    .gt(UserTokenDO::getExpireTime, new Date());
            UserTokenDO userTokenDO = UserTokenDO.builder()
                    .username(requestParam.getNewUsername())
                    .build();
            userTokenMapper.update(userTokenDO, tokenUpdateWrapper);
        }
    }

}
