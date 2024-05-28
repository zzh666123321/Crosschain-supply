package com.tanklab.supply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("contract_info")
@ApiModel(value="Contract对象", description="合约信息表")
public class Contract implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "contract_id", type = IdType.AUTO)
    private Integer contractId;
    private String ipChain;
    private Integer port;
    private Integer chainNumber;
    private String  contractName;
    private String  contractAddress;

    //private Boolean isProcessed;



}