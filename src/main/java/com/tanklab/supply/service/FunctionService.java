package com.tanklab.supply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tanklab.supply.ds.req.CrossReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Crosschain;
import com.tanklab.supply.entity.Function;



public interface FunctionService extends IService<Function> {


    //CommonResp doCross(int param, int crosstype, int crossFrom, int crossTo);



    CommonResp queryFunctionInfo(String ipChain, Integer port, Integer chainNumber, String contractName);


}
