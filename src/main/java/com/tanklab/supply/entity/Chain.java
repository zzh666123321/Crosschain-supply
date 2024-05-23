package com.tanklab.supply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 链信息表
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Chain对象", description="链信息表")
public class Chain implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "chain_id", type = IdType.AUTO)
    private Long chainId;
    private String chainIp;

    private Integer chainStatus;

    private Integer chainType;

    private Integer ownerId;

    //private Boolean isProcessed;



}
