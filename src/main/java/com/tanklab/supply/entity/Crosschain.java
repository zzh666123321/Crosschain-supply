package com.tanklab.supply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 跨链信息表
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("crossTx_info")
@ApiModel(value="Crosschain对象", description="跨链交易信息表")
public class Crosschain implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "tx_id", type = IdType.AUTO)
    private int txId;
    private String username;
    private String status;
    private String contract;
    private String crossFrom;
    private String crossTo;
    //@ApiModelProperty(value = "跨链类型")
    private String crossType;
    private String txHash;
//    //@ApiModelProperty(value = "交易结果")
//    private Boolean crossResult;

//    //@ApiModelProperty(value = "交易时间")
//    private Date crossTime;




}
