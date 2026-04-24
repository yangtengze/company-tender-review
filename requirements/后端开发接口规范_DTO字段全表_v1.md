# 工程招投标智能审查系统 — 后端开发接口规范 DTO 字段全表 v2.0

> Java / Spring Boot 3 · Swagger / OpenAPI 3 · 基于 schema_v2.sql（22 表 37 FK）

---

## 全局约定

### 统一响应体 `R<T>`

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `code` | `Integer` | 业务状态码：0=成功，非 0=业务错误 |
| `message` | `String` | 提示信息，成功时为 `"ok"` |
| `data` | `T` | 实际返回数据，失败时为 null |
| `total` | `Long` | 分页总数，非分页接口为 null |
| `page` | `Integer` | 当前页码，非分页接口为 null |
| `size` | `Integer` | 每页条数，非分页接口为 null |

### 通用分页参数（所有列表 GET 均支持）

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `page` | `Integer` | N | `@Min(1)` | 页码，默认 1 |
| `size` | `Integer` | N | `@Min(1) @Max(100)` | 每页条数，默认 20 |
| `orderBy` | `String` | N | — | 排序字段，默认 `createdAt` |
| `order` | `String` | N | `@Pattern("ASC\|DESC")` | 排序方向，默认 DESC |

### FK 级联策略说明

| 策略 | 触发场景 | 涉及的表/字段 |
|------|----------|---------------|
| `RESTRICT` | 有子数据时禁止删除父行 | `project` / `review_task` / `document` 等核心表 |
| `CASCADE` | 父行删除时子行同步删除 | `doc_bid_announcement` / `doc_contract` / `review_issue` 等扩展表 |
| `SET NULL` | 父行删除时子行 FK 置 NULL | `creator_id` / `assignee_id` / `reviewer_id` 等人员字段 |

### Swagger 配置片段

```java
@Bean
OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("招投标审查系统 API").version("v1.0"))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components().addSecuritySchemes("bearerAuth",
            new SecurityScheme().type(HTTP).scheme("bearer").bearerFormat("JWT")));
}
```

### 实现状态约定（本版）

- 以下接口字段以当前代码实现为准（Controller + DTO）。
- 文中未特殊标记的接口均为**已实现**。
- 未落地但保留的接口将显式标注为：**规划中**。

---

## 模块一  认证 `/api/auth`

### 1.1 `POST /api/auth/login` — 用户登录

```
@Operation(summary="用户登录")  @Tag("Auth")
ReqDTO: LoginRequest   RespDTO: LoginResponse
```

**Request Body — `LoginRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `username` | `String` | **Y** | `@NotBlank @Size(max=64)` | 用户名 |
| `password` | `String` | **Y** | `@NotBlank @Size(min=6,max=32)` | 明文密码（HTTPS 传输） |

**Response Data — `LoginResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `accessToken` | `String` | JWT，有效期 2h；`Authorization: Bearer {token}` |
| `refreshToken` | `String` | Refresh Token，有效期 7d |
| `expiresIn` | `Long` | accessToken 剩余秒数，示例：7200 |
| `userId` | `Long` | 用户 ID |
| `username` | `String` | 用户名 |
| `realName` | `String` | 真实姓名 |
| `role` | `Integer` | 1=超管 2=审查员 3=项目负责人 4=只读 |
| `orgId` | `Long` | 所属机构 ID |
| `orgName` | `String` | 所属机构名称 |

---

### 1.2 `POST /api/auth/refresh` — 刷新 Token

```
@Operation(summary="刷新 Access Token")  @Tag("Auth")
ReqDTO: RefreshRequest   RespDTO: LoginResponse（同上）
```

**Request Body — `RefreshRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `refreshToken` | `String` | **Y** | `@NotBlank` | 登录时返回的 refreshToken |

> 返回结构同 `LoginResponse`，含新的 `accessToken` 和 `refreshToken`。

---

### 1.3 `GET /api/auth/me` — 当前登录用户信息

```
@Operation(summary="获取当前用户信息")  @Tag("Auth")
RespDTO: UserDetailResponse
```

**Response Data — `UserDetailResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 用户 ID |
| `username` | `String` | 用户名 |
| `realName` | `String` | 真实姓名 |
| `phone` | `String` | 手机号 |
| `email` | `String` | 邮箱 |
| `role` | `Integer` | 角色枚举 |
| `roleName` | `String` | 角色名称 |
| `status` | `Integer` | 1=启用 0=禁用 |
| `orgId` | `Long` | 机构 ID |
| `orgName` | `String` | 机构名称 |
| `orgType` | `Integer` | 1=审计 2=建设 3=施工 4=监理 |
| `avatarUrl` | `String` | 头像 URL |
| `lastLoginAt` | `String` | 最近登录时间 ISO8601 |
| `createdAt` | `String` | 创建时间 |

---

### 1.4 `POST /api/auth/logout` — 登出

```
@Operation(summary="登出，写操作日志，客户端丢弃 Token")  @Tag("Auth")
```

> 服务端无状态（JWT），登出仅写 `sys_operation_log`；如需主动失效，客户端清除本地 Token。

---

## 模块二  机构管理 `/api/orgs`

> `sys_org.parent_id` 为 NULL 表示顶层（v2 修正，原 DEFAULT 0 无法加自引用 FK）。删除含子机构的父机构会触发 RESTRICT 报错。

### 2.1 `GET /api/orgs` — 机构列表

```
@Operation(summary="查询机构列表（平铺或树形）")  @Tag("Org")
ReqDTO: OrgQueryRequest   RespDTO: List<OrgNode>
```

**Query Params — `OrgQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `type` | `Integer` | N | `@Min(1) @Max(4)` | 1=审计 2=建设 3=施工 4=监理 |
| `status` | `Integer` | N | `@Min(0) @Max(1)` | 状态过滤，默认只返回 status=1 |
| `tree` | `Boolean` | N | — | true=树形结构（含 children），false=平铺列表 |

**Response Data — `List<OrgNode>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 机构 ID |
| `name` | `String` | 机构名称 |
| `code` | `String` | 统一社会信用代码 |
| `type` | `Integer` | 类型枚举 |
| `typeName` | `String` | 类型名称 |
| `parentId` | `Long` | 上级机构 ID，null=顶层 |
| `parentName` | `String` | 上级机构名称 |
| `address` | `String` | 地址 |
| `status` | `Integer` | 状态 |
| `children` | `List<OrgNode>` | tree=true 时返回子机构列表 |
| `createdAt` | `String` | 创建时间 |

---

### 2.2 `POST /api/orgs` — 创建机构

```
@Operation(summary="创建机构")  @Tag("Org")
ReqDTO: OrgCreateRequest   RespDTO: OrgNode
```

**Request Body — `OrgCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `name` | `String` | **Y** | `@NotBlank @Size(max=128)` | 机构名称 |
| `code` | `String` | N | `@Size(max=64)` | 统一社会信用代码（唯一） |
| `type` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 机构类型 |
| `parentId` | `Long` | N | `@Positive` | 上级机构 ID，不传则为顶层（NULL） |
| `address` | `String` | N | `@Size(max=256)` | 地址 |

---

### 2.3 `PUT /api/orgs/{id}` — 更新机构信息

```
@Operation(summary="更新机构信息")  @Tag("Org")
ReqDTO: OrgUpdateRequest   RespDTO: OrgNode
```

**Request Body — `OrgUpdateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `name` | `String` | N | `@Size(max=128)` | 机构名称 |
| `code` | `String` | N | `@Size(max=64)` | 统一社会信用代码 |
| `address` | `String` | N | `@Size(max=256)` | 地址 |
| `status` | `Integer` | N | `@Min(0) @Max(1)` | 状态（仅超管可改） |

---

## 模块三  用户管理 `/api/users`

> CRUD 需 role=1 超管权限。`org_id` FK → `sys_org` ON DELETE SET NULL。软删除 status=0。

### 3.1 `GET /api/users` — 用户列表（分页）

```
@Operation(summary="查询用户分页列表")  @Tag("User")
ReqDTO: UserQueryRequest   RespDTO: Page<UserListItem>
```

