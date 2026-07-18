-- ==========================================
-- 学生请假审核系统 - 数据库初始化脚本
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS leave_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE leave_db;

-- ==========================================
-- 用户表
-- ==========================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL              COMMENT '登录账号',
    `password`    VARCHAR(255) NOT NULL              COMMENT '加密后的密码',
    `real_name`   VARCHAR(50)  NOT NULL              COMMENT '真实姓名',
    `role`        VARCHAR(20)  NOT NULL              COMMENT '角色：STUDENT/TEACHER/DEAN',
    `phone`       VARCHAR(20)  DEFAULT NULL          COMMENT '手机号',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==========================================
-- 请假申请表
-- ==========================================
CREATE TABLE IF NOT EXISTS `leave_request` (
    `id`              BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `student_id`      BIGINT       NOT NULL              COMMENT '学生ID，关联user.id',
    `type`            VARCHAR(20)  NOT NULL              COMMENT '请假类型：SICK(病假)/PERSONAL(事假)',
    `start_time`      DATETIME     NOT NULL              COMMENT '开始时间',
    `end_time`        DATETIME     NOT NULL              COMMENT '结束时间',
    `reason`          TEXT         NOT NULL              COMMENT '请假原因',
    `status`          VARCHAR(30)  NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `teacher_id`      BIGINT       DEFAULT NULL          COMMENT '审核老师ID',
    `teacher_comment` VARCHAR(500) DEFAULT NULL          COMMENT '老师审核意见',
    `teacher_time`    DATETIME     DEFAULT NULL          COMMENT '老师审核时间',
    `dean_id`         BIGINT       DEFAULT NULL          COMMENT '复核领导ID',
    `dean_comment`    VARCHAR(500) DEFAULT NULL          COMMENT '领导复核意见',
    `dean_time`       DATETIME     DEFAULT NULL          COMMENT '领导复核时间',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_status`     (`status`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_dean_id`    (`dean_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';
