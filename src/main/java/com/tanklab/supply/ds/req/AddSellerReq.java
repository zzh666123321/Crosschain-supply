package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="添加卖方的请求格式")
public class AddSellerReq {

    @ApiModelProperty(value = "牛肉key hash")
    private String beefKey;

    @ApiModelProperty(value = "国内中转时间")
    private Date transferTime;

    @ApiModelProperty(value = "国内中转地（中国某市某区某地……）")
    private String transferPlace;

    @ApiModelProperty(value = "国内中转负责人")
    private String transferPerson;

    @ApiModelProperty(value = "国内售卖地（中国某市某区某商场……）")
    private String sellPlace;

    @ApiModelProperty(value = "售卖价格（单位：元）")
    private Integer price;

    @ApiModelProperty(value = "净重（单位：kg）")
    private Integer weight;

    @ApiModelProperty(value = "保质期（过期的日期）")
    private Date qualityGuaranteeTime;
}