**Query Params — `UserQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `orgId` | `Long` | N | — | 机构 ID 过滤 |
| `role` | `Integer` | N | `@Min(1) @Max(4)` | 角色过滤 |
| `status` | `Integer` | N | `@Min(0) @Max(1)` | 状态过滤 |
| `keyword` | `String` | N | `@Size(max=50)` | 模糊搜索：username 或 realName |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

**Response Data — `Page<UserListItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 用户 ID |
| `username` | `String` | 用户名 |
| `realName` | `String` | 真实姓名 |
| `phone` | `String` | 手机号 |
| `email` | `String` | 邮箱 |
| `role` | `Integer` | 角色枚举 |
| `roleName` | `String` | 角色名称 |
| `status` | `Integer` | 状态 |
| `orgId` | `Long` | 机构 ID |
| `orgName` | `String` | 机构名称 |
| `lastLoginAt` | `String` | 最近登录时间 |
| `createdAt` | `String` | 创建时间 |

---

### 3.2 `POST /api/users` — 创建用户

```
@Operation(summary="创建新用户，密码 BCrypt 加密后入库")  @Tag("User")
ReqDTO: UserCreateRequest   RespDTO: UserDetailResponse
```

**Request Body — `UserCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `username` | `String` | **Y** | `@NotBlank @Size(4,64) @Pattern("[a-zA-Z0-9_]+")` | 用户名 |
| `password` | `String` | **Y** | `@NotBlank @Size(8,32)` | 初始密码（服务端 BCrypt） |
| `realName` | `String` | **Y** | `@NotBlank @Size(max=64)` | 真实姓名 |
| `phone` | `String` | N | `@Pattern("1[3-9]\\d{9}")` | 手机号 |
| `email` | `String` | N | `@Email @Size(max=128)` | 邮箱 |
| `orgId` | `Long` | **Y** | `@NotNull @Positive` | 所属机构 ID（FK → sys_org） |
| `role` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 角色：2=审查员 3=项目负责人 4=只读 |

---

### 3.3 `PUT /api/users/{id}` — 修改用户信息

```
@Operation(summary="更新用户信息，超管可改 role/status")  @Tag("User")
ReqDTO: UserUpdateRequest   RespDTO: UserDetailResponse
```

**Request Body — `UserUpdateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `realName` | `String` | N | `@Size(max=64)` | 真实姓名 |
| `phone` | `String` | N | `@Pattern("1[3-9]\\d{9}")` | 手机号 |
| `email` | `String` | N | `@Email @Size(max=128)` | 邮箱 |
| `role` | `Integer` | N | `@Min(1) @Max(4)` | 角色（仅超管可改） |
| `status` | `Integer` | N | `@Min(0) @Max(1)` | 状态（仅超管可改） |
| `avatarUrl` | `String` | N | `@Size(max=512)` | 头像 URL |

---

### 3.4 `PATCH /api/users/{id}/pwd` — 修改密码

```
@Operation(summary="修改密码；超管可强制重置他人")  @Tag("User")
ReqDTO: ChangePasswordRequest
```

**Request Body — `ChangePasswordRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `oldPassword` | `String` | Y* | `@NotBlank` | 旧密码（超管重置他人时可不传） |
| `newPassword` | `String` | **Y** | `@NotBlank @Size(8,32)` | 新密码 |
| `confirmPassword` | `String` | **Y** | `@NotBlank` | 确认新密码，后端校验与 newPassword 一致 |

---

## 模块四  工程项目 `/api/projects`

> `build_org_id` / `contractor_id` / `supervisor_id` 均 FK → `sys_org` ON DELETE SET NULL。  
> 状态流转：1=立项 → 2=招标中 → 3=施工中 → 4=竣工 → 5=归档。

### 4.1 `GET /api/projects` — 项目列表（分页）

```
@Operation(summary="查询项目分页列表")  @Tag("Project")
ReqDTO: ProjectQueryRequest   RespDTO: Page<ProjectListItem>
```

**Query Params — `ProjectQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `status` | `Integer` | N | `@Min(1) @Max(5)` | 项目状态过滤 |
| `projectType` | `Integer` | N | `@Min(1) @Max(5)` | 1=房建 2=市政 3=水利 4=交通 5=其他 |
| `buildOrgId` | `Long` | N | — | 建设单位 ID 过滤 |
| `keyword` | `String` | N | `@Size(max=100)` | 项目名称或编号模糊搜索 |
| `plannedStartFrom` | `String` | N | `@DateTimeFormat` | 计划开工日期起始（yyyy-MM-dd） |
| `plannedStartTo` | `String` | N | `@DateTimeFormat` | 计划开工日期截止 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

**Response Data — `Page<ProjectListItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 项目 ID |
| `projectNo` | `String` | 项目编号（唯一） |
| `projectName` | `String` | 项目名称 |
| `projectType` | `Integer` | 工程类型枚举 |
| `projectTypeName` | `String` | 工程类型名称 |
| `status` | `Integer` | 项目状态 |
| `statusName` | `String` | 状态名称 |
| `buildOrgId` | `Long` | 建设单位 ID |
| `buildOrgName` | `String` | 建设单位名称 |
| `contractorName` | `String` | 施工单位名称 |
| `supervisorName` | `String` | 监理单位名称 |
| `totalInvestment` | `BigDecimal` | 概算总投资（元） |
| `contractAmount` | `BigDecimal` | 合同金额（元） |
| `plannedStart` | `String` | 计划开工日期 |
| `plannedEnd` | `String` | 计划竣工日期 |
| `location` | `String` | 项目地点 |
| `pendingIssues` | `Integer` | 待整改问题数（聚合字段） |
| `createdAt` | `String` | 创建时间 |

---

### 4.2 `POST /api/projects` — 创建项目

```
@Operation(summary="创建新工程项目")  @Tag("Project")
ReqDTO: ProjectCreateRequest   RespDTO: ProjectDetailResponse
```

**Request Body — `ProjectCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectNo` | `String` | **Y** | `@NotBlank @Size(max=64)` | 项目编号，全局唯一 |
| `projectName` | `String` | **Y** | `@NotBlank @Size(max=256)` | 项目名称 |
| `projectType` | `Integer` | **Y** | `@NotNull @Min(1) @Max(5)` | 工程类型 |
| `buildOrgId` | `Long` | **Y** | `@NotNull @Positive` | 建设单位 ID（FK → sys_org） |
| `contractorId` | `Long` | N | `@Positive` | 施工单位 ID（FK → sys_org） |
| `supervisorId` | `Long` | N | `@Positive` | 监理单位 ID（FK → sys_org） |
| `totalInvestment` | `BigDecimal` | N | `@DecimalMin("0.01")` | 概算总投资（元） |
| `contractAmount` | `BigDecimal` | N | `@DecimalMin("0.01")` | 合同金额（元） |
| `location` | `String` | N | `@Size(max=256)` | 项目地点 |
| `approvalNo` | `String` | N | `@Size(max=128)` | 立项批准文号 |
| `approvalDate` | `LocalDate` | N | — | 立项批准日期 |
| `plannedStart` | `LocalDate` | N | — | 计划开工日期 |
| `plannedEnd` | `LocalDate` | N | — | 计划竣工日期 |
| `description` | `String` | N | `@Size(max=2000)` | 项目简介 |

---

### 4.3 `PATCH /api/projects/{id}/status` — 更新项目状态

```
@Operation(summary="推进项目状态（状态机流转）")  @Tag("Project")
ReqDTO: ProjectStatusRequest   RespDTO: ProjectDetailResponse
```

**Request Body — `ProjectStatusRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `status` | `Integer` | **Y** | `@NotNull @Min(1) @Max(5)` | 目标状态（需符合流转规则） |
| `actualStart` | `LocalDate` | N | — | 实际开工日期（status=3 时建议填） |
| `actualEnd` | `LocalDate` | N | — | 实际竣工日期（status=4 时建议填） |

---

### 4.4 `GET /api/projects/{id}/stats` — 项目统计

```
@Operation(summary="项目汇总统计")  @Tag("Project")
RespDTO: ProjectStatsResponse
```

