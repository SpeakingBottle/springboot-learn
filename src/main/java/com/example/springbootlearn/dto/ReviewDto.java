package com.example.springbootlearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核意见请求体
 */
@Data
public class ReviewDto {
  @Schema(description = "审核意见内容", example = "同意请假")
  @NotBlank(message = "审核意见不能为空")
  private String comment;
}
