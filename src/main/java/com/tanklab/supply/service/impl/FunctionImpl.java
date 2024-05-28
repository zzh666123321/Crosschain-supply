package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanklab.supply.common.ResultCode;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Contract;
import com.tanklab.supply.entity.Function;
import com.tanklab.supply.mapper.FunctionMapper;
import com.tanklab.supply.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class FunctionImpl extends ServiceImpl<FunctionMapper, Function> implements FunctionService {
    @Autowired
    public FunctionMapper functionMapper;
//    @Autowired
//    private ContractServiceImpl ContractService;



    @Override
    public CommonResp queryFunctionInfo(String ipChain, Integer port, Integer chainNumber, String contractName) {
        CommonResp queryfunctionesp = new CommonResp();

        QueryWrapper<Function> wrapper = new QueryWrapper<>();
        if (ipChain != null && !ipChain.isEmpty()) {
            wrapper.eq("ip_chain", ipChain);
        }
        if (port != null) {
            wrapper.eq("port", port);
        } else if (chainNumber != null) {
            wrapper.eq("chain_number", chainNumber);
        }
        if (contractName != null && !contractName.isEmpty()) {
            wrapper.eq("contract_name", contractName).or().eq("contract_address", contractName);
        }

        List<Function> functions = functionMapper.selectList(wrapper);

        if (functions.isEmpty()) {
            queryfunctionesp.setRet(ResultCode.FAILURE);
            queryfunctionesp.setMessage("No contracts found with the provided criteria.");
            return queryfunctionesp;
        }

        Set<String> functionNames = new HashSet<>();
        for (Function function : functions) {
            functionNames.add(function.getFunctionName());
        }

        JSONObject contractsInfo = new JSONObject();
        contractsInfo.put("functionNames", functionNames);

        queryfunctionesp.setRet(ResultCode.SUCCESS);
        queryfunctionesp.setData(contractsInfo);
        return queryfunctionesp;

    }
}