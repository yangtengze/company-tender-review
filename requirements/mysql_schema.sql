-- ============================================================
-- 基于大模型的工程招投标与施工变更智能审查系统
-- MySQL 数据库设计  v2.0
-- 修订说明：
--   1. 按依赖顺序建表（被引用表先建）
--   2. 所有外键字段补充 CONSTRAINT FOREIGN KEY ... REFERENCES
--   3. sys_org.parent_id 改为 NULL 表示顶层（原 DEFAULT 0 无法做自引用 FK）
--   4. 补充 doc_bid_announcement / doc_contract / review_item_result
--      / review_issue 缺失的 updated_at 字段
--   5. 外键命名规范：fk_<子表>_<引用列>
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE IF NOT EXISTS company_tender_review
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE company_tender_review; 

drop table if exists sys_org;
drop table if exists sys_user;
drop table if exists project;
drop table if exists document;
drop table if exists doc_bid_announcement;
drop table if exists doc_contract;
drop table if exists change_request;
drop table if exists change_request_doc;
drop table if exists review_task;
drop table if exists review_task_doc;
drop table if exists review_result;
drop table if exists review_item_result;
drop table if exists review_issue;
drop table if exists law_regulation;
drop table if exists law_clause;
drop table if exists case_library;
drop table if exists market_price;
drop table if exists public_platform;
drop table if exists llm_call_log;
drop table if exists doc_extract_cache;
drop table if exists sys_operation_log;
drop table if exists sys_notification;

-- ============================================================
-- 模块一：基础支撑层
-- 建表顺序：sys_org → sys_user（user 引用 org，org 必须先建）
-- ============================================================

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


-- ============================================================
-- 模块二：工程项目层（依赖 sys_org、sys_user）
-- ============================================================

