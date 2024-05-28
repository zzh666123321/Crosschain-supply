package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;




    @Data
    @ApiModel(value="跨链请求格式")
    public class CrossReq {

        @ApiModelProperty(value = "交易参数")
        private int param;
        @ApiModelProperty(value = "交易起始链")
        private int crossFrom;
        @ApiModelProperty(value = "交易目的链")
        private int crossTo;
        @ApiModelProperty(value = "跨链类型")
        private int crossType;




    }
