package com.example.springbootlearn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.BeanUtils;
import com.example.springbootlearn.common.Result;
import com.example.springbootlearn.dto.CreateLeaveRequestDto;
import com.example.springbootlearn.dto.ReviewDto;
import com.example.springbootlearn.entity.LeaveRequest;
import com.example.springbootlearn.service.LeaveRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 请假单管理接口
 */
@Tag(name = "请假管理", description = "请假单的提交、审核相关接口")
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {
  /**
     * @Autowired：自动注入 LeaveRequestService 的实例
     * 不用 new LeaveRequestService()，Spring 帮你管理对象的创建和销毁
     */
    @Autowired
    private LeaveRequestService leaveRequestService;  //创建service实例

    @Operation(summary = "查询所有请假单")
    @GetMapping
    public Result<List<LeaveRequest>> list(){
      List<LeaveRequest> leaveRequests= leaveRequestService.list();
      return Result.success(leaveRequests);
    }

    @Operation(summary = "根据ID查询请假单")
    @GetMapping("/{id}")
    public Result<LeaveRequest> getById(
        @Parameter(description = "请假单ID") @PathVariable Long id){
      LeaveRequest leaveRequest=leaveRequestService.getById(id);
      return Result.success(leaveRequest);
    }

    @Operation(summary = "学生提交请假申请")
    @PreAuthorize("hasRole('STUDENT')") //只有学生能提交
    @PostMapping
    public Result<Void> save(
        @Parameter(description = "请假申请信息") @Valid @RequestBody CreateLeaveRequestDto dto){
      //把DTO拷贝到Entity
      LeaveRequest leaveRequest=new LeaveRequest();
      BeanUtils.copyProperties(dto, leaveRequest);

      leaveRequestService.save(leaveRequest);
      return Result.success();
    }

    @Operation(summary = "老师初审")
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{id}/review-first")
    public Result<Void> reviewFirst(
        @Parameter(description = "请假单ID") @PathVariable Long id,
        @Parameter(description = "审核意见") @Valid @RequestBody ReviewDto dto){
      leaveRequestService.reviewFirst(id, dto.getComment());
      return Result.success();
    }

    @Operation(summary = "领导复审")
    @PreAuthorize("hasRole('DEAN')")
    @PutMapping("/{id}/review-second")
    public Result<Void> reviewSecond(
        @Parameter(description = "请假单ID") @PathVariable Long id,
        @Parameter(description = "复审意见") @Valid @RequestBody ReviewDto dto){
      leaveRequestService.reviewSecond(id, dto.getComment());
      return Result.success();
    }

    @Operation(summary = "查看待审核列表（初审+复审）")
    @PreAuthorize("hasAnyRole('TEACHER','DEAN')")
    @GetMapping("/pending")
    public Result<List<LeaveRequest>> pending(){
      return Result.success(leaveRequestService.listPending());
    }
}
