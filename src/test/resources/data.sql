insert into sys_org (id, name, code, type, parent_id, address, status, created_at, updated_at)
values (1001, '审计局', 'ORG-1001', 1, null, '济南', 1, current_timestamp, current_timestamp);

insert into sys_org (id, name, code, type, parent_id, address, status, created_at, updated_at)
values (2001, '审计局-一处', 'ORG-2001', 1, 1001, '济南', 1, current_timestamp, current_timestamp);

insert into sys_user (
    id, username, password_hash, real_name, phone, email, org_id,
    role, status, avatar_url, last_login_at, created_at, updated_at
) values (
    1, 'admin', '$2b$12$8ZrYQOMn/l5/q8gNMchhEuGFolGmF0H0WNEb8fYMwj.bT5l.ViVYq',
    '系统管理员', '13800000000', 'admin@example.com', 1001,
    1, 1, null, null, current_timestamp, current_timestamp
);

insert into project (
    id, project_no, project_name, project_type, build_org_id, contractor_id, supervisor_id,
    total_investment, contract_amount, location, approval_no, approval_date, planned_start, planned_end,
    actual_start, actual_end, status, description, creator_id, created_at, updated_at
) values (
    1, 'PRJ-2026-001', '示例项目A', 1, 1001, 2001, null,
    1000000.00, 900000.00, '济南', 'AP-001', '2026-01-01', '2026-02-01', '2026-12-31',
    null, null, 2, '测试项目', 1, current_timestamp, current_timestamp
);

insert into review_task (id, task_no, project_id, task_type, change_id, task_name, status, priority, assignee_id, trigger_mode, creator_id, created_at, updated_at)
values
(1, 'RT-001', 1, 1, null, '任务1', 3, 2, 1, 1, 1, current_timestamp, current_timestamp),
(2, 'RT-002', 1, 1, null, '任务2', 2, 2, 1, 1, 1, current_timestamp, current_timestamp);

insert into review_result (id, task_id, project_id, overall_verdict, created_at, updated_at)
values
(1, 1, 1, 1, current_timestamp, current_timestamp),
(2, 2, 1, 2, current_timestamp, current_timestamp);

insert into review_item_result (id, result_id, task_id, check_dimension, dimension_name, verdict, created_at, updated_at)
values
(1, 1, 1, 11, '公开平台核验', 1, current_timestamp, current_timestamp),
(2, 2, 2, 12, '时间顺序', 2, current_timestamp, current_timestamp);

insert into review_issue (id, item_result_id, result_id, project_id, title, status, created_at, updated_at)
values
(1, 2, 2, 1, '问题1', 1, current_timestamp, current_timestamp),
(2, 2, 2, 1, '问题2', 3, current_timestamp, current_timestamp);

insert into change_request (id, project_id, change_no, status, created_at, updated_at)
values
(1, 1, 'CR-001', 1, current_timestamp, current_timestamp),
(2, 1, 'CR-002', 3, current_timestamp, current_timestamp);

insert into document (id, project_id, doc_type, doc_name, file_path, created_at, updated_at)
values
(1, 1, 1, '公告.pdf', '/tmp/1.pdf', current_timestamp, current_timestamp),
(2, 1, 5, '合同.pdf', '/tmp/2.pdf', current_timestamp, current_timestamp);

insert into review_task_doc (id, task_id, doc_id, doc_role)
values
(1, 1, 1, '公告主文档'),
(2, 2, 2, '合同主文档');

insert into law_regulation(id,title,short_name,category,status,created_at,updated_at)
values
(1,'测试1','杨腾泽',2,0,current_timestamp,current_timestamp),
(2,'测试2','李玉璇',1,1,current_timestamp,current_timestamp);

insert into law_clause(id, law_id, clause_no, title, content, keywords, created_at)
values
(1,1,"第一条",null,"测试1",'["key1","key2"]', current_timestamp),
(2,1,"第二条",null,"测试2",'["key1","key2","key3"]', current_timestamp);

insert into case_library(id, title, case_type, case_date, created_at, updated_at)
values
(1, "测试1", 1, '2026-02-01', current_timestamp, current_timestamp);

insert into market_price(id, item_code, item_name, category, price, price_date, region, created_at, updated_at)
values
(1, "1001", "测试项目", 1, 100.1, "2026-04-02", "中国滨州", current_timestamp, current_timestamp);

insert into public_platform(id, name, url, level, region, is_approved, created_at, updated_at)
values
(1, "测试平台", "https://www.baidu.com/", 1, "中国滨州", 1, current_timestamp, current_timestamp);