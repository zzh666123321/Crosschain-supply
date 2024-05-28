package com.tanklab.supply.service;

import com.tanklab.supply.ds.req.*;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Chain;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 链信息表 服务类
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
public interface ChainService extends IService<Chain> {
//    CommonResp checkChainnewblock(ChainReq chainreq);
//    CommonResp checkBlockInfo(BlockhashReq blockhashreq);
//
//    CommonResp checkHeightInfo(BlockheightReq blockheightreq);
//    CommonResp checkTxInfo(TxhashReq txhashreq);
//
//    CommonResp addChain(AddChainReq addchainreq);
//    CommonResp checkChainmaker(ChainMakerReq chainmakerreq);

    CommonResp querychainInfo();

}
