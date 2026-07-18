package com.example.springbootlearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建用户的请求体
 *
 * 为什么不用 User 实体直接接收？因为：
 *   1. Entity 包含 id、createTime 等后端自动生成的字段，前端不应该传
 *   2. 校验注解（@NotBlank）应该写在 DTO 上，不要污染 Entity
 *   3. 某些接口需要的字段和 Entity 不同（比如登录只要 username + password）
 */
@Data
public class CreateUserDto {

    @Schema(description = "用户名",example = "deki")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3~50 之间")
    private String username;

    @Schema(description = "密码",example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在 6~100 之间")
    private String password;

    @Schema(description = "真实姓名",example = "zhangsan")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "角色",example = "STUDENT")
    @NotBlank(message = "角色不能为空")
    private String role;

    private String phone;  // 手机号选填，不加 @NotBlank
}
