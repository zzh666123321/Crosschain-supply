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
public interface BlockService extends IService<Chain> {
CommonResp blockInfo(ChainReq chainreq);

    //void updateblocks();
}
