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
}
