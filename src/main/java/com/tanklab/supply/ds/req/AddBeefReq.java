package com.tanklab.supply.ds.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="国外加工牛肉信息的请求格式")
public class AddBeefReq {

    @ApiModelProperty(value = "牛的序号")
    private Long oxId;

    @ApiModelProperty(value = "加工份数")
    private Integer numberOfBeef;

    @ApiModelProperty(value = "牛肉品种")
    private String breed;

    @ApiModelProperty(value = "加工时间")
    private Date processTime;

    @ApiModelProperty(value = "加工地点")
    private String processPlace;

    @ApiModelProperty(value = "加工负责人")
    private String processPerson;

    @ApiModelProperty(value = "转运地")
    private Date transportTime;

    @ApiModelProperty(value = "转运时间")
    private String transportPlace;

    @ApiModelProperty(value = "转运负责人")
    private String transportPerson;
}
