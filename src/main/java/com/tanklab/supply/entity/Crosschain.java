package com.tanklab.supply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@ApiModel(value="Crosschain对象", description="跨链信息表")
public class Crosschain implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "cross_id", type = IdType.AUTO)
    private Long crossId;

    private Long oxId;

    private String beefKey;

    private String crossFrom;

    private String crossTo;

    private String txFrom;

    private String txTo;

    @ApiModelProperty(value = "跨链类型")
    private Boolean crossType;

    @ApiModelProperty(value = "交易结果")
    private Boolean crossResult;

    @ApiModelProperty(value = "交易时间")
    private Date crossTime;

    private String txBack;


}
