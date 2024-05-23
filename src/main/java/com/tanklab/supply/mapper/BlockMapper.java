package com.tanklab.supply.mapper;

import com.tanklab.supply.entity.Chain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
/**
 * <p>
 * 链信息表 Mapper 接口
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Mapper
public interface BlockMapper extends BaseMapper<Chain> {

}