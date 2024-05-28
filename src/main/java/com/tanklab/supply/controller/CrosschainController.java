package com.tanklab.supply.controller;


import com.tanklab.supply.ds.req.ChainMakerReq;
import com.tanklab.supply.ds.req.CrossReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.CrosschainService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 跨链信息表 前端控制器
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@RestController
@RequestMapping("/supply/crosschain")
public class CrosschainController {

        @Autowired
        CrosschainService CrosschainService;

        @ApiOperation(value="查询跨链结果")
        @GetMapping("/queryCrossRet/{oxId}")
        public CommonResp  doCross(@RequestBody CrossReq crossreq){
            return CrosschainService.doCross(crossreq);
        }
    }


