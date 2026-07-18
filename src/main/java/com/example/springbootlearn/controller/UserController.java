package com.example.springbootlearn.controller;

import com.example.springbootlearn.common.Result;
import com.example.springbootlearn.dto.CreateUserDto;
import com.example.springbootlearn.entity.User;
import com.example.springbootlearn.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口 —— 改造版，使用统一返回体 Result
 */
@Tag(name = "用户管理接口",description = "用户的增删改查接口")
@RestController
@RequestMapping("/api/users")
@Validated  // 开启参数校验
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询全部用户
     */
    @Operation(summary = "查询全部用户")
    @GetMapping
    public Result<List<User>> list() {
        List<User> users = userService.list();
        return Result.success(users);
        // 返回：{"code":200, "msg":"success", "data":[...]}
    }

    /**
     * 根据 ID 查询单个用户
     */
    @Operation(summary = "根据ID查询单个用户")
    @GetMapping("/{id}")
    public Result<User> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        // 如果用户不存在，user 为 null，前端也可以根据 data 是否为 null 来判断
        return Result.success(user);
    }

    /**
     * 新增用户
     *
     * @Valid 触发了 DTO 上的校验注解（@NotBlank、@Size 等）
     * 校验不通过时抛出 MethodArgumentNotValidException
     * → 被 GlobalExceptionHandler 捕获 → 返回 {"code":400, "msg":"用户名不能为空", "data":null}
     */
    @Operation(summary = "新增用户")
    @PreAuthorize("hasRole('DEAN')")    // 只有领导能操作
    @PostMapping
    public Result<Void> save(@Parameter(description = "新增用户请求体") @Valid @RequestBody CreateUserDto dto) {
        // 把 DTO 拷贝到 Entity
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        // BeanUtils.copyProperties：把同名属性从 dto 拷贝到 user，
        // 等同于 user.setUsername(dto.getUsername()); user.setPassword(dto.getPassword()); ...

        userService.save(user);
        return Result.success();
        // 返回：{"code":200, "msg":"success", "data":null}
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PreAuthorize("hasRole('DEAN')")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "要更新的用户ID") @PathVariable Long id,@Parameter(description = "要更新的用户数据") @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @PreAuthorize("hasRole('DEAN')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "要删除的用户ID") @PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
