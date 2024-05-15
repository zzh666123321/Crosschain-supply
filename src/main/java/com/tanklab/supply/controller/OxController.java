package com.tanklab.supply.controller;


import com.tanklab.supply.ds.req.AddOxReq;
import com.tanklab.supply.ds.req.UserLoginReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.OxService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 牛信息表 前端控制器
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@RestController
@RequestMapping("/supply/ox")
public class OxController {
    @Autowired
    OxService oxService;

    @ApiOperation(value="【农场主】添加牛的信息")
    @PostMapping("/addOx")
    public CommonResp AddOx(@RequestBody AddOxReq addoxreq) {
        return oxService.addOx(addoxreq);
    }

    @ApiOperation(value = "查询所有牛的信息")
    @GetMapping("/queryOxen")
    public CommonResp QueryOxen(){
        return oxService.queryOxen();
    }

    @ApiOperation(value = "查询一头牛的信息")
    @GetMapping("/queryOneOx/{oxId}")
    public CommonResp QueryOneOx(
            @ApiParam(name = "oxId", value = "牛的编号", example = "1", required = true)
            @PathVariable("oxId") Long oxId
    ){
        return oxService.queryOneOx(oxId);
    }
}