**Response Data — `ProjectStatsResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `tasksTotal` | `Integer` | 审查任务总数 |
| `tasksDone` | `Integer` | 已完成任务数 |
| `tasksRunning` | `Integer` | 执行中任务数 |
| `issuesTotal` | `Integer` | 问题总数 |
| `issuesPending` | `Integer` | 待整改问题数 |
| `issuesResolved` | `Integer` | 已整改问题数 |
| `changesTotal` | `Integer` | 变更申请总数 |
| `changesPending` | `Integer` | 待审查变更数 |
| `docsTotal` | `Integer` | 上传文件总数 |
| `complianceRate` | `Double` | 合规率（合规/总），示例：0.85 |

---

## 模块五  文件管理 `/api/documents`

> `document.project_id` FK RESTRICT（有文件的项目不可删除）。`uploader_id` FK SET NULL。  
> 扩展表（`doc_bid_announcement` / `doc_contract` / `doc_extract_cache`）CASCADE 随 document 删除。

### 5.1 `POST /api/documents/upload` — 上传文件

```
@Operation(summary="上传工程文件，解析异步进行")  @Tag("Document")
ReqDTO: Multipart + DocumentUploadRequest   RespDTO: DocumentDetailResponse
```

**Request — `multipart/form-data`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `file` | `MultipartFile` | **Y** | `@NotNull` | PDF/Word/Excel，最大 100MB |
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 所属项目 ID（FK → project RESTRICT） |
| `docType` | `Integer` | **Y** | `@NotNull @Min(1) @Max(99)` | 文件类型枚举（见附录 A） |
| `issueDate` | `String` | N | `@DateTimeFormat` | 文件出具日期 yyyy-MM-dd |
| `issuer` | `String` | N | `@Size(max=128)` | 出具单位/人 |
| `version` | `String` | N | `@Size(max=32)` | 版本号，默认 1.0 |
| `remark` | `String` | N | `@Size(max=512)` | 备注 |

**Response Data — `DocumentDetailResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 文件 ID |
| `projectId` | `Long` | 项目 ID |
| `docType` | `Integer` | 文件类型枚举 |
| `docTypeName` | `String` | 文件类型名称 |
| `docName` | `String` | 文件原始名称 |
| `filePath` | `String` | 存储路径（OSS Key） |
| `fileSize` | `Long` | 文件大小（Bytes） |
| `fileExt` | `String` | 后缀名，如 pdf |
| `md5` | `String` | 文件 MD5（用于秒传/去重） |
| `version` | `String` | 版本号 |
| `parseStatus` | `Integer` | 0=待解析 1=解析中 2=完成 3=失败 |
| `issueDate` | `String` | 出具日期 |
| `issuer` | `String` | 出具单位 |
| `uploaderId` | `Long` | 上传人 ID |
| `uploaderName` | `String` | 上传人姓名 |
| `createdAt` | `String` | 上传时间 |

---

### 5.2 `GET /api/documents` — 文件列表（分页）

```
@Operation(summary="查询项目文件列表")  @Tag("Document")
ReqDTO: DocumentQueryRequest   RespDTO: Page<DocumentDetailResponse>
```

**Query Params — `DocumentQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 必填，项目 ID |
| `docType` | `Integer` | N | `@Min(1) @Max(99)` | 文件类型过滤 |
| `parseStatus` | `Integer` | N | `@Min(0) @Max(5)` | 解析状态过滤 |
| `keyword` | `String` | N | `@Size(max=100)` | 文件名模糊搜索 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 5.3 `GET /api/documents/{id}/extract` — AI 结构化提取结果

```
@Operation(summary="获取文档 AI 提取结果，命中缓存直接返回，否则异步触发")  @Tag("Document")
RespDTO: ExtractResultResponse
```

**Response Data — `ExtractResultResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `docId` | `Long` | 文件 ID |
| `extractType` | `String` | 提取类型：`bid_announcement` / `contract` / `general` |
| `modelName` | `String` | 提取使用的模型 |
| `resultJson` | `Object` | 结构化 JSON（字段随 docType 动态变化，建议用 `Map<String,Object>` 接收） |
| `createdAt` | `String` | 提取时间 |
| `updatedAt` | `String` | 最后更新时间 |

---
### 5.4 `GET /api/documents/{id}/chunks` — 获取文档的 RAG 分块列表（支持树形）

```
@Operation(summary="获取文档的层次化切分结果")  @Tag("Document")
ReqDTO: ChunkQueryRequest   RespDTO: List<DocumentChunkNode>
```

**Query Params — `ChunkQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `tree` | `Boolean` | N | — | true=返回树形结构（基于 parentId），false=返回平铺列表（按 chunkIndex 排序） |

**Response Data — `List<DocumentChunkNode>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 块 ID |
| `docId` | `Long` | 文档 ID |
| `parentId` | `Long` | 父节点 ID |
| `chunkType` | `String` | 类型：`title` / `paragraph` / `table` / `list` |
| `chunkLevel` | `Integer` | 层级深度 |
| `chunkIndex` | `Integer` | 文档内顺序索引 |
| `content` | `String` | 块文本内容 |
| `tokenCount` | `Integer` | Token 数量 |
| `metadata` | `Map<String,Object>` | 附加元数据 |
| `children` | `List<DocumentChunkNode>` | `tree=true` 时返回子块列表 |

---

### 5.5 `POST /api/documents/chunks/batch` — 批量写入文档分块（含向量 ID）

```
@Operation(summary="批量写入包含 Vector ID 的 ChunkEntity 到 MySQL")  @Tag("Document")
ReqDTO: List<DocumentChunkCreateRequest>
```

**Request Body — `List<DocumentChunkCreateRequest>`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `clientChunkId` | `Long` | N | — | 客户端临时块 ID（用于回填 parentId） |
| `clientParentChunkId` | `Long` | N | — | 客户端临时父块 ID |
| `docId` | `Long` | **Y** | `@NotNull` | 文档 ID |
| `parentId` | `Long` | N | — | 父块 ID |
| `chunkType` | `String` | **Y** | `@NotBlank` | 分块类型 |
| `chunkLevel` | `Integer` | N | — | 分块层级 |
| `chunkIndex` | `Integer` | **Y** | `@NotNull` | 文档内顺序索引 |
| `content` | `String` | **Y** | `@NotBlank` | 分块文本 |
| `tokenCount` | `Integer` | N | — | Token 数 |
| `vectorId` | `String` | N | — | 向量库 ID |
| `metadata` | `Map<String,Object>` | N | — | 附加元数据 |

> 响应体 `R<Void>`，成功时 `code=0`。

---

### 5.6 `GET /api/documents/chunks/context-expansion` — 向量命中块上下文扩展

```
@Operation(summary="根据 vectorId 扩展上下文，返回命中块所属完整章节")  @Tag("Document")
ReqDTO: ContextExpandQueryRequest   RespDTO: ExpandedChunkContextResponse
```

**Query Params — `ContextExpandQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `vectorId` | `String` | **Y** | `@NotBlank @Size(max=128)` | 向量库返回的 chunk 向量 ID |

**Response Data — `ExpandedChunkContextResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `docId` | `Long` | 文档 ID |
| `vectorId` | `String` | 命中的向量 ID |
| `hitChunkId` | `Long` | 命中块 ID |
| `hitChunkIndex` | `Integer` | 命中块索引 |
| `titleChunkId` | `Long` | 所属章节标题块 ID |
| `title` | `String` | 章节标题 |
| `titleLevel` | `Integer` | 标题层级 |
| `sectionStartIndex` | `Integer` | 章节起始 chunkIndex |
| `sectionEndIndex` | `Integer` | 章节结束 chunkIndex |
| `contextText` | `String` | 聚合后的完整上下文文本 |
| `chunks` | `List<DocumentChunkNode>` | 该章节下的分块列表 |

## 模块六  招标公告扩展 `/api/bid-announcements`

> 与 document 1:1，`doc_id` FK CASCADE（document 删除则自动删除）。v2 新增 `updated_at`。

### 6.1 `POST /api/bid-announcements` — 录入招标公告扩展信息

```
@Operation(summary="录入招标公告关键字段")  @Tag("BidAnnouncement")
ReqDTO: BidAnnouncementCreateRequest   RespDTO: BidAnnouncementResponse
```

