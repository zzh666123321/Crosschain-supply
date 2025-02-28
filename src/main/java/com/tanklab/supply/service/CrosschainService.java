package com.tanklab.supply.service;

import com.tanklab.supply.ds.req.CrossReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Crosschain;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 跨链信息表 服务类
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
public interface CrosschainService extends IService<Crosschain> {


    //CommonResp doCross(int param, int crosstype, int crossFrom, int crossTo);

    CommonResp queryCrossTx();

    CommonResp queryTxInfo(String txHash, Integer txId);

    CommonResp addCrossTx(CrossReq crossReq);

    /**
     * 启动跨链网关
     * @param srcIp 源链服务器IP
     * @param srcChainType 源链类型 (ethereum/chainmaker/h2chain)
     * @param dstIp 目标链服务器IP
     * @param dstChainType 目标链类型 (ethereum/chainmaker/h2chain)
     * @param relayIp 中继链服务器IP
     * @return 启动结果
     */
    CommonResp startGateways(String srcIp, String srcChainType, String dstIp, String dstChainType, String relayIp);
}
