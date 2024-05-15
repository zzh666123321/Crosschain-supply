package com.tanklab.supply.service;

import com.tanklab.supply.ds.req.AddBeefReq;
import com.tanklab.supply.ds.req.AddSellerReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Beef;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 牛肉信息表 服务类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
public interface BeefService extends IService<Beef> {

    CommonResp addBeef(AddBeefReq addbeefreq);

    CommonResp queryOxBeef(Long oxId);

    CommonResp queryBeef();

    CommonResp queryOneBeef(String beefKey);

    CommonResp addSeller(AddSellerReq addsellerreq);
}
