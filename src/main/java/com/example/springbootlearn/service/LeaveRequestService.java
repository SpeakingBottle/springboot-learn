package com.example.springbootlearn.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootlearn.entity.LeaveRequest;

//请假申请表业务接口
public interface LeaveRequestService extends IService<LeaveRequest>{
 /*
 *   save(entity)           新增（单条/批量）
 *   getById(id)            根据 ID 查询
 *   list(wrapper)          条件查询列表
 *   page(page, wrapper)    分页查询
 *   updateById(entity)     根据 ID 更新
 *   removeById(id)         根据 ID 删除
 */
  //老师审核
  public void reviewFirst(Long id,String comment);  //接口的抽象方法
  //领导审核
  public void reviewSecond(Long id,String comment);  //接口的抽象方法
  //查看待审核列表
  public List<LeaveRequest> listPending();
}
