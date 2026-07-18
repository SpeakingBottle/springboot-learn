package com.example.springbootlearn.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

// 请假申请表实体类，与数据库中leave_request表对应
@Data
@TableName("leave_request")
public class LeaveRequest {
  @TableId(type = IdType.AUTO)  //主键自增
  private Long id;

  private Long studentId; //学生id

  private String type;  //请假类型

  private LocalDateTime startTime;  //请假开始时间

  private LocalDateTime endTime;  //请假结束时间

  private String reason;  //请假原因

  private String status;  //审批状态

  private Long teacherId; //审核老师id

  private String teacherComment;  //老师审核意见

  private LocalDateTime teacherTime;  //老师审核时间

  private Long deanId;  //审核领导id

  private String deanComment; //领导审核意见

  private LocalDateTime deanTime; //领导审核时间

  private LocalDateTime createTime; //提交时间

  private LocalDateTime updateTime; //最后更新时间
}
