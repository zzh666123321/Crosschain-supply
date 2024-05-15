package com.tanklab.supply.mapper;

import com.tanklab.supply.entity.Beef;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 牛肉信息表 Mapper 接口
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Mapper
public interface BeefMapper extends BaseMapper<Beef> {

}
