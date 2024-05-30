package com.tanklab.supply.mapper;

import com.tanklab.supply.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanklab.supply.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
/**
 * <p>
 * 用户个人信息表 Mapper 接口
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}