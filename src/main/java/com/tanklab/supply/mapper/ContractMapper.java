package com.tanklab.supply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanklab.supply.entity.Chain;
import com.tanklab.supply.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
}
