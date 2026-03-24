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
