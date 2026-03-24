drop table if exists sys_operation_log;
drop table if exists sys_user;
drop table if exists sys_org;

create table sys_org (
    id bigint auto_increment primary key,
    name varchar(128) not null,
    code varchar(64),
    type tinyint,
    parent_id bigint,
    address varchar(256),
    status tinyint not null default 1,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    index idx_parent_id (parent_id),
    constraint fk_org_parent foreign key (parent_id) references sys_org(id)
);

create table sys_user (
    id bigint auto_increment primary key,
    username varchar(64) not null unique,
    password_hash varchar(255) not null,
    real_name varchar(64) not null,
    phone varchar(20),
    email varchar(128),
    org_id bigint,
    role tinyint not null default 2,
    status tinyint not null default 1,
    avatar_url varchar(512),
    last_login_at datetime,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    constraint fk_user_org foreign key (org_id) references sys_org(id)
);

create table sys_operation_log (
    id bigint auto_increment primary key,
    user_id bigint not null,
    module varchar(64),
    action varchar(64),
    object_type varchar(64),
    object_id bigint,
    detail text,
    ip varchar(64),
    created_at datetime not null default current_timestamp,
    constraint fk_op_log_user foreign key (user_id) references sys_user(id)
);
