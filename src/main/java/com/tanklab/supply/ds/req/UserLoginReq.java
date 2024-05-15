package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="用户登录请求统一格式")
public class UserLoginReq {
    @ApiModelProperty(value = "用户名")
    private String user_name;

    @ApiModelProperty(value = "密码")
    private String user_pswd;
}