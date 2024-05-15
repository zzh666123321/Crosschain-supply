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
 * 牛信息表
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ox对象", description="牛信息表")
public class Ox implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ox_id", type = IdType.AUTO)
    private Long oxId;

    private String oxKey;

    private Boolean isProcessed;

    private String breed;

    private Date endTime;

    private Integer feedingPeriod;

    private Integer weight;

    private String location;

    private String feedPerson;

    private String transactionId;


}
