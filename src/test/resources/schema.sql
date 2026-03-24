-- ============================================================
-- 基于大模型的工程招投标与施工变更智能审查系统
-- MySQL 数据库设计  v2.0
-- （测试用，仅包含模块一/二/三所需表，建表写法与 requirements/mysql_schema.sql 保持一致）
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists sys_org;
drop table if exists sys_user;
drop table if exists sys_operation_log;

-- 1.1 机构/单位表（无外部依赖，最先建）
CREATE TABLE `sys_org` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '机构ID',
    `name`        VARCHAR(128) NOT NULL                    COMMENT '机构名称',
    `code`        VARCHAR(64)  UNIQUE                      COMMENT '统一社会信用代码',
    `type`        TINYINT                                  COMMENT '机构类型: 1=审计机构 2=建设单位 3=施工单位 4=监理单位',
    `parent_id`   BIGINT UNSIGNED DEFAULT NULL             COMMENT '上级机构ID，NULL 表示顶层',
    `address`     VARCHAR(256)                             COMMENT '地址',
    `status`      TINYINT NOT NULL DEFAULT 1               COMMENT '状态: 1=启用 0=禁用',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_parent_id` (`parent_id`),

    -- 自引用：子机构的 parent_id → 父机构 id（顶层为 NULL，不参与约束）
    CONSTRAINT `fk_org_parent` FOREIGN KEY (`parent_id`)
        REFERENCES `sys_org` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构/单位表';


-- 1.2 用户表（依赖 sys_org）
CREATE TABLE `sys_user` (
    `id`            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username`      VARCHAR(64)  NOT NULL UNIQUE            COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL                   COMMENT '密码哈希',
    `real_name`     VARCHAR(64)  NOT NULL                   COMMENT '真实姓名',
    `phone`         VARCHAR(20)                             COMMENT '手机号',
    `email`         VARCHAR(128)                            COMMENT '邮箱',
    `org_id`        BIGINT UNSIGNED                         COMMENT '所属机构ID',
    `role`          TINYINT NOT NULL DEFAULT 2
                    COMMENT '角色: 1=超级管理员 2=审查员 3=项目负责人 4=只读',
    `status`        TINYINT NOT NULL DEFAULT 1              COMMENT '状态: 1=启用 0=禁用',
    `avatar_url`    VARCHAR(512)                            COMMENT '头像URL',
    `last_login_at` DATETIME                                COMMENT '最近登录时间',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_org_id` (`org_id`),

    CONSTRAINT `fk_user_org` FOREIGN KEY (`org_id`)
        REFERENCES `sys_org` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 8.1 操作日志（依赖 sys_user；审计日志，不级联删除）
CREATE TABLE `sys_operation_log` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id`     BIGINT UNSIGNED NOT NULL                    COMMENT '操作人ID → sys_user',
    `module`      VARCHAR(64)                                 COMMENT '模块名称',
    `action`      VARCHAR(64)                                 COMMENT '操作类型，如 CREATE / UPDATE / DELETE',
    `object_type` VARCHAR(64)                                 COMMENT '操作对象类型，如 project / review_task',
    `object_id`   BIGINT UNSIGNED                             COMMENT '操作对象ID',
    `detail`      TEXT                                        COMMENT '操作详情（JSON 或描述文本）',
    `ip`          VARCHAR(64)                                 COMMENT '客户端IP',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX `idx_user_id`     (`user_id`),
    INDEX `idx_object`      (`object_type`, `object_id`),
    INDEX `idx_created_at`  (`created_at`),

    -- 审计日志：用户注销时保留日志，置 NULL
    CONSTRAINT `fk_op_log_user` FOREIGN KEY (`user_id`)
        REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

SET FOREIGN_KEY_CHECKS = 1;
