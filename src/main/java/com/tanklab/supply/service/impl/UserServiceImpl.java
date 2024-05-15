package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanklab.supply.common.ResultCode;
import com.tanklab.supply.ds.req.UserLoginReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.User;
import com.tanklab.supply.mapper.UserMapper;
import com.tanklab.supply.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户个人信息表 服务实现类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public CommonResp login(UserLoginReq userloginreq){
        CommonResp userloginresp = new CommonResp();

        String username = userloginreq.getUser_name();
        String userpswd = userloginreq.getUser_pswd();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name",username);
        List<User> users = userMapper.selectList(wrapper);
        if (users.size() != 1){
            userloginresp.setRet(ResultCode.LOGIN_FAIL);
        } else {
            User user = users.get(0);
            if (user.getUserPswd().compareTo(userpswd) == 0){
                userloginresp.setRet(ResultCode.SUCCESS);
                JSONObject data = new JSONObject();
                data.put("token",user.getToken());
                data.put("authority",user.getAuthority());
                userloginresp.setData(data);
            }else {
                userloginresp.setRet(ResultCode.PSWD_ERROR);
            }
        }

        return userloginresp;
    }
}