**Request Body — `BidAnnouncementCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `docId` | `Long` | **Y** | `@NotNull @Positive` | 关联 document.id（doc_type 必须=1） |
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 项目 ID（FK → project RESTRICT） |
| `bidNo` | `String` | N | `@Size(max=128)` | 招标编号 |
| `bidType` | `Integer` | N | `@Min(1) @Max(3)` | 1=公开 2=邀请 3=竞争性谈判 |
| `publishDate` | `LocalDateTime` | N | — | 公告发布时间 |
| `deadlineDate` | `LocalDateTime` | N | — | 投标截止时间（系统自动算公示期天数） |
| `bidOpenDate` | `LocalDateTime` | N | — | 开标时间 |
| `platformName` | `String` | N | `@Size(max=128)` | 发布平台名称 |
| `platformUrl` | `String` | N | `@URL @Size(max=512)` | 发布平台 URL |
| `qualificationReq` | `String` | N | — | 资质要求原文（长文本） |
| `performanceReq` | `String` | N | — | 业绩要求原文（长文本） |
| `estimatedPrice` | `BigDecimal` | N | `@DecimalMin("0")` | 招标控制价（元） |

**Response Data — `BidAnnouncementResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | ID |
| `docId` | `Long` | 关联文件 ID |
| `projectId` | `Long` | 项目 ID |
| `bidNo` | `String` | 招标编号 |
| `bidType` | `Integer` | 招标方式枚举 |
| `bidTypeName` | `String` | 招标方式名称 |
| `publishDate` | `String` | 发布时间 |
| `deadlineDate` | `String` | 截止时间 |
| `bidOpenDate` | `String` | 开标时间 |
| `publicNoticeDays` | `Integer` | 公示期天数（= `DATEDIFF(deadline, publish)`，系统计算） |
| `platformName` | `String` | 发布平台 |
| `platformUrl` | `String` | 平台 URL |
| `isPublicPlatform` | `Integer` | 1=合规平台 0=不合规 null=待核验 |
| `qualificationReq` | `String` | 资质要求 |
| `performanceReq` | `String` | 业绩要求 |
| `estimatedPrice` | `BigDecimal` | 控制价（元） |
| `updatedAt` | `String` | 最后更新时间（v2 新增） |

---

## 模块七  合同扩展 `/api/contracts`

> 与 document 1:1，`doc_id` FK CASCADE。v2 新增 `updated_at`。用于维度 13（合同一致性）审查。

### 7.1 `POST /api/contracts` — 录入合同扩展信息

```
@Operation(summary="录入施工合同关键字段")  @Tag("Contract")
ReqDTO: ContractCreateRequest   RespDTO: ContractResponse
```

**Request Body — `ContractCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `docId` | `Long` | **Y** | `@NotNull @Positive` | 关联 document.id（doc_type 必须=5） |
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 项目 ID |
| `contractNo` | `String` | N | `@Size(max=128)` | 合同编号 |
| `contractAmount` | `BigDecimal` | N | `@DecimalMin("0.01")` | 合同金额（元） |
| `signDate` | `LocalDate` | N | — | 签订日期 |
| `partyA` | `String` | N | `@Size(max=128)` | 甲方（建设单位） |
| `partyB` | `String` | N | `@Size(max=128)` | 乙方（施工单位） |
| `startDate` | `LocalDate` | N | — | 合同约定开工日期 |
| `endDate` | `LocalDate` | N | — | 合同约定竣工日期 |
| `warrantyPeriod` | `Integer` | N | `@Min(0)` | 质保期（月） |
| `paymentTerms` | `String` | N | — | 付款条款原文（长文本） |
| `penaltyTerms` | `String` | N | — | 违约条款原文（长文本） |

> Response Data `ContractResponse` 含以上所有字段 + `id` / `updatedAt`。

---

## 模块八  施工变更申请 `/api/change-requests`

> `project_id` FK RESTRICT；`apply_org_id` / `creator_id` FK SET NULL。  
> `change_ratio` 由后端自动计算（= changeAmount / originalAmount × 100），前端无需传。

### 8.1 `POST /api/change-requests` — 创建变更申请

```
@Operation(summary="提交施工变更申请")  @Tag("ChangeRequest")
ReqDTO: ChangeRequestCreateRequest   RespDTO: ChangeRequestDetailResponse
```

**Request Body — `ChangeRequestCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 所属项目 ID |
| `changeNo` | `String` | **Y** | `@NotBlank @Size(max=64)` | 变更编号（全局唯一） |
| `changeType` | `Integer` | **Y** | `@NotNull @Min(1) @Max(5)` | 1=设计 2=工程量 3=材料 4=工期 5=综合 |
| `changeReason` | `Integer` | N | `@Min(1) @Max(5)` | 1=设计缺陷 2=不可抗力 3=建设方要求 4=施工条件变化 5=其他 |
| `reasonDesc` | `String` | N | — | 原因详细描述 |
| `changeDesc` | `String` | **Y** | `@NotBlank` | 变更内容描述 |
| `originalAmount` | `BigDecimal` | N | `@DecimalMin("0")` | 原合同金额（元），可自动带入 project.contract_amount |
| `changeAmount` | `BigDecimal` | **Y** | `@NotNull` | 变更金额（元），可为负数表示减少 |
| `applyDate` | `LocalDate` | N | — | 申请日期，不传默认今日 |
| `applyOrgId` | `Long` | N | `@Positive` | 申请单位 ID（FK → sys_org SET NULL） |

**Response Data — `ChangeRequestDetailResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 变更申请 ID |
| `projectId` | `Long` | 项目 ID |
| `projectName` | `String` | 项目名称 |
| `changeNo` | `String` | 变更编号 |
| `changeType` | `Integer` | 变更类型枚举 |
| `changeTypeName` | `String` | 变更类型名称 |
| `changeReason` | `Integer` | 原因类型枚举 |
| `reasonDesc` | `String` | 原因描述 |
| `changeDesc` | `String` | 变更描述 |
| `originalAmount` | `BigDecimal` | 原合同金额（元） |
| `changeAmount` | `BigDecimal` | 变更金额（元） |
| `changeRatio` | `BigDecimal` | 变更占比（%），4 位小数，后端计算 |
| `applyDate` | `String` | 申请日期 |
| `applyOrgId` | `Long` | 申请单位 ID |
| `applyOrgName` | `String` | 申请单位名称 |
| `status` | `Integer` | 1=待审查 2=审查中 3=完成 4=已撤回 |
| `statusName` | `String` | 状态名称 |
| `docs` | `List<ChangeDocItem>` | 关联文件列表（见下） |
| `createdAt` | `String` | 创建时间 |

**内嵌结构 `ChangeDocItem`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `docId` | `Long` | 文件 ID |
| `docName` | `String` | 文件名 |
| `docType` | `Integer` | 文件类型枚举 |
| `docRole` | `Integer` | 1=变更方案 2=原设计图纸 3=工程量清单 4=佐证材料 |
| `docRoleName` | `String` | 角色名称 |
| `fileExt` | `String` | 后缀名 |
| `fileSize` | `Long` | 字节数 |

---

### 8.2 `POST /api/change-requests/{id}/docs` — 关联变更文件

```
@Operation(summary="为变更申请绑定已上传文件")  @Tag("ChangeRequest")
ReqDTO: ChangeDocBindRequest
```

**Request Body — `ChangeDocBindRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `docId` | `Long` | **Y** | `@NotNull @Positive` | 已上传文件 ID（必须属于同项目） |
| `docRole` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 文件角色 |

> `UNIQUE KEY uk_change_doc(change_request_id, doc_id)`，重复绑定返回 409 Conflict。

---

## 模块九  审查任务 `/api/review-tasks` ★ 核心

> 创建任务后自动加入执行队列（MQ/线程池），异步调用大模型，状态 1→2→3 流转。  
> `change_id` FK SET NULL；`assignee_id` / `creator_id` FK SET NULL。

### 9.1 `POST /api/review-tasks` — 创建审查任务

```
@Operation(summary="创建并排队审查任务")  @Tag("ReviewTask")
ReqDTO: ReviewTaskCreateRequest   RespDTO: ReviewTaskDetailResponse
```

