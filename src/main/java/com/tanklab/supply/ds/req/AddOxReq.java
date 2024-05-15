package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="添加牛信息的请求格式")
public class AddOxReq {

    @ApiModelProperty(value = "牛的品种")
    private String breed;

    @ApiModelProperty(value = "出栏时间")
    private Date endTime;

    @ApiModelProperty(value = "出栏周期（单位：天）")
    private Integer feedingPeriod;

    @ApiModelProperty(value = "体重（单位：kg）")
    private Integer weight;

    @ApiModelProperty(value = "饲养地（某国某市某区某农场……）")
    private String location;

    @ApiModelProperty(value = "饲养负责人")
    private String feedPerson;
}
