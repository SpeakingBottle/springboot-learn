package com.example.springbootlearn.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建请假单的请求体
 */
@Data
public class CreateLeaveRequestDto {
  @Schema(description = "学生ID", example = "1")
  @NotNull(message = "学号不能为空")
  private Long studentId;

  @Schema(description = "请假类型：事假/病假/年假", example = "事假")
  @NotBlank(message = "请假类型不能为空")
  private String type;

  @Schema(description = "请假开始时间", example = "2026-07-20T08:00:00")
  @NotNull(message = "开始时间不能为空")
  private LocalDateTime startTime;

  @Schema(description = "请假结束时间", example = "2026-07-20T17:00:00")
  @NotNull(message = "结束时间不能为空")
  private LocalDateTime endTime;

  @Schema(description = "请假原因", example = "家里有事需要处理")
  @NotBlank(message = "请假原因不能为空")
  private String reason;
}