**Request Body — `ReviewTaskCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 关联项目 ID（RESTRICT） |
| `taskType` | `Integer` | **Y** | `@NotNull @Min(1) @Max(2)` | 1=招投标审查 2=施工变更审查 |
| `taskName` | `String` | **Y** | `@NotBlank @Size(max=256)` | 任务名称 |
| `changeId` | `Long` | N | `@Positive` | 关联变更申请 ID（taskType=2 时必填） |
| `docIds` | `List<Long>` | **Y** | `@NotEmpty @Size(max=20)` | 本次审查使用的文件 ID 列表 |
| `docRoles` | `Map<Long,String>` | N | — | 各文件角色说明，key=docId，可选 |
| `priority` | `Integer` | N | `@Min(1) @Max(3)` | 1=低 2=中 3=高，默认 2 |
| `assigneeId` | `Long` | N | `@Positive` | 指派审查员 ID（FK SET NULL） |
| `triggerMode` | `Integer` | N | `@Min(1) @Max(2)` | 1=手动 2=自动，默认 1 |

**Response Data — `ReviewTaskDetailResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 任务 ID |
| `taskNo` | `String` | 任务编号（系统生成，格式：`RT-yyyyMMdd-xxxx`） |
| `projectId` | `Long` | 项目 ID |
| `projectName` | `String` | 项目名称 |
| `taskType` | `Integer` | 任务类型 |
| `taskTypeName` | `String` | 类型名称 |
| `changeId` | `Long` | 关联变更申请 ID（null=招投标任务） |
| `taskName` | `String` | 任务名称 |
| `status` | `Integer` | 1=待执行 2=执行中 3=已完成 4=已失败 5=已取消 |
| `statusName` | `String` | 状态名称 |
| `priority` | `Integer` | 优先级 |
| `assigneeId` | `Long` | 审查员 ID |
| `assigneeName` | `String` | 审查员姓名 |
| `startAt` | `String` | 开始时间 |
| `endAt` | `String` | 完成时间 |
| `durationMs` | `Integer` | 耗时毫秒 |
| `docs` | `List<TaskDocItem>` | 关联文件列表 |
| `createdAt` | `String` | 创建时间 |

---

### 9.2 `GET /api/review-tasks` — 任务列表（分页）

```
@Operation(summary="查询审查任务列表")  @Tag("ReviewTask")
ReqDTO: ReviewTaskQueryRequest   RespDTO: Page<ReviewTaskDetailResponse>
```

**Query Params — `ReviewTaskQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | N | — | 项目 ID 过滤 |
| `taskType` | `Integer` | N | `@Min(1) @Max(2)` | 任务类型过滤 |
| `status` | `Integer` | N | `@Min(1) @Max(5)` | 状态过滤 |
| `assigneeId` | `Long` | N | — | 审查员 ID 过滤 |
| `dateFrom` | `String` | N | `@DateTimeFormat` | 创建时间起始 |
| `dateTo` | `String` | N | `@DateTimeFormat` | 创建时间截止 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 9.3 `GET /api/review-tasks/{id}/result` — 完整审查结果

```
@Operation(summary="获取任务的完整审查结果（总结论 + 8 个子项 + 问题清单）")  @Tag("ReviewTask")
RespDTO: ReviewFullResultResponse
```

**Response Data — `ReviewFullResultResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `taskId` | `Long` | 任务 ID |
| `taskName` | `String` | 任务名称 |
| `projectId` | `Long` | 项目 ID |
| `projectName` | `String` | 项目名称 |
| `overallVerdict` | `Integer` | 总体结论 1=合规 2=存在问题 3=严重违规 |
| `verdictName` | `String` | 结论名称 |
| `riskLevel` | `Integer` | 风险等级 1=低 2=中 3=高 4=极高 |
| `riskLevelName` | `String` | 风险等级名称 |
| `summary` | `String` | AI 生成审查总结 |
| `suggestion` | `String` | AI 生成处理建议 |
| `issueCount` | `Integer` | 问题总数 |
| `modelName` | `String` | 使用的大模型 |
| `tokensUsed` | `Integer` | 消耗 Token 数 |
| `reviewStatus` | `Integer` | 复核状态 1=待复核 2=已确认 3=已驳回 |
| `reviewStatusName` | `String` | 复核状态名称 |
| `reviewerName` | `String` | 复核人姓名 |
| `reviewerNote` | `String` | 复核意见 |
| `reviewedAt` | `String` | 复核时间 |
| `items` | `List<ReviewItemResult>` | 子项结果列表（最多 8 条，每维度一条） |
| `issues` | `List<ReviewIssueItem>` | 问题清单列表 |
| `createdAt` | `String` | 审查完成时间 |

**内嵌结构 `ReviewItemResult`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 子项 ID |
| `checkDimension` | `Integer` | 维度编码（11-15=招投标，21-23=变更，见附录 B） |
| `dimensionName` | `String` | 维度名称 |
| `verdict` | `Integer` | 1=合规 2=存在问题 3=严重违规 4=无法判断 |
| `verdictName` | `String` | 结论名称 |
| `confidence` | `BigDecimal` | AI 置信度 0.0000~1.0000 |
| `detail` | `String` | AI 详细分析内容 |
| `evidence` | `String` | 判断依据/引用原文片段 |
| `issueDesc` | `String` | 问题描述（无问题时为 null） |
| `suggestion` | `String` | 改进建议 |
| `refLaws` | `List<LawRef>` | 引用法规列表 `[{id, title, clauseNo}]` |
| `refCases` | `List<CaseRef>` | 引用案例列表 `[{id, title}]` |
| `updatedAt` | `String` | 最后更新时间（v2 新增） |

**内嵌结构 `ReviewIssueItem`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 问题 ID |
| `issueType` | `Integer` | 1=程序违规 2=内容不一致 3=时序异常 4=条款违规 5=成本异常 6=其他 |
| `typeName` | `String` | 问题类型名称 |
| `severity` | `Integer` | 1=提示 2=警告 3=错误 4=严重 |
| `severityName` | `String` | 严重程度名称 |
| `title` | `String` | 问题标题 |
| `description` | `String` | 问题详述 |
| `location` | `String` | 问题所在文件及位置 |
| `suggestion` | `String` | 处置建议 |
| `status` | `Integer` | 1=待整改 2=整改中 3=已整改 4=已忽略 |
| `statusName` | `String` | 整改状态名称 |
| `updatedAt` | `String` | 最后更新时间（v2 新增） |

---

## 模块十  审查结果 `/api/review-results`

> `review_result` 由系统写入，人工仅做复核（PATCH）。`reviewer_id` FK SET NULL；`project_id` / `task_id` FK RESTRICT。

### 10.1 `GET /api/review-results` — 结果列表（分页）

```
@Operation(summary="查询审查结果分页列表")  @Tag("ReviewResult")
ReqDTO: ReviewResultQueryRequest   RespDTO: Page<ReviewResultListItem>
```

**Query Params — `ReviewResultQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | N | — | 项目 ID 过滤 |
| `overallVerdict` | `Integer` | N | `@Min(1) @Max(3)` | 总体结论过滤 |
| `riskLevel` | `Integer` | N | `@Min(1) @Max(4)` | 风险等级过滤 |
| `reviewStatus` | `Integer` | N | `@Min(1) @Max(3)` | 复核状态过滤 |
| `taskType` | `Integer` | N | `@Min(1) @Max(2)` | 任务类型过滤 |
| `dateFrom` | `String` | N | `@DateTimeFormat` | 完成时间起始 |
| `dateTo` | `String` | N | `@DateTimeFormat` | 完成时间截止 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 10.2 `PATCH /api/review-results/{id}/review` — 人工复核

```
@Operation(summary="审查员对 AI 结果进行确认或驳回")  @Tag("ReviewResult")
ReqDTO: ReviewConfirmRequest   RespDTO: ReviewResultDetailResponse
```

**Request Body — `ReviewConfirmRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `reviewStatus` | `Integer` | **Y** | `@NotNull @Min(2) @Max(3)` | 2=已确认 3=已驳回 |
| `reviewerNote` | `String` | N | `@Size(max=2000)` | 复核意见，驳回时建议必填 |

> `reviewerId` = 当前登录用户 ID；`reviewedAt` = NOW()，由后端自动填入。

---

### 10.3 `GET /api/review-results/{id}/export` — 导出审查报告

```
@Operation(summary="导出审查报告（PDF 或 Word）")  @Tag("ReviewResult")
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `format` | `String` | N | `@Pattern("pdf\|word")` | 导出格式，默认 pdf |

> 响应 `Content-Type: application/pdf` 或 `application/vnd.openxmlformats…`，前端直接触发下载。

---

## 模块十一  问题清单 `/api/review-issues`

> `item_result_id` / `result_id` FK CASCADE（审查结果删除时问题随之删除）。`handled_by` FK SET NULL。v2 新增 `updated_at`。

### 11.1 `GET /api/review-issues` — 问题列表（分页）

```
@Operation(summary="查询问题清单")  @Tag("ReviewIssue")
ReqDTO: IssueQueryRequest   RespDTO: Page<IssueDetailResponse>
```

**Query Params — `IssueQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | N | — | 项目 ID 过滤 |
| `resultId` | `Long` | N | — | 审查结果 ID 过滤 |
| `severity` | `Integer` | N | `@Min(1) @Max(4)` | 严重程度（建议前端默认 severity>=3） |
| `issueType` | `Integer` | N | `@Min(1) @Max(6)` | 问题类型过滤 |
| `status` | `Integer` | N | `@Min(1) @Max(4)` | 整改状态过滤 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 11.2 `PATCH /api/review-issues/{id}/handle` — 整改跟进

