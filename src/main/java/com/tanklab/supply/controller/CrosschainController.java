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
        CrosschainService crosschainService;

        @ApiOperation(value="查询跨链结果")
        @GetMapping("/queryAllCrossTx")
        public CommonResp QueryChainInfo(){
            return crosschainService.queryCrossTx();
        }

        @ApiOperation(value = "查询某个交易的具体信息")
        @GetMapping("/queryTxInfo")
        public CommonResp queryTxInfo(
                @RequestParam(required = false) String txHash,
                @RequestParam(required = false) Integer txId

        ) {
            // 调用服务层方法，根据参数查询链的信息
            return crosschainService.queryTxInfo(txHash, txId);
        }

        @ApiOperation(value="插入跨链请求内容")
        @PostMapping("/addCrossTx")
        public CommonResp addCrossTx (@RequestBody CrossReq addbeefreq) {
                return crosschainService.addCrossTx(addbeefreq);
        }

}


