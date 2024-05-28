package com.tanklab.supply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanklab.supply.entity.Contract;
import com.tanklab.supply.entity.Function;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FunctionMapper extends BaseMapper<Function> {
}