```
@Operation(summary="更新问题整改状态")  @Tag("ReviewIssue")
ReqDTO: IssueHandleRequest   RespDTO: IssueDetailResponse
```

**Request Body — `IssueHandleRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `status` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 1=待整改 2=整改中 3=已整改 4=已忽略 |
| `handleNote` | `String` | N | `@Size(max=2000)` | 整改说明（status=3 建议必填） |

> `handledBy` = 当前登录用户 ID；`handledAt` = NOW()，后端自动填入。

---

### 11.3 `GET /api/review-issues/statistic` — 问题统计（当前实现）

```
@Operation(summary="按项目统计各级别问题数量")  @Tag("ReviewIssue")
RespDTO: IssueStatsResponse
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | **Y** | `@NotNull @Positive` | 项目 ID |

**Response Data — `IssueStatsResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `total` | `Integer` | 总数 |
| `pending` | `Integer` | 待整改 |
| `handling` | `Integer` | 整改中 |
| `resolved` | `Integer` | 已整改 |
| `ignored` | `Integer` | 已忽略 |
| `bySeverity` | `List<SeverityCount>` | `[{severity, severityName, total, resolved}]` |
| `byType` | `List<TypeCount>` | `[{issueType, typeName, total}]` |

---

### 11.4 `GET /api/review-issues/statistics` — 问题统计（规划中）

> 说明：该路径为兼容别名，当前代码未实现；建议后续与 `statistic` 保持同参同返回。

---

## 模块十二  法规知识库 `/api/laws`

> `law_clause.law_id` FK CASCADE（法规删除，条款同步删除）。  
> `FULLTEXT INDEX ft_title_text(title, full_text)`，关键词检索使用 BOOLEAN MODE。

### 12.1 `GET /api/laws` — 法规列表（全文检索）

```
@Operation(summary="全文检索法规库")  @Tag("Law")
ReqDTO: LawQueryRequest   RespDTO: Page<LawListItem>
```

**Query Params — `LawQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `category` | `Integer` | N | `@Min(1) @Max(5)` | 1=法律 2=行政法规 3=部门规章 4=地方性法规 5=标准规范 |
| `status` | `Integer` | N | `@Min(0) @Max(1)` | 状态，默认只返回 status=1（有效） |
| `keyword` | `String` | N | `@Size(max=100)` | 全文检索关键词（FULLTEXT BOOLEAN MODE） |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

**Response Data — `Page<LawListItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | ID |
| `title` | `String` | 法规标题 |
| `shortName` | `String` | 简称 |
| `lawNo` | `String` | 法规编号 |
| `category` | `Integer` | 类别枚举 |
| `categoryName` | `String` | 类别名称 |
| `issuer` | `String` | 发布机关 |
| `issueDate` | `String` | 发布日期 |
| `effectiveDate` | `String` | 生效日期 |
| `expireDate` | `String` | 废止日期（null=现行有效） |
| `status` | `Integer` | 1=有效 0=废止 |
| `keywords` | `List<String>` | 关键词列表 |
| `summary` | `String` | 摘要 |

---

### 12.2 `POST /api/laws` — 新增法规

```
@Operation(summary="录入新法规全文")  @Tag("Law")
ReqDTO: LawCreateRequest   RespDTO: LawDetailResponse
```

**Request Body — `LawCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `title` | `String` | **Y** | `@NotBlank @Size(max=256)` | 法规标题 |
| `shortName` | `String` | N | `@Size(max=128)` | 简称 |
| `lawNo` | `String` | N | `@Size(max=128)` | 法规编号 |
| `category` | `Integer` | **Y** | `@NotNull @Min(1) @Max(5)` | 类别 |
| `issuer` | `String` | N | `@Size(max=128)` | 发布机关 |
| `issueDate` | `LocalDate` | N | — | 发布日期 |
| `effectiveDate` | `LocalDate` | N | — | 生效日期 |
| `fullText` | `String` | **Y** | `@NotBlank` | 法规全文（LONGTEXT） |
| `summary` | `String` | N | `@Size(max=2000)` | 摘要 |
| `keywords` | `List<String>` | N | — | 关键词列表 |

---

### 12.3 `GET /api/laws/{id}/clauses` — 法规条款列表

```
@Operation(summary="获取法规细粒度条款")  @Tag("Law")
ReqDTO: ClauseQueryRequest   RespDTO: List<LawClause>
```

**Query Params — `ClauseQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `clauseNo` | `String` | N | `@Size(max=64)` | 条款编号精确/前缀匹配，如"第十五条" |
| `keyword` | `String` | N | `@Size(max=100)` | 条款内容关键词 |

**Response Data — `List<LawClause>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 条款 ID |
| `lawId` | `Long` | 所属法规 ID |
| `clauseNo` | `String` | 条款编号 |
| `title` | `String` | 条款标题 |
| `content` | `String` | 条款内容 |
| `keywords` | `List<String>` | 关键词 |

---

## 模块十三  历史案例库 `/api/cases`

> `ref_law_ids` 存 JSON，不建物理 FK（跨知识库软关联）。`FULLTEXT INDEX ft_case(title, description, key_findings)`。v2 新增 `updated_at`。

### 13.1 `GET /api/cases` — 案例检索

```
@Operation(summary="全文检索历史案例")  @Tag("Case")
ReqDTO: CaseQueryRequest   RespDTO: Page<CaseListItem>
```

**Query Params — `CaseQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `caseType` | `Integer` | N | `@Min(1) @Max(4)` | 1=招投标违规 2=合同纠纷 3=变更纠纷 4=其他 |
| `issueType` | `Integer` | N | `@Min(1) @Max(6)` | 问题类型过滤 |
| `projectType` | `Integer` | N | `@Min(1) @Max(5)` | 工程类型过滤 |
| `keyword` | `String` | N | `@Size(max=100)` | 全文检索关键词 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 13.2 `POST /api/cases` — 新增案例

```
@Operation(summary="录入历史案例")  @Tag("Case")
ReqDTO: CaseCreateRequest   RespDTO: CaseDetailResponse
```

**Request Body — `CaseCreateRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `title` | `String` | **Y** | `@NotBlank @Size(max=256)` | 案例标题 |
| `caseType` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 案例类型 |
| `source` | `String` | N | `@Size(max=128)` | 来源 |
| `caseDate` | `LocalDate` | N | — | 发生日期 |
| `projectType` | `Integer` | N | `@Min(1) @Max(5)` | 工程类型 |
| `issueType` | `Integer` | N | `@Min(1) @Max(6)` | 问题类型 |
| `description` | `String` | **Y** | `@NotBlank` | 案例描述 |
| `keyFindings` | `String` | N | — | 关键发现 |
| `outcome` | `String` | N | — | 处理结果 |
| `lesson` | `String` | N | — | 经验教训 |
| `keywords` | `List<String>` | N | — | 关键词 |
| `refLawIds` | `List<Long>` | N | — | 相关法规 ID（JSON，软关联） |

---

## 模块十四  市场价格库 `/api/market-prices`

> v2 新增 `UNIQUE KEY uk_price_item_region_date(item_code, region, price_date)`，批量导入用 `ON DUPLICATE KEY UPDATE`。

### 14.1 `GET /api/market-prices` — 价格查询

```
@Operation(summary="查询市场价格，支持时间范围过滤")  @Tag("MarketPrice")
ReqDTO: PriceQueryRequest   RespDTO: Page<MarketPriceItem>
```

