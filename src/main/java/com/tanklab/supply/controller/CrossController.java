package com.tanklab.supply.controller;


import com.tanklab.supply.ds.req.UserLoginReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.CrossService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户个人信息表 前端控制器
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-12-18
 */
@RestController
@RequestMapping("/supply/cross")
@ApiModel
public class CrossController {
    @Autowired
    CrossService crossService;

    @ApiOperation(value="查询跨链结果")
    @GetMapping("/queryCrossRet/{oxId}")
    public CommonResp QueryCrossRet(
            @ApiParam(name="oxId",value = "牛肉序号", example = "1", required = true)
            @PathVariable("oxId") Long oxId
    ) {
        return crossService.queryCrossRet(oxId);
    }
}

