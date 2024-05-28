package com.tanklab.supply.controller;

import com.tanklab.supply.ds.req.*;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.ChainService;
import com.tanklab.supply.service.ContractService;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 链信息表 前端控制器
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@RestController
@RequestMapping("/supply/contract")
public class ContractController {
    @Autowired
    ContractService contractService;

    @ApiOperation(value = "查询接入链的信息")
    @GetMapping("/queryContractInfo")
    public CommonResp queryContractInfo(
            @RequestParam(required = false) String ipChain,
            @RequestParam(required = false) Integer port,
            @RequestParam(required = false) Integer chainNumber
    ) {
        // 调用服务层方法，根据参数查询链的信息
        return contractService.queryContractInfo(ipChain, port, chainNumber);
    }

}