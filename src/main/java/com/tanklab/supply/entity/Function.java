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
@TableName("fun_info")
@ApiModel(value="function对象", description="函数信息表")
public class Function implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "fun_id", type = IdType.AUTO)
    private Integer funId;
    private String ipChain;
    private Integer port;
    private Integer chainNumber;
    private String  contractName;
    private String  contractAddress;
    private String  functionName;

    //private Boolean isProcessed;


}

