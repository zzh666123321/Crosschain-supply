package com.tanklab.supply.controller;


import com.tanklab.supply.ds.req.*;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.service.ChainService;
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
@RequestMapping("/supply/chain")
public class ChainController {
    @Autowired
    ChainService ChainService;

//    @ApiOperation(value="查询新块高")
//    @PostMapping("/checkChainnewblock")
//    public CommonResp CheckChain(@RequestBody ChainReq chainreq) {return ChainService.checkChainnewblock(chainreq);}
//
//
//    @ApiOperation(value="哈希查询块")
//    @PostMapping("/checkHashInfo")
//    public CommonResp CheckBlockHash(@RequestBody BlockhashReq blockhashreq) {return ChainService.checkBlockInfo(blockhashreq);}
//    @ApiOperation(value="块高查询块")
//    @PostMapping("/checkHeightInfo")
//    public CommonResp CheckBlockHeight(@RequestBody BlockheightReq blockheightreq) {return ChainService.checkHeightInfo(blockheightreq);}
//    @ApiOperation(value="查询交易")
//    @PostMapping("/checkTxInfo")
//    public CommonResp CheckTx(@RequestBody TxhashReq txhashreq) {return ChainService.checkTxInfo(txhashreq);}
//
//
//    @ApiOperation(value="上传链")//暂时不需要
//    @PostMapping("/addChain")
//    public CommonResp AddChain(@RequestBody AddChainReq addchainreq) {return ChainService.addChain(addchainreq);}
//
//    @ApiOperation(value="查询长安链信息配置")
//    @PostMapping("/checkChainmaker")
//    public CommonResp AddChain(@RequestBody ChainMakerReq chainmakerreq)
//    {
//        return ChainService.checkChainmaker(chainmakerreq);
//    }


    @ApiOperation(value = "查询所有接入链的信息")
    @GetMapping("/queryChainInfo")
    public CommonResp QueryChainInfo(){
        return ChainService.querychainInfo();
    }


}

