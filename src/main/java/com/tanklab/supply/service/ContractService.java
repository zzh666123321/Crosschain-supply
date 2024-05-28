package com.tanklab.supply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Chain;
import com.tanklab.supply.entity.Contract;

public interface ContractService extends IService<Contract> {


    CommonResp queryContractInfo(String chainIp, Integer port, Integer chainNumber);
}
