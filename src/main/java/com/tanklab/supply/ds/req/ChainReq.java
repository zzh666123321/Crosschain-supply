package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;



    @Data
    @ApiModel(value="区块链IP请求格式")
    public class ChainReq {

        @ApiModelProperty(value = "区块链IP")
        private String chainIP;


    }


