insert into sys_org (id, name, type) values (1001, '审计局', 1);

insert into sys_user (
    id, username, password_hash, real_name, phone, email, org_id,
    role, status, avatar_url, last_login_at, created_at, updated_at
) values (
    1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    '系统管理员', '13800000000', 'admin@example.com', 1001,
    1, 1, null, null, current_timestamp, current_timestamp
);
