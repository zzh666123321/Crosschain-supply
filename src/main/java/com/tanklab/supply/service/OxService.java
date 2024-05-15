package com.tanklab.supply.service;

import com.tanklab.supply.ds.req.AddOxReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Ox;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 牛信息表 服务类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
public interface OxService extends IService<Ox> {
    CommonResp addOx(AddOxReq addoxreq);

    CommonResp queryOxen();

    CommonResp queryOneOx(Long oxId);
}
