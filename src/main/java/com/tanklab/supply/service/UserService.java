package com.tanklab.supply.service;

import com.tanklab.supply.ds.req.UserLoginReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户个人信息表 服务类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
public interface UserService extends IService<User> {
    CommonResp login(UserLoginReq userloginreq);
}
