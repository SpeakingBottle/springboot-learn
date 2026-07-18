package com.example.springbootlearn.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootlearn.entity.LeaveRequest;
import com.example.springbootlearn.entity.User;
import com.example.springbootlearn.mapper.LeaveRequestMapper;
import com.example.springbootlearn.mapper.UserMapper;
import com.example.springbootlearn.security.SecurityUtils;
import com.example.springbootlearn.service.LeaveRequestService;

//请假申请表业务实现类
@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper,LeaveRequest> implements LeaveRequestService {
  @Autowired
  private UserMapper userMapper;

  //老师初审
  @Override
  @Transactional  //一个事务，保证必须一起完成
  public void reviewFirst(Long id,String comment){
    //1.查询请假单
    LeaveRequest lr = getById(id);
    if (lr==null) {
      throw new RuntimeException("请假单不存在");
    }

    //2.请假单状态校验：只有PENGDING状态才能进行初审
    if (!"PENDING".equals(lr.getStatus())) {
      throw new RuntimeException("当前状态不允许初审，状态："+lr.getStatus());
    }

    //3.获取当前审核人
    String username =SecurityUtils.getCurrentUsername();
    User teacher = userMapper.selectOne(
      new LambdaQueryWrapper<User>().eq(User::getUsername, username)
    );

    //4.更新请假单信息
    if (comment.contains("驳回") || comment.contains("不同意")) {
      lr.setStatus("TEACHER_REJECTED"); //驳回
    } else{
      lr.setStatus("TEACHER_APPROVED"); //同意
    }

    lr.setTeacherId(teacher.getId()); //哪个老师审的
    lr.setTeacherComment(comment);  //记录意见
    lr.setTeacherTime(LocalDateTime.now()); //审核时间
    lr.setUpdateTime(LocalDateTime.now());  //最后更新时间

    //更新数据库
    updateById(lr);
  }

  //领导复核
  @Override
  @Transactional  //一个事务，保证必须一起完成
  public void reviewSecond(Long id,String comment){
    //1.查询请假单
    LeaveRequest lr = getById(id);
    if (lr==null) {
      throw new RuntimeException("请假单不存在");
    }

    //2.请假单状态校验：只有TEACHER_APPROVED状态才能进行初审
    if (!"TEACHER_APPROVED".equals(lr.getStatus())) {
      throw new RuntimeException("当前状态不允许复核，状态："+lr.getStatus());
    }

    //3.获取当前审核人
    String username =SecurityUtils.getCurrentUsername();
    User dean = userMapper.selectOne(
      new LambdaQueryWrapper<User>().eq(User::getUsername, username)
    );

    //4.更新请假单信息
    if (comment.contains("驳回") || comment.contains("不同意")) {
      lr.setStatus("DEAN_REJECTED"); //驳回
    } else{
      lr.setStatus("DEAN_APPROVED"); //同意
    }

    lr.setDeanId(dean.getId()); //哪个领导审的
    lr.setDeanComment(comment);  //记录意见
    lr.setDeanTime(LocalDateTime.now()); //审核时间
    lr.setUpdateTime(LocalDateTime.now());  //最后更新时间

    //更新数据库
    updateById(lr);
  }

  //查询待审核列表
  @Override
  public List<LeaveRequest> listPending() {
    return list(           // list() 继承自 ServiceImpl，等于 selectList()
        new LambdaQueryWrapper<LeaveRequest>()
            .in(LeaveRequest::getStatus, "PENDING", "TEACHER_APPROVED")  // WHERE status IN (...)
            .orderByDesc(LeaveRequest::getCreateTime)        // ORDER BY create_time DESC
    );
  }
}