-- 2.1 工程项目表
CREATE TABLE `project` (
    `id`               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
    `project_no`       VARCHAR(64)   NOT NULL UNIQUE        COMMENT '项目编号（全局唯一）',
    `project_name`     VARCHAR(256)  NOT NULL               COMMENT '项目名称',
    `project_type`     TINYINT                              COMMENT '项目类型: 1=房建 2=市政 3=水利 4=交通 5=其他',
    `build_org_id`     BIGINT UNSIGNED                      COMMENT '建设单位ID → sys_org',
    `contractor_id`    BIGINT UNSIGNED                      COMMENT '施工单位ID → sys_org',
    `supervisor_id`    BIGINT UNSIGNED                      COMMENT '监理单位ID → sys_org',
    `total_investment` DECIMAL(18,2)                        COMMENT '概算总投资（元）',
    `contract_amount`  DECIMAL(18,2)                        COMMENT '合同金额（元）',
    `location`         VARCHAR(256)                         COMMENT '项目地点',
    `approval_no`      VARCHAR(128)                         COMMENT '立项批准文号',
    `approval_date`    DATE                                 COMMENT '立项批准日期',
    `planned_start`    DATE                                 COMMENT '计划开工日期',
    `planned_end`      DATE                                 COMMENT '计划竣工日期',
    `actual_start`     DATE                                 COMMENT '实际开工日期',
    `actual_end`       DATE                                 COMMENT '实际竣工日期',
    `status`           TINYINT NOT NULL DEFAULT 1
                       COMMENT '项目状态: 1=立项 2=招标中 3=施工中 4=竣工 5=归档',
    `description`      TEXT                                 COMMENT '项目简介',
    `creator_id`       BIGINT UNSIGNED                      COMMENT '创建人ID → sys_user',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_build_org`   (`build_org_id`),
    INDEX `idx_contractor`  (`contractor_id`),
    INDEX `idx_supervisor`  (`supervisor_id`),
    INDEX `idx_creator`     (`creator_id`),
    INDEX `idx_status`      (`status`),

    CONSTRAINT `fk_project_build_org`  FOREIGN KEY (`build_org_id`)
        REFERENCES `sys_org` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_project_contractor` FOREIGN KEY (`contractor_id`)
        REFERENCES `sys_org` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_project_supervisor` FOREIGN KEY (`supervisor_id`)
        REFERENCES `sys_org` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_project_creator`    FOREIGN KEY (`creator_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工程项目表';


-- ============================================================
-- 模块三：文件管理层（依赖 project、sys_user）
-- ============================================================

-- 3.1 文件主表
-- doc_type 枚举（代码层维护，不建字典表）:
--   1=招标公告  2=投标文件  3=评标报告   4=中标通知书
--   5=施工合同  6=立项审批文件  7=开工报告  8=施工日志
--   9=变更申请  10=变更方案  11=施工图纸  12=工程量清单
--   13=质量验收规范  14=监理报告  15=竣工验收报告  99=其他
CREATE TABLE `document` (
    `id`            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    `project_id`    BIGINT UNSIGNED NOT NULL                COMMENT '所属项目ID → project',
    `doc_type`      TINYINT         NOT NULL                COMMENT '文件类型（见枚举注释）',
    `doc_name`      VARCHAR(256)    NOT NULL                COMMENT '文件名称',
    `file_path`     VARCHAR(512)    NOT NULL                COMMENT '存储路径（OSS Key/本地路径）',
    `file_size`     BIGINT                                  COMMENT '文件大小（字节）',
    `file_ext`      VARCHAR(16)                             COMMENT '文件后缀名，如 pdf',
    `md5`           VARCHAR(64)                             COMMENT '文件MD5，用于秒传/去重',
    `version`       VARCHAR(32)     DEFAULT '1.0'           COMMENT '版本号',
    `issue_date`    DATE                                    COMMENT '文件出具日期',
    `issuer`        VARCHAR(128)                            COMMENT '出具单位/人',
    `parse_status`  TINYINT         DEFAULT 0
                    COMMENT '解析状态: 0=待解析 1=解析中 2=解析完成 3=解析失败',
    `parse_text`    LONGTEXT                                COMMENT '解析后的纯文本内容',
    `uploader_id`   BIGINT UNSIGNED                         COMMENT '上传人ID → sys_user',
    `remark`        VARCHAR(512)                            COMMENT '备注',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_doctype` (`project_id`, `doc_type`),
    INDEX `idx_parse_status`    (`parse_status`),
    INDEX `idx_uploader`        (`uploader_id`),
    INDEX `idx_md5`             (`md5`),

    CONSTRAINT `fk_document_project`  FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_document_uploader` FOREIGN KEY (`uploader_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件主表';


-- 3.2 招标公告扩展信息表（依赖 document、project；与 document 1:1）
CREATE TABLE `doc_bid_announcement` (
    `id`                   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `doc_id`               BIGINT UNSIGNED NOT NULL UNIQUE  COMMENT '关联 document.id（1:1）',
    `project_id`           BIGINT UNSIGNED NOT NULL         COMMENT '关联项目ID → project',
    `bid_no`               VARCHAR(128)                     COMMENT '招标编号',
    `bid_type`             TINYINT                          COMMENT '招标方式: 1=公开 2=邀请 3=竞争性谈判',
    `publish_date`         DATETIME                         COMMENT '公告发布时间',
    `deadline_date`        DATETIME                         COMMENT '投标截止时间',
    `bid_open_date`        DATETIME                         COMMENT '开标时间',
    `public_notice_days`   INT                              COMMENT '公示期天数（系统自动计算）',
    `platform_name`        VARCHAR(128)                     COMMENT '发布平台名称',
    `platform_url`         VARCHAR(512)                     COMMENT '发布平台URL',
    `is_public_platform`   TINYINT                          COMMENT '是否在合规公开平台: 1=是 0=否 NULL=待核验',
    `qualification_req`    TEXT                             COMMENT '资质要求原文',
    `performance_req`      TEXT                             COMMENT '业绩要求原文',
    `estimated_price`      DECIMAL(18,2)                    COMMENT '招标控制价（元）',
    `created_at`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_bid_no`     (`bid_no`),

    CONSTRAINT `fk_bid_ann_doc`     FOREIGN KEY (`doc_id`)
        REFERENCES `document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_bid_ann_project` FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招标公告扩展信息';


-- 3.3 合同扩展信息表（依赖 document、project；与 document 1:1）
CREATE TABLE `doc_contract` (
    `id`                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `doc_id`            BIGINT UNSIGNED NOT NULL UNIQUE     COMMENT '关联 document.id（1:1）',
    `project_id`        BIGINT UNSIGNED NOT NULL            COMMENT '关联项目ID → project',
    `contract_no`       VARCHAR(128)                        COMMENT '合同编号',
    `contract_amount`   DECIMAL(18,2)                       COMMENT '合同金额（元）',
    `sign_date`         DATE                                COMMENT '签订日期',
    `party_a`           VARCHAR(128)                        COMMENT '甲方（建设单位）',
    `party_b`           VARCHAR(128)                        COMMENT '乙方（施工单位）',
    `start_date`        DATE                                COMMENT '合同约定开工日期',
    `end_date`          DATE                                COMMENT '合同约定竣工日期',
    `warranty_period`   INT                                 COMMENT '质保期（月）',
    `payment_terms`     TEXT                                COMMENT '付款条款原文',
    `penalty_terms`     TEXT                                COMMENT '违约条款原文',
    `created_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_id` (`project_id`),

    CONSTRAINT `fk_contract_doc`     FOREIGN KEY (`doc_id`)
        REFERENCES `document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_contract_project` FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同扩展信息';


-- 3.4 施工变更申请表（依赖 project、sys_org、sys_user）
CREATE TABLE `change_request` (
    `id`              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '变更申请ID',
    `project_id`      BIGINT UNSIGNED NOT NULL                COMMENT '所属项目ID → project',
    `change_no`       VARCHAR(64)     NOT NULL UNIQUE         COMMENT '变更编号（全局唯一）',
    `change_type`     TINYINT                                 COMMENT '变更类型: 1=设计 2=工程量 3=材料 4=工期 5=综合',
    `change_reason`   TINYINT                                 COMMENT '原因类型: 1=设计缺陷 2=不可抗力 3=建设方要求 4=施工条件变化 5=其他',
    `reason_desc`     TEXT                                    COMMENT '变更原因描述',
    `change_desc`     TEXT                                    COMMENT '变更内容描述',
    `original_amount` DECIMAL(18,2)                           COMMENT '原合同金额（元）',
    `change_amount`   DECIMAL(18,2)                           COMMENT '变更金额（元）',
    `change_ratio`    DECIMAL(8,4)                            COMMENT '变更金额占合同比（%），后端自动计算',
    `apply_date`      DATE                                    COMMENT '申请日期',
    `apply_org_id`    BIGINT UNSIGNED                         COMMENT '申请单位ID → sys_org',
    `status`          TINYINT NOT NULL DEFAULT 1
                      COMMENT '状态: 1=待审查 2=审查中 3=审查完成 4=已撤回',
    `creator_id`      BIGINT UNSIGNED                         COMMENT '创建人ID → sys_user',
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_id`  (`project_id`),
    INDEX `idx_apply_org`   (`apply_org_id`),
    INDEX `idx_creator`     (`creator_id`),
    INDEX `idx_status`      (`status`),

    CONSTRAINT `fk_change_req_project`   FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_change_req_apply_org` FOREIGN KEY (`apply_org_id`)
        REFERENCES `sys_org` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_change_req_creator`   FOREIGN KEY (`creator_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='施工变更申请表';


-- 3.5 变更申请与文件关联表（依赖 change_request、document；多对多中间表）
CREATE TABLE `change_request_doc` (
    `id`                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `change_request_id` BIGINT UNSIGNED NOT NULL              COMMENT '变更申请ID → change_request',
    `doc_id`            BIGINT UNSIGNED NOT NULL              COMMENT '文件ID → document',
    `doc_role`          TINYINT                               COMMENT '文件角色: 1=变更方案 2=原设计图纸 3=工程量清单 4=佐证材料',

    UNIQUE KEY `uk_change_doc` (`change_request_id`, `doc_id`),
    INDEX `idx_doc_id` (`doc_id`),

    CONSTRAINT `fk_cr_doc_change` FOREIGN KEY (`change_request_id`)
        REFERENCES `change_request` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_cr_doc_doc`    FOREIGN KEY (`doc_id`)
        REFERENCES `document` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='变更申请与文件关联表';


-- ============================================================
-- 模块四：审查任务层（依赖 project、change_request、sys_user、document）
-- ============================================================

-- 4.1 审查任务主表
CREATE TABLE `review_task` (
    `id`           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '审查任务ID',
    `task_no`      VARCHAR(64)     NOT NULL UNIQUE           COMMENT '任务编号（系统生成）',
    `project_id`   BIGINT UNSIGNED NOT NULL                  COMMENT '关联项目ID → project',
    `task_type`    TINYINT         NOT NULL
                   COMMENT '任务类型: 1=招投标审查 2=施工变更审查',
    `change_id`    BIGINT UNSIGNED                           COMMENT '关联变更申请ID → change_request（变更审查时填）',
    `task_name`    VARCHAR(256)    NOT NULL                  COMMENT '任务名称',
    `status`       TINYINT         NOT NULL DEFAULT 1
                   COMMENT '任务状态: 1=待执行 2=执行中 3=已完成 4=已失败 5=已取消',
    `priority`     TINYINT         DEFAULT 2                 COMMENT '优先级: 1=低 2=中 3=高',
    `assignee_id`  BIGINT UNSIGNED                           COMMENT '指派审查员ID → sys_user',
    `trigger_mode` TINYINT         DEFAULT 1                 COMMENT '触发方式: 1=手动 2=自动',
    `start_at`     DATETIME                                  COMMENT '开始执行时间',
    `end_at`       DATETIME                                  COMMENT '执行完成时间',
    `duration_ms`  INT                                       COMMENT '执行耗时（毫秒）',
    `error_msg`    TEXT                                      COMMENT '失败原因',
    `creator_id`   BIGINT UNSIGNED                           COMMENT '创建人ID → sys_user',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_status` (`project_id`, `status`),
    INDEX `idx_task_type`      (`task_type`),
    INDEX `idx_change_id`      (`change_id`),
    INDEX `idx_assignee`       (`assignee_id`),
    INDEX `idx_creator`        (`creator_id`),

    CONSTRAINT `fk_task_project`  FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_task_change`   FOREIGN KEY (`change_id`)
        REFERENCES `change_request` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_task_assignee` FOREIGN KEY (`assignee_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_task_creator`  FOREIGN KEY (`creator_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审查任务主表';


-- 4.2 审查任务与文件关联表（依赖 review_task、document；多对多中间表）
CREATE TABLE `review_task_doc` (
    `id`       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `task_id`  BIGINT UNSIGNED NOT NULL                      COMMENT '审查任务ID → review_task',
    `doc_id`   BIGINT UNSIGNED NOT NULL                      COMMENT '文件ID → document',
    `doc_role` VARCHAR(64)                                   COMMENT '文件在本次审查中的角色说明',

    UNIQUE KEY `uk_task_doc` (`task_id`, `doc_id`),
    INDEX `idx_doc_id` (`doc_id`),

    CONSTRAINT `fk_task_doc_task` FOREIGN KEY (`task_id`)
        REFERENCES `review_task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_task_doc_doc`  FOREIGN KEY (`doc_id`)
        REFERENCES `document` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审查任务与文件关联';


-- ============================================================
-- 模块五：审查结果层（依赖 review_task、project、sys_user）
-- ============================================================

-- 5.1 审查结果主表（与 review_task 1:1）
CREATE TABLE `review_result` (
    `id`              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '审查结果ID',
    `task_id`         BIGINT UNSIGNED NOT NULL UNIQUE          COMMENT '关联审查任务ID → review_task（1:1）',
    `project_id`      BIGINT UNSIGNED NOT NULL                 COMMENT '关联项目ID → project（冗余，方便查询）',
    `overall_verdict` TINYINT         NOT NULL
                      COMMENT '总体结论: 1=合规 2=存在问题 3=严重违规',
    `risk_level`      TINYINT
                      COMMENT '风险等级: 1=低风险 2=中风险 3=高风险 4=极高风险',
    `summary`         TEXT                                     COMMENT '审查总结（AI生成）',
    `suggestion`      TEXT                                     COMMENT '处理建议（AI生成）',
    `issue_count`     INT             DEFAULT 0                COMMENT '发现问题数量',
    `model_name`      VARCHAR(128)                             COMMENT '使用的大模型名称',
    `model_version`   VARCHAR(64)                              COMMENT '模型版本',
    `tokens_used`     INT                                      COMMENT '消耗 tokens 数',
    `reviewer_id`     BIGINT UNSIGNED                          COMMENT '人工复核人ID → sys_user',
    `review_status`   TINYINT         DEFAULT 1
                      COMMENT '人工复核状态: 1=待复核 2=已确认 3=已驳回',
    `reviewer_note`   TEXT                                     COMMENT '人工复核意见',
    `reviewed_at`     DATETIME                                 COMMENT '人工复核时间',
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_project_id`    (`project_id`),
    INDEX `idx_overall`       (`overall_verdict`),
    INDEX `idx_review_status` (`review_status`),
    INDEX `idx_reviewer`      (`reviewer_id`),

    CONSTRAINT `fk_result_task`     FOREIGN KEY (`task_id`)
        REFERENCES `review_task` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_result_project`  FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_result_reviewer` FOREIGN KEY (`reviewer_id`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审查结果主表';


-- 5.2 审查子项结果表（依赖 review_result、review_task；每个审查维度一条）
-- check_dimension 枚举:
--   招投标: 11=公开平台核验 12=时间顺序 13=合同一致性 14=公示期 15=限制性条款
--   施工变更: 21=必要性 22=合理性 23=经济性
CREATE TABLE `review_item_result` (
    `id`               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '子项结果ID',
    `result_id`        BIGINT UNSIGNED NOT NULL                   COMMENT '关联 review_result.id',
    `task_id`          BIGINT UNSIGNED NOT NULL                   COMMENT '关联 review_task.id（冗余，方便直查）',
    `check_dimension`  TINYINT         NOT NULL                   COMMENT '审查维度编码（见枚举注释）',
    `dimension_name`   VARCHAR(128)    NOT NULL                   COMMENT '审查维度名称',
    `verdict`          TINYINT         NOT NULL
                       COMMENT '单项结论: 1=合规 2=存在问题 3=严重违规 4=无法判断',
    `confidence`       DECIMAL(5,4)                               COMMENT 'AI 置信度 0.0000~1.0000',
    `detail`           TEXT                                       COMMENT '详细分析内容（AI生成）',
    `evidence`         TEXT                                       COMMENT '判断依据/引用原文片段',
    `issue_desc`       TEXT                                       COMMENT '问题描述（有问题时填写）',
    `suggestion`       TEXT                                       COMMENT '改进建议',
    `ref_law_ids`      JSON                                       COMMENT '引用法规ID列表 [1,2,3]',
    `ref_case_ids`     JSON                                       COMMENT '引用案例ID列表',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_result_id`   (`result_id`),
    INDEX `idx_task_id`     (`task_id`),
    INDEX `idx_dimension`   (`check_dimension`),
    INDEX `idx_verdict`     (`verdict`),

    CONSTRAINT `fk_item_result_result` FOREIGN KEY (`result_id`)
        REFERENCES `review_result` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_item_result_task`   FOREIGN KEY (`task_id`)
        REFERENCES `review_task` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审查子项结果表';


-- 5.3 问题清单表（依赖 review_item_result、review_result、project、sys_user）
-- issue_type: 1=程序违规 2=内容不一致 3=时序异常 4=条款违规 5=成本异常 6=其他
-- severity:   1=提示 2=警告 3=错误 4=严重
CREATE TABLE `review_issue` (
    `id`             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '问题ID',
    `item_result_id` BIGINT UNSIGNED NOT NULL                   COMMENT '关联子项结果ID → review_item_result',
    `result_id`      BIGINT UNSIGNED NOT NULL                   COMMENT '关联审查结果ID → review_result（冗余）',
    `project_id`     BIGINT UNSIGNED NOT NULL                   COMMENT '关联项目ID → project（冗余）',
    `issue_type`     TINYINT                                    COMMENT '问题类型（见枚举注释）',
    `severity`       TINYINT                                    COMMENT '严重程度（见枚举注释）',
    `title`          VARCHAR(256)    NOT NULL                   COMMENT '问题标题',
    `description`    TEXT                                       COMMENT '问题详述',
    `location`       VARCHAR(512)                               COMMENT '问题所在文件及位置描述',
    `suggestion`     TEXT                                       COMMENT '处置建议',
    `status`         TINYINT         DEFAULT 1
                     COMMENT '整改状态: 1=待整改 2=整改中 3=已整改 4=已忽略',
    `handle_note`    TEXT                                       COMMENT '整改说明',
    `handled_by`     BIGINT UNSIGNED                            COMMENT '处理人ID → sys_user',
    `handled_at`     DATETIME                                   COMMENT '处理时间',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_item_result_id` (`item_result_id`),
    INDEX `idx_result_id`      (`result_id`),
    INDEX `idx_project_id`     (`project_id`),
    INDEX `idx_severity`       (`severity`),
    INDEX `idx_status`         (`status`),
    INDEX `idx_handled_by`     (`handled_by`),

    CONSTRAINT `fk_issue_item_result` FOREIGN KEY (`item_result_id`)
        REFERENCES `review_item_result` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_issue_result`      FOREIGN KEY (`result_id`)
        REFERENCES `review_result` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_issue_project`     FOREIGN KEY (`project_id`)
        REFERENCES `project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_issue_handled_by`  FOREIGN KEY (`handled_by`)
        REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题清单表';


-- ============================================================
-- 模块六：知识库层（独立，无业务表外键，知识类数据）
-- ============================================================

-- 6.1 法规库
CREATE TABLE `law_regulation` (
    `id`             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '法规ID',
    `title`          VARCHAR(256)    NOT NULL                   COMMENT '法规标题',
    `short_name`     VARCHAR(128)                               COMMENT '简称',
    `law_no`         VARCHAR(128)                               COMMENT '法规编号',
    `category`       TINYINT                                    COMMENT '类别: 1=法律 2=行政法规 3=部门规章 4=地方性法规 5=标准规范',
    `issuer`         VARCHAR(128)                               COMMENT '发布机关',
    `issue_date`     DATE                                       COMMENT '发布日期',
    `effective_date` DATE                                       COMMENT '生效日期',
    `expire_date`    DATE                                       COMMENT '废止日期（NULL=现行有效）',
    `full_text`      LONGTEXT                                   COMMENT '法规全文',
    `summary`        TEXT                                       COMMENT '摘要',
    `keywords`       JSON                                       COMMENT '关键词列表 ["招标","公示期"]',
    `status`         TINYINT         DEFAULT 1                  COMMENT '状态: 1=有效 0=废止',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FULLTEXT INDEX `ft_title_text` (`title`, `full_text`),
    INDEX `idx_category` (`category`),
    INDEX `idx_status`   (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规库';


-- 6.2 法规条款表（依赖 law_regulation；细粒度条款拆分）
CREATE TABLE `law_clause` (
    `id`        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '条款ID',
    `law_id`    BIGINT UNSIGNED NOT NULL                   COMMENT '所属法规ID → law_regulation',
    `clause_no` VARCHAR(64)                                COMMENT '条款编号（如"第十五条"）',
    `title`     VARCHAR(256)                               COMMENT '条款标题',
    `content`   TEXT            NOT NULL                   COMMENT '条款内容',
    `keywords`  JSON                                       COMMENT '关键词',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX `idx_law_id` (`law_id`),

    CONSTRAINT `fk_clause_law` FOREIGN KEY (`law_id`)
        REFERENCES `law_regulation` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规条款表';


-- 6.3 历史案例库（ref_law_ids 存 JSON，不建物理 FK）
CREATE TABLE `case_library` (
    `id`           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '案例ID',
    `case_no`      VARCHAR(64)                               COMMENT '案例编号',
    `title`        VARCHAR(256)    NOT NULL                  COMMENT '案例标题',
    `case_type`    TINYINT                                   COMMENT '案例类型: 1=招投标违规 2=合同纠纷 3=施工变更纠纷 4=其他',
    `source`       VARCHAR(128)                              COMMENT '来源（裁判文书/审计报告等）',
    `case_date`    DATE                                      COMMENT '案例发生日期',
    `project_type` TINYINT                                   COMMENT '工程类型（同 project.project_type）',
    `issue_type`   TINYINT                                   COMMENT '问题类型（同 review_issue.issue_type）',
    `description`  TEXT                                      COMMENT '案例描述',
    `key_findings` TEXT                                      COMMENT '关键发现',
    `outcome`      TEXT                                      COMMENT '处理结果',
    `lesson`       TEXT                                      COMMENT '经验教训',
    `keywords`     JSON                                      COMMENT '关键词',
    `ref_law_ids`  JSON                                      COMMENT '相关法规ID列表（JSON，不建物理FK）',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FULLTEXT INDEX `ft_case` (`title`, `description`, `key_findings`),
    INDEX `idx_case_type`  (`case_type`),
    INDEX `idx_issue_type` (`issue_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史案例库';


-- 6.4 市场价格库（独立数据，无外键依赖）
CREATE TABLE `market_price` (
    `id`         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `item_code`  VARCHAR(64)     NOT NULL                  COMMENT '项目编码',
    `item_name`  VARCHAR(256)    NOT NULL                  COMMENT '项目名称',
    `unit`       VARCHAR(32)                               COMMENT '计量单位',
    `category`   TINYINT                                   COMMENT '类别: 1=人工费 2=材料费 3=机械费 4=综合单价',
    `price`      DECIMAL(18,4)   NOT NULL                  COMMENT '价格（元）',
    `price_date` DATE            NOT NULL                  COMMENT '价格日期',
    `region`     VARCHAR(64)                               COMMENT '适用地区',
    `source`     VARCHAR(128)                              COMMENT '数据来源',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY `uk_price_item_region_date` (`item_code`, `region`, `price_date`),
    INDEX `idx_item_code`  (`item_code`),
    INDEX `idx_price_date` (`price_date`),
    INDEX `idx_region`     (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='市场价格库';


-- 6.5 公开招标平台注册表（独立数据，无外键依赖）
CREATE TABLE `public_platform` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(128)    NOT NULL                  COMMENT '平台名称',
    `url`         VARCHAR(512)                              COMMENT '平台网址',
    `level`       TINYINT                                   COMMENT '级别: 1=国家级 2=省级 3=市级 4=区县级',
    `region`      VARCHAR(64)                               COMMENT '覆盖地区',
    `is_approved` TINYINT         DEFAULT 1                 COMMENT '是否认可合规平台: 1=是 0=否',
    `remark`      VARCHAR(256)                              COMMENT '备注',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_level`       (`level`),
    INDEX `idx_is_approved` (`is_approved`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公开招标平台注册表';


-- ============================================================
-- 模块七：AI 交互层（依赖 review_task、document）
-- ============================================================

-- 7.1 大模型调用日志（依赖 review_task；日志表不级联删除）
CREATE TABLE `llm_call_log` (
    `id`                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `task_id`           BIGINT UNSIGNED                            COMMENT '关联审查任务ID → review_task（NULL=非任务调用）',
    `call_type`         VARCHAR(64)                                COMMENT '调用类型，如 text_extract / consistency_check',
    `model_name`        VARCHAR(128)    NOT NULL                   COMMENT '模型名称',
    `model_version`     VARCHAR(64)                                COMMENT '模型版本',
    `prompt_tokens`     INT                                        COMMENT 'Prompt token 数',
    `completion_tokens` INT                                        COMMENT 'Completion token 数',
    `total_tokens`      INT                                        COMMENT '总 token 数',
    `latency_ms`        INT                                        COMMENT '响应延迟（ms）',
    `status`            TINYINT                                    COMMENT '调用状态: 1=成功 2=失败 3=超时',
    `error_msg`         VARCHAR(512)                               COMMENT '错误信息',
    `input_hash`        VARCHAR(64)                                COMMENT '输入内容 MD5（便于缓存命中判断）',
    `created_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX `idx_task_id`    (`task_id`),
    INDEX `idx_model`      (`model_name`),
    INDEX `idx_created_at` (`created_at`),

    -- 日志保留历史：任务删除时置 NULL 而非删除日志
    CONSTRAINT `fk_llm_log_task` FOREIGN KEY (`task_id`)
        REFERENCES `review_task` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大模型调用日志';


-- 7.2 文件结构化提取结果缓存（依赖 document；与 document 1:1）
CREATE TABLE `doc_extract_cache` (
    `id`           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `doc_id`       BIGINT UNSIGNED NOT NULL UNIQUE             COMMENT '关联文件ID → document（1:1）',
    `extract_type` VARCHAR(64)                                 COMMENT '提取类型标识，如 bid_announcement / contract',
    `result_json`  JSON                                        COMMENT '结构化提取结果',
    `model_name`   VARCHAR(128)                                COMMENT '提取使用的模型',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT `fk_extract_cache_doc` FOREIGN KEY (`doc_id`)
        REFERENCES `document` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件结构化提取缓存';


-- ============================================================
-- 模块八：系统辅助层（依赖 sys_user）
-- ============================================================

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


-- 8.2 通知消息表（依赖 sys_user）
CREATE TABLE `sys_notification` (
    `id`         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT UNSIGNED NOT NULL                     COMMENT '接收人ID → sys_user',
    `type`       TINYINT                                      COMMENT '通知类型: 1=任务完成 2=问题待处理 3=复核提醒 4=系统消息',
    `title`      VARCHAR(256)    NOT NULL                     COMMENT '标题',
    `content`    TEXT                                         COMMENT '内容',
    `ref_type`   VARCHAR(64)                                  COMMENT '关联对象类型，如 review_task / review_issue',
    `ref_id`     BIGINT UNSIGNED                              COMMENT '关联对象ID',
    `is_read`    TINYINT         DEFAULT 0                    COMMENT '是否已读: 1=已读 0=未读',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_user_unread` (`user_id`, `is_read`),
    INDEX `idx_created_at`  (`created_at`),

    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`)
        REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';


SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 外键关系汇总（供 ERD 校验）
-- ============================================================
-- sys_user.org_id                  → sys_org.id              SET NULL
-- sys_org.parent_id                → sys_org.id              RESTRICT（自引用）
-- project.build_org_id             → sys_org.id              SET NULL
-- project.contractor_id            → sys_org.id              SET NULL
-- project.supervisor_id            → sys_org.id              SET NULL
-- project.creator_id               → sys_user.id             SET NULL
-- document.project_id              → project.id              RESTRICT
-- document.uploader_id             → sys_user.id             SET NULL
-- doc_bid_announcement.doc_id      → document.id             CASCADE
-- doc_bid_announcement.project_id  → project.id              RESTRICT
-- doc_contract.doc_id              → document.id             CASCADE
-- doc_contract.project_id          → project.id              RESTRICT
-- change_request.project_id        → project.id              RESTRICT
-- change_request.apply_org_id      → sys_org.id              SET NULL
-- change_request.creator_id        → sys_user.id             SET NULL
-- change_request_doc.change_request_id → change_request.id  CASCADE
-- change_request_doc.doc_id        → document.id             RESTRICT
-- review_task.project_id           → project.id              RESTRICT
-- review_task.change_id            → change_request.id       SET NULL
-- review_task.assignee_id          → sys_user.id             SET NULL
-- review_task.creator_id           → sys_user.id             SET NULL
-- review_task_doc.task_id          → review_task.id          CASCADE
-- review_task_doc.doc_id           → document.id             RESTRICT
-- review_result.task_id            → review_task.id          RESTRICT
-- review_result.project_id         → project.id              RESTRICT
-- review_result.reviewer_id        → sys_user.id             SET NULL
-- review_item_result.result_id     → review_result.id        CASCADE
-- review_item_result.task_id       → review_task.id          RESTRICT
-- review_issue.item_result_id      → review_item_result.id   CASCADE
-- review_issue.result_id           → review_result.id        CASCADE
-- review_issue.project_id          → project.id              RESTRICT
-- review_issue.handled_by          → sys_user.id             SET NULL
-- law_clause.law_id                → law_regulation.id       CASCADE
-- llm_call_log.task_id             → review_task.id          SET NULL
-- doc_extract_cache.doc_id         → document.id             CASCADE
-- sys_operation_log.user_id        → sys_user.id             RESTRICT
-- sys_notification.user_id         → sys_user.id             CASCADE