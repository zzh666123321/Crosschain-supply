package com.tanklab.supply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanklab.supply.entity.Crosschain;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 牛肉信息表 Mapper 接口
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-12-18
 */
@Mapper
public interface CrossMapper extends BaseMapper<Crosschain> {

}
