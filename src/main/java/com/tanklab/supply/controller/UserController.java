package com.tanklab.supply.controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.tanklab.supply.ds.req.UserLoginReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.UserService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * <p>
 * 用户个人信息表 前端控制器
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@RestController
@RequestMapping("/supply/user")
public class UserController {
    @Autowired
    UserService userService;

    @ApiOperation(value="用户登录，前端传入账号密码")
    @PostMapping("/login")
    public CommonResp Login(@RequestBody UserLoginReq userloginreq) {
        return userService.login(userloginreq);
    }
}