**Query Params — `PriceQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `itemCode` | `String` | N | `@Size(max=64)` | 项目编码前缀匹配 |
| `keyword` | `String` | N | `@Size(max=100)` | 项目名称关键词 |
| `category` | `Integer` | N | `@Min(1) @Max(4)` | 1=人工 2=材料 3=机械 4=综合单价 |
| `region` | `String` | N | `@Size(max=64)` | 地区精确匹配 |
| `priceDateFrom` | `String` | N | `@DateTimeFormat` | 价格日期范围起始（yyyy-MM） |
| `priceDateTo` | `String` | N | `@DateTimeFormat` | 价格日期范围截止 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

---

### 14.2 `POST /api/market-prices/batch` — 批量导入

```
@Operation(summary="批量导入市场价格，最多 1000 条")  @Tag("MarketPrice")
ReqDTO: List<MarketPriceCreateRequest>   RespDTO: BatchImportResult
```

**Request Body — `List<MarketPriceCreateRequest>`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `itemCode` | `String` | **Y** | `@NotBlank @Size(max=64)` | 项目编码 |
| `itemName` | `String` | **Y** | `@NotBlank @Size(max=256)` | 项目名称 |
| `unit` | `String` | N | `@Size(max=32)` | 计量单位 |
| `category` | `Integer` | **Y** | `@NotNull @Min(1) @Max(4)` | 类别 |
| `price` | `BigDecimal` | **Y** | `@NotNull @DecimalMin("0")` | 价格（元） |
| `priceDate` | `LocalDate` | **Y** | `@NotNull` | 价格日期 |
| `region` | `String` | N | `@Size(max=64)` | 适用地区 |
| `source` | `String` | N | `@Size(max=128)` | 数据来源 |

**Response Data — `BatchImportResult`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `total` | `Integer` | 提交总条数 |
| `success` | `Integer` | 成功导入条数 |
| `updated` | `Integer` | 覆盖更新条数（触发 ON DUPLICATE KEY UPDATE） |
| `failed` | `Integer` | 失败条数 |
| `errors` | `List<String>` | 失败原因列表（行号+错误信息） |

---

### 14.3 `GET /api/market-prices/compare` — 价格趋势

```
@Operation(summary="查询指定物料近 N 月价格时序（变更经济性图表数据源）")  @Tag("MarketPrice")
RespDTO: List<PriceTrendItem>
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `itemCode` | `String` | **Y** | `@NotBlank` | 物料编码 |
| `region` | `String` | **Y** | `@NotBlank` | 地区 |
| `months` | `Integer` | N | `@Min(1) @Max(60)` | 查询最近月数，默认 12 |

**Response Data — `List<PriceTrendItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `priceDate` | `String` | 价格日期 yyyy-MM |
| `price` | `BigDecimal` | 价格（元） |
| `region` | `String` | 地区 |
| `source` | `String` | 数据来源 |

---

## 模块十五  招标平台库 `/api/platforms`

> v2 新增 `updated_at`。`is_approved` 是审查维度 11（公开平台核验）的核验数据源。

### 15.1 `PATCH /api/platforms/verify` — 平台合规性核验

```
@Operation(summary="根据 URL 或名称核验是否合规平台（供维度 11 实时调用）")  @Tag("Platform")
ReqDTO: PlatformVerifyRequest   RespDTO: PlatformVerifyResult
```

**Request Body — `PlatformVerifyRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `url` | `String` | N* | `@URL @Size(max=512)` | url 与 name 至少传一个 |
| `name` | `String` | N* | `@Size(max=128)` | url 与 name 至少传一个 |

**Response Data — `PlatformVerifyResult`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `isApproved` | `Boolean` | true=合规 false=不合规 null=未找到记录 |
| `platformId` | `Long` | 匹配到的平台 ID |
| `platformName` | `String` | 平台名称 |
| `level` | `Integer` | 1=国家 2=省 3=市 4=区县 |
| `levelName` | `String` | 级别名称 |
| `region` | `String` | 覆盖地区 |
| `matchScore` | `Double` | 匹配置信度（模糊匹配时 < 1.0） |

---

### 15.2 `GET /api/platforms` — 招标平台列表（当前实现）

```
@Operation(summary="查询招标平台列表")  @Tag("Platform")
ReqDTO: PlatformQueryRequest   RespDTO: List<PlatformItem>
```

**Query Params — `PlatformQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `level` | `Integer` | N | `@Min(1) @Max(4)` | 平台等级过滤 |
| `region` | `String` | N | `@Size(max=64)` | 覆盖地区过滤 |
| `isApproved` | `Integer` | N | `@Min(0) @Max(1)` | 合规状态过滤 |

**Response Data — `List<PlatformItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 平台 ID |
| `name` | `String` | 平台名称 |
| `url` | `String` | 平台 URL |
| `level` | `Integer` | 平台等级 |
| `levelName` | `String` | 等级名称 |
| `region` | `String` | 覆盖地区 |
| `isApproved` | `Integer` | 1=合规 0=不合规 |
| `remark` | `String` | 备注 |
| `createdAt` | `String` | 创建时间 |
| `updatedAt` | `String` | 更新时间 |

---

## 模块十六  AI 调用日志 `/api/llm-logs`

> `task_id` FK SET NULL（任务删除后日志保留）。只读接口，仅超管可访问。

### 16.1 `GET /api/llm-logs/summary` — 调用汇总统计

```
@Operation(summary="按模型统计 Token 消耗/延迟/失败率")  @Tag("LLMLog")
RespDTO: List<LLMLogSummary>
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `dateFrom` | `String` | N | `@DateTimeFormat` | 起始时间，默认最近 30 天 |
| `dateTo` | `String` | N | `@DateTimeFormat` | 截止时间 |

**Response Data — `List<LLMLogSummary>`（按 model_name 分组）**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `modelName` | `String` | 模型名称 |
| `callCount` | `Integer` | 调用次数 |
| `tokensTotal` | `Long` | Token 总消耗 |
| `promptTokens` | `Long` | Prompt Token 总量 |
| `completionTokens` | `Long` | Completion Token 总量 |
| `failedCount` | `Integer` | 失败次数 |
| `failRate` | `Double` | 失败率（百分比） |
| `avgLatencyMs` | `Double` | 平均延迟（ms） |
| `p95LatencyMs` | `Double` | P95 延迟（ms，**规划中**，当前 DTO 未返回） |

---

### 16.2 `GET /api/llm-logs` — 调用日志列表（当前实现）

```
@Operation(summary="查询大模型调用日志")  @Tag("LLMLog")
ReqDTO: LLMLogQueryRequest   RespDTO: Page<LLMLogItem>
```

**Query Params — `LLMLogQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `taskId` | `Long` | N | — | 任务 ID 过滤 |
| `modelName` | `String` | N | `@Size(max=128)` | 模型名称 |
| `status` | `Integer` | N | `@Min(1) @Max(3)` | 调用状态 |
| `dateFrom` | `String` | N | `@DateTimeFormat` | 起始时间 |
| `dateTo` | `String` | N | `@DateTimeFormat` | 截止时间 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

**Response Data — `Page<LLMLogItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 日志 ID |
| `taskId` | `Long` | 关联任务 ID |
| `callType` | `String` | 调用类型 |
| `modelName` | `String` | 模型名称 |
| `modelVersion` | `String` | 模型版本 |
| `promptTokens` | `Integer` | Prompt Token |
| `completionTokens` | `Integer` | Completion Token |
| `totalTokens` | `Integer` | 总 Token |
| `latencyMs` | `Integer` | 延迟（ms） |
| `status` | `Integer` | 调用状态 |
| `errorMsg` | `String` | 错误信息 |
| `createdAt` | `String` | 创建时间 |

---

## 模块十七  通知消息 `/api/notifications`

> `user_id` FK CASCADE（用户注销时通知随之删除）。v2 新增 `updated_at`。

### 17.1 `GET /api/notifications` — 通知列表

```
@Operation(summary="获取当前用户通知列表")  @Tag("Notification")
ReqDTO: NotificationQueryRequest   RespDTO: Page<NotificationItem>
```

**Query Params — `NotificationQueryRequest`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `isRead` | `Integer` | N | `@Min(0) @Max(1)` | 0=未读 1=已读，不传返回全部 |
| `type` | `Integer` | N | `@Min(1) @Max(4)` | 1=任务完成 2=问题待处理 3=复核提醒 4=系统消息 |
| `page` | `Integer` | N | `@Min(1)` | 页码 |
| `size` | `Integer` | N | `@Max(100)` | 每页条数 |

**Response Data — `Page<NotificationItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `id` | `Long` | 通知 ID |
| `type` | `Integer` | 类型枚举 |
| `typeName` | `String` | 类型名称 |
| `title` | `String` | 标题 |
| `content` | `String` | 内容 |
| `refType` | `String` | 关联对象类型，如 `review_task` / `review_issue` |
| `refId` | `Long` | 关联对象 ID |
| `isRead` | `Integer` | 0=未读 1=已读 |
| `createdAt` | `String` | 发送时间 |

