package com.tanklab.supply.controller;

import com.google.common.base.Functions;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.ContractService;
import com.tanklab.supply.service.FunctionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/supply/function")
public class FunctionController {
    @Autowired
    FunctionService functionService;

    @ApiOperation(value = "查询函数的信息")
    @GetMapping("/queryFunctionInfo")
    public CommonResp queryContractInfo(
            @RequestParam(required = false) String ipChain,
            @RequestParam(required = false) Integer port,
            @RequestParam(required = false) Integer chainNumber,
            @RequestParam(required = true) String contractName
    ) {
        // 调用服务层方法，根据参数查询链的信息
        return functionService.queryFunctionInfo(ipChain, port, chainNumber,contractName);
    }

}