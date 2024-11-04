package org.buaa.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.buaa.shortlink.dao.entity.UserDO;
import org.buaa.shortlink.dto.req.UserLoginReqDTO;
import org.buaa.shortlink.dto.req.UserRegisterReqDTO;
import org.buaa.shortlink.dto.resp.UserLoginRespDTO;
import org.buaa.shortlink.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 发送验证码
     */
    Boolean sendCode(String mail);

    /**
     * 查询用户名是否存在
     *
     * @param username
     * @return
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     *
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 用户登录请求参数
     * @return 用户登录返回参数 Token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);


    /**
     * 检查用户是否登录
     *
     * @param username 用户名
     * @param token    用户登录 Token
     */
    Boolean checkLogin(String username, String token);
}
