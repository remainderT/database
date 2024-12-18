package org.buaa.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.cache.MailCodeCache;
import org.buaa.shortlink.cache.UserTokenCache;
import org.buaa.shortlink.common.biz.user.UserContext;
import org.buaa.shortlink.common.consts.MailSendConstants;
import org.buaa.shortlink.common.convention.exception.ClientException;
import org.buaa.shortlink.common.convention.exception.ServiceException;
import org.buaa.shortlink.dao.entity.UserDO;
import org.buaa.shortlink.dao.mapper.UserMapper;
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

import java.util.Objects;

import static org.buaa.shortlink.common.consts.UserConstants.DEFAULT_GROUP_NAME;
import static org.buaa.shortlink.common.enums.ServiceErrorCodeEnum.MAIL_SEND_ERROR;
import static org.buaa.shortlink.common.enums.UserErrorCodeEnum.USER_CODE_ERROR;
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
public class  UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final JavaMailSender mailSender;
    private final MailCodeCache mailCodeCache;
    private final UserTokenCache tokenCache;
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
        mailCodeCache.put(mail, code);
        return true;
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        String code = requestParam.getCode();
        if (code == null) {
            throw new ClientException(USER_CODE_NULL);
        }
        String cacheCode = mailCodeCache.get(requestParam.getMail());
        if (!Objects.equals(code, cacheCode)) {
            throw new ClientException(USER_CODE_ERROR);
        }
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        try {
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if (inserted < 1) {
                throw new ClientException(USER_SAVE_ERROR);
            }
            groupService.saveGroup(DEFAULT_GROUP_NAME, requestParam.getUsername());
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
        String oldToken = tokenCache.get(requestParam.getUsername());
        if (oldToken != null) {
            throw new ClientException(USER_REPEATED_LOGIN);
        }
        String uuid = UUID.randomUUID().toString();
        tokenCache.put(requestParam.getUsername(), uuid);

        return new UserLoginRespDTO(uuid);
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            tokenCache.evict(username);
        }
        throw new ClientException(USER_TOKEN_NULL);
    }


    @Override
    public Boolean checkLogin(String username, String token) {
        return Objects.equals(tokenCache.get(username), token);
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
            String token = tokenCache.get(requestParam.getOldUsername());
            tokenCache.put(requestParam.getNewUsername(), token);
        }
    }

    @Override
    public void upGrade() {
        UserDO userDO = UserDO.builder()
                .isVip(1)
                .build();
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, UserContext.getUsername());
        baseMapper.update(userDO, updateWrapper);
    }

}
