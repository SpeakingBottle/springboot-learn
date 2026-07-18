package com.example.springbootlearn.common;

import lombok.Data;

/**
 * 统一返回体 —— 所有接口都用这个类包装返回值
 *
 * <T> 是泛型，表示 data 可以是任意类型：
 *   Result<List<User>>      data 是用户列表
 *   Result<User>           data 是单个用户
 *   Result<String>          data 是字符串
 *   Result<Void>            data 是 null
 *
 * 最终返回给前端的 JSON 格式：
 *   {"code": 200, "msg": "success", "data": {...}}
 */
@Data
public class Result<T> {

    private int code;       // 状态码：200 成功，400 参数错误，500 系统异常
    private String msg;     // 提示信息
    private T data;         // 返回数据

    // 私有构造方法 —— 外面通过静态工厂方法创建对象
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ============ 工厂方法 ============

    /** 成功，带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功，不带数据（比如删除操作） */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 失败，自定义 code 和 msg */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 失败，默认 500 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
