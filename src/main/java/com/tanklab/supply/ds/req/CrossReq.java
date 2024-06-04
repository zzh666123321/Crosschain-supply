package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;




    @Data
    @ApiModel(value="跨链请求格式")
    public class CrossReq {

        @ApiModelProperty(value = "原链IP")
        private String srcIp;
        @ApiModelProperty(value = "目的链IP")
        private String dstIp;
        @ApiModelProperty(value = "源链类型")
        private String srcChain;
        @ApiModelProperty(value = "目的链类型")
        private String dstChain;




    }
