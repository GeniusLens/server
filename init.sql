-- 鉴权表
DROP TABLE IF EXISTS auth;

CREATE TABLE auth
(
    user_id         BIGINT       NOT NULL,
    password        VARCHAR(255) NOT NULL,
    salt            VARCHAR(255) NULL,
    last_login_time TIMESTAMP    NULL     DEFAULT CURRENT_TIMESTAMP,
    last_login_ip   VARCHAR(255) NULL     DEFAULT '',

    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT       NOT NULL,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      BIGINT       NOT NULL,
    is_deleted      BOOL         NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE auth IS '鉴权表';
COMMENT ON COLUMN auth.user_id IS '用户ID';
COMMENT ON COLUMN auth.password IS '密码';
COMMENT ON COLUMN auth.salt IS '盐';
COMMENT ON COLUMN auth.last_login_time IS '最后登录时间';
COMMENT ON COLUMN auth.last_login_ip IS '最后登录IP';

COMMENT ON COLUMN auth.id IS '主键ID';
COMMENT ON COLUMN auth.created_at IS '创建时间';
COMMENT ON COLUMN auth.created_by IS '创建人';
COMMENT ON COLUMN auth.updated_at IS '更新时间';
COMMENT ON COLUMN auth.updated_by IS '更新人';
COMMENT ON COLUMN auth.is_deleted IS '是否删除, 0: 否, 1: 是';

-- 用户表
DROP TABLE IF EXISTS "user";

CREATE TABLE "user"
(
    uid        VARCHAR(255) NOT NULL,
    -- 手机号码唯一，添加UNIQUE约束
    phone      VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL,
    avatar     VARCHAR(255) NULL,
    quote      VARCHAR(255) NULL,

    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT       NOT NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT       NOT NULL,
    is_deleted BOOL         NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE "user" IS '用户表';
COMMENT ON COLUMN "user".uid IS '用户ID';
COMMENT ON COLUMN "user".phone IS '手机号';
COMMENT ON COLUMN "user".nickname IS '昵称';
COMMENT ON COLUMN "user".avatar IS '头像URL';
COMMENT ON COLUMN "user".quote IS '签名';

-- 分类表
DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    parent_id   BIGINT       NULL,
    cover       VARCHAR(255) NULL,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

-- 功能表
DROP TABLE IF EXISTS function;

CREATE TABLE function
(
    category_id BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    url         VARCHAR(255) NOT NULL,
    type        VARCHAR(255) NOT NULL,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE function IS '功能表';
COMMENT ON COLUMN function.name IS '名称';
COMMENT ON COLUMN function.description IS '描述';
COMMENT ON COLUMN function.url IS '演示图的URL';
COMMENT ON COLUMN function.type IS '类型';

-- 示例表
DROP TABLE IF EXISTS sample;

CREATE TABLE sample
(
    function_id BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        int          NOT NULL DEFAULT 0,
    url         VARCHAR(255) NOT NULL,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE sample IS '示例表';
COMMENT ON COLUMN sample.function_id IS '功能ID, 0: 未分类';
COMMENT ON COLUMN sample.name IS '名称';
COMMENT ON COLUMN sample.type IS '类型';
COMMENT ON COLUMN sample.url IS '演示图的URL';

-- LORA 模型表
DROP TABLE IF EXISTS lora;

CREATE TABLE lora
(
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    images      VARCHAR(255) NULL,
    description VARCHAR(255) NULL,
    avatar      VARCHAR(255) NULL,
    is_default  BOOL         NOT NULL DEFAULT FALSE,
    status      int          NOT NULL DEFAULT 0,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE lora IS 'LORA模型表';
COMMENT ON COLUMN lora.user_id IS '用户ID';
COMMENT ON COLUMN lora.name IS '名称';
COMMENT ON COLUMN lora.images IS '训练图片的URL，多个图片以逗号分隔';
COMMENT ON COLUMN lora.description IS '描述';
COMMENT ON COLUMN lora.avatar IS '头像URL';
COMMENT ON COLUMN lora.is_default IS '是否默认模型';
COMMENT ON COLUMN lora.status IS '状态';

-- 消息
DROP TABLE IF EXISTS message;

CREATE TABLE message
(
    sender_id   BIGINT       NOT NULL,
    receiver_id BIGINT       NOT NULL,
    message     VARCHAR(255) NOT NULL,
    type        int          NOT NULL DEFAULT 0,
    status      int          NOT NULL DEFAULT 0,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

-- 任务
DROP TABLE IF EXISTS task;

CREATE TABLE task
(
    task_id     VARCHAR(255) NOT NULL,
    status      int          NOT NULL DEFAULT 0,
    function_id BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    message_id  BIGINT       NOT NULL,
    result      VARCHAR(255) NULL,

    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);

-- 动态
DROP TABLE IF EXISTS post;

CREATE TABLE post
(
    user_id     BIGINT       NOT NULL,
    function_id BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     VARCHAR(255) NOT NULL,
    images      VARCHAR(255) NULL,
    like_count  int          NOT NULL DEFAULT 0,


    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  BIGINT       NOT NULL,
    is_deleted  BOOL         NOT NULL DEFAULT FALSE
);