---

### 17.2 `PATCH /api/notifications/read-all` — 全部已读

```
@Operation(summary="将当前用户所有未读通知标记为已读")  @Tag("Notification")
```

---

### 17.3 `PATCH /api/notifications/{id}/read` — 单条已读（当前实现）

```
@Operation(summary="将单条通知标记为已读")  @Tag("Notification")
```

---

## 模块十八  统计看板 `/api/dashboard`

> 纯只读聚合，建议 Redis 缓存 TTL=300s。

### 18.1 `GET /api/dashboard/overview` — 系统总览

```
@Operation(summary="首页大屏总览数据")  @Tag("Dashboard")
RespDTO: DashboardOverviewResponse
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `orgId` | `Long` | N | — | 按机构过滤，不传返回全局数据（超管专用） |

**Response Data — `DashboardOverviewResponse`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `activeProjects` | `Integer` | 进行中项目数（status < 5） |
| `pendingTasks` | `Integer` | 待执行/执行中任务数 |
| `completedTasks` | `Integer` | 已完成任务数 |
| `highRiskIssues` | `Integer` | 高风险问题数（severity >= 3 且 status=1） |
| `pendingChanges` | `Integer` | 待审查变更数 |
| `tokensThisMonth` | `Long` | 本月 AI Token 消耗总量 |
| `complianceRate` | `Double` | 全局合规率（最近 30 天） |
| `avgReviewTime` | `Double` | 平均审查耗时（秒） |

---

### 18.2 `GET /api/dashboard/dimension-stats` — 维度合规统计

```
@Operation(summary="按审查维度统计合规率（雷达图数据源）")  @Tag("Dashboard")
RespDTO: List<DimensionStatItem>
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | N | — | 项目 ID，不传返回全局 |
| `taskType` | `Integer` | N | `@Min(1) @Max(2)` | 任务类型过滤 |
| `dateFrom` | `String` | N | `@DateTimeFormat` | 时间范围起始 |
| `dateTo` | `String` | N | `@DateTimeFormat` | 时间范围截止 |

**Response Data — `List<DimensionStatItem>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `checkDimension` | `Integer` | 维度编码（11-15 / 21-23） |
| `dimensionName` | `String` | 维度名称 |
| `total` | `Integer` | 审查总次数 |
| `compliant` | `Integer` | 合规次数 |
| `problematic` | `Integer` | 问题次数 |
| `complianceRate` | `Double` | 合规率 0.0~1.0 |
| `avgConfidence` | `Double` | 平均置信度 |

---

### 18.3 `GET /api/dashboard/issue-trend` — 问题趋势

```
@Operation(summary="按日统计问题数量趋势（折线图数据源）")  @Tag("Dashboard")
RespDTO: List<TrendDataPoint>
```

**Query Params**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| `projectId` | `Long` | N | — | 项目 ID，不传返回全局 |
| `days` | `Integer` | N | `@Min(7) @Max(365)` | 统计天数，默认 30 |

**Response Data — `List<TrendDataPoint>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| `date` | `String` | 日期 yyyy-MM-dd |
| `total` | `Integer` | 当日新增问题数 |
| `resolved` | `Integer` | 当日已整改数 |
| `pending` | `Integer` | 当日新增待整改数 |

---

## 模块十九  AI 转发 `/api/ai`

### 19.1 `POST /api/ai/prompt` — AI 对话转发（当前实现）

```
@Tag("AI Chat")
ReqDTO: Map<String,Object>   RespDTO: Map<String,Object>
```

**Request Body — `Map<String,Object>`**

| 字段 | Java 类型 | 必填 | 校验注解 | 说明 |
|------|-----------|------|----------|------|
| （动态） | `Map<String,Object>` | N | — | 原样透传至 Python 服务 `/api/ai/prompt` |

**Response Data — `Map<String,Object>`**

| 字段 | Java 类型 | 说明 |
|------|-----------|------|
| （动态） | `Map<String,Object>` | Python 服务返回结果原样包装在 `R.data` |

---

## 附录 A  文件类型枚举（`doc_type`）

| 编码 | Java 枚举名 | 说明 |
|------|-------------|------|
| `1` | `BID_ANNOUNCEMENT` | 招标公告（关联 doc_bid_announcement 1:1） |
| `2` | `BID_DOCUMENT` | 投标文件 |
| `3` | `EVAL_REPORT` | 评标报告 |
| `4` | `AWARD_NOTICE` | 中标通知书 |
| `5` | `CONTRACT` | 施工合同（关联 doc_contract 1:1） |
| `6` | `PROJECT_APPROVAL` | 项目立项审批文件 |
| `7` | `START_REPORT` | 开工报告 |
| `8` | `CONSTRUCTION_LOG` | 施工日志 |
| `9` | `CHANGE_REQUEST_DOC` | 变更申请文件 |
| `10` | `CHANGE_SCHEME` | 变更方案文件 |
| `11` | `DRAWING` | 施工图纸 |
| `12` | `BOQ` | 工程量清单（Bill of Quantities） |
| `13` | `QUALITY_SPEC` | 质量验收规范 |
| `14` | `SUPERVISION_REPORT` | 监理报告 |
| `15` | `COMPLETION_REPORT` | 竣工验收报告 |
| `99` | `OTHER` | 其他 |

---

## 附录 B  审查维度枚举（`check_dimension`）

| 编码 | Java 枚举名 | 审查要点 |
|------|-------------|----------|
| `11` | `DIM_PUBLIC_PLATFORM` | 检测招标公告是否在政府采购网等合规平台发布（对接 `public_platform` 表） |
| `12` | `DIM_TIME_ORDER` | 确保招标→中标→开工时序合规，不存在"未招先施工" |
| `13` | `DIM_CONSISTENCY` | 对比招标文件与施工合同，识别金额/工期/范围偏差 |
| `14` | `DIM_NOTICE_PERIOD` | 自动计算公示期天数，对照法定最短期限（招标投标法实施条例） |
| `15` | `DIM_RESTRICTIVE` | 识别资质门槛过高、业绩要求歧视性等限制竞争条款 |
| `21` | `DIM_NECESSITY` | 判断变更是否因设计缺陷/不可抗力等必要因素引起（对接 `case_library`） |
| `22` | `DIM_RATIONALITY` | 对比变更方案与施工图纸/质量验收规范，检测行业标准符合性 |
| `23` | `DIM_ECONOMY` | 参照 `market_price` 库评估变更成本合理性，防止虚增造价 |

---

## 附录 C  Spring Boot 项目结构建议

```
com.yourcompany.review
├── config/
│   ├── OpenApiConfig.java          # Swagger/OpenAPI 3 配置（bearerAuth SecurityScheme）
│   ├── SecurityConfig.java         # Spring Security + JWT 过滤器链
│   └── RedisConfig.java            # 看板缓存配置（TTL 300s）
├── common/
│   ├── R.java                      # 统一响应体 R<T>
│   ├── PageResult.java             # 分页结果封装
│   ├── GlobalExceptionHandler.java # @RestControllerAdvice（处理 FK 冲突 / ConstraintViolation）
│   └── enums/
│       ├── DocType.java            # 附录 A 枚举
│       ├── CheckDimension.java     # 附录 B 枚举
│       ├── ReviewVerdict.java
│       └── IssueStatus.java
├── controller/                     # 每模块一个 @RestController
├── service/                        # 业务逻辑层（含 FK 冲突 / 状态机校验）
├── mapper/                         # MyBatis-Plus Mapper 接口
├── entity/                         # 数据库实体（与表 1:1）
├── dto/
│   ├── request/                    # XxxRequest（带 Jakarta Validation 注解）
│   └── response/                   # XxxResponse
├── job/
│   └── ReviewTaskWorker.java       # 异步审查 Worker（@Async 或 RabbitMQ 消费者）
└── llm/
    ├── LLMClient.java              # 大模型调用封装（含重试 @Retryable / 超时）
    └── PromptBuilder.java          # 各维度 Prompt 模板（checkDimension → prompt 映射）
```
