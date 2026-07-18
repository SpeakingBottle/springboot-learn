package com.example.springbootlearn.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootlearn.entity.LeaveRequest;

//请假申请表Mapper
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {
  /*
  selectById(id)         根据 ID 查询
  selectList(wrapper)    条件查询列表
  selectPage(page, wrapper)  分页查询
  insert(entity)         新增
  updateById(entity)     根据 ID 更新
  deleteById(id)         根据 ID 删除
   */
}
