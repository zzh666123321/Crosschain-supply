package com.tanklab.supply.controller;


import com.tanklab.supply.ds.req.AddBeefReq;
import com.tanklab.supply.ds.req.AddOxReq;
import com.tanklab.supply.ds.req.AddSellerReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.BeefService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 牛肉信息表 前端控制器
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@RestController
@RequestMapping("/supply/beef")
public class BeefController {
    @Autowired
    BeefService beefService;

    @ApiOperation(value="【国外加工运输企业】将某头牛加工成肉，同时填写运输信息（简化）")
    @PostMapping("/addBeef")
    public CommonResp AddBeef(@RequestBody AddBeefReq addbeefreq) {
        return beefService.addBeef(addbeefreq);
    }

    @ApiOperation(value = "查询某头牛加工成的所有肉的信息")
    @GetMapping("/queryOxBeef/{oxId}")
    public CommonResp QueryOxBeef(
            @ApiParam(name = "oxId", value = "牛的编号", example = "1", required = true)
            @PathVariable("oxId") Long oxId
    ){
        return beefService.queryOxBeef(oxId);
    }

    @ApiOperation(value = "查询所有牛肉的信息")
    @GetMapping("/queryBeef")
    public CommonResp QueryBeef(){
        return beefService.queryBeef();
    }

    @ApiOperation(value = "查询一片牛肉的信息")
    @GetMapping("/queryOneBeef")
    public CommonResp QueryOneBeef(
            @ApiParam(name = "beefKey", value = "牛肉的key hash", example = "12ab28cd238f1", required = true)
            @RequestParam("beefKey") String beefKey
    ){
        return beefService.queryOneBeef(beefKey);
    }

    @ApiOperation(value="【国内运输营销企业】国内添加运输售卖信息")
    @PostMapping("/addSeller")
    public CommonResp AddSeller(@RequestBody AddSellerReq addsellerreq) {
        return beefService.addSeller(addsellerreq);
    }


}

