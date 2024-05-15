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
 * 牛肉信息表
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Beef对象", description="牛肉信息表")
public class Beef implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "beef_id", type = IdType.AUTO)
    private Long beefId;

    private String beefKey;

    private Long oxId;

    private String breed;

    private Date processTime;

    private String processPlace;

    private String processPerson;

    private Date transportTime;

    private String transportPlace;

    private String transportPerson;

    private Date transferTime;

    private String transferPlace;

    private String transferPerson;

    private String sellPlace;

    private Integer price;

    private Integer weight;

    private Date qualityGuaranteeTime;

    private String transactionIdC0;

    private String transactionIdC1;


}
