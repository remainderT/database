package org.buaa.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.buaa.shortlink.common.convention.result.Result;
import org.buaa.shortlink.common.convention.result.Results;
import org.buaa.shortlink.dto.resp.UserRespDTO;
import org.buaa.shortlink.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/short-link/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 查询用户名是否存在
     */
    @GetMapping("/api/short-link/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 发送验证码
     */
    @PostMapping("/api/short-link/user/send-code")
    public Result<Boolean> sendCode(@RequestParam("mail") String mail) {
        return Results.success(userService.sendCode(mail));
    }



}
