package com.tanklab.supply.mapper;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanklab.supply.entity.Crosschain;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 跨链信息表 Mapper 接口
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Mapper
public interface CrosschainMapper extends BaseMapper<Crosschain> {

}
