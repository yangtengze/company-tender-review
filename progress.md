# 已实现内容
## 全局约定
    R<T> 统一响应体：code/message/data/total/page/size
    通用分页参数对象 PageQuery：page/size/orderBy/order，含注解校验
    全局异常处理 GlobalExceptionHandler：统一返回业务错误码（如参数校验失败、请求体格式错误等）
    OpenAPI/Swagger 配置 OpenApiConfig（包含 bearerAuth）
## 模块一 /api/auth
    POST /api/auth/login
    POST /api/auth/refresh
    GET /api/auth/me
    POST /api/auth/logout
    DTO 已按文档字段定义：
        LoginRequest, RefreshRequest
        LoginResponse, UserDetailResponse
    AuthService + AuthServiceImpl：当前使用内存用户和 token 存储（便于先联调和测试，后续可替换为 DB+JWT）
## 测试

    AuthControllerTest（MockMvc）覆盖：
        登录 + 获取当前用户
        refresh 获取新 token
        登录参数校验失败
        logout 后 token 失效

## 新增主要文件
    pom.xml
    src/main/java/com/finaldesign/audit/AuditBackendApplication.java
    src/main/java/com/finaldesign/audit/common/R.java
    src/main/java/com/finaldesign/audit/common/PageQuery.java
    src/main/java/com/finaldesign/audit/common/GlobalExceptionHandler.java
    src/main/java/com/finaldesign/audit/config/OpenApiConfig.java
    src/main/java/com/finaldesign/audit/controller/AuthController.java
    src/main/java/com/finaldesign/audit/service/AuthService.java
    src/main/java/com/finaldesign/audit/service/impl/AuthServiceImpl.java
    src/main/java/com/finaldesign/audit/dto/request/*
    src/main/java/com/finaldesign/audit/dto/response/*
    src/test/java/com/finaldesign/audit/AuthControllerTest.java
## 测试执行情况
    我已尝试执行 mvn test，但你当前终端环境提示找不到 mvn（PATH 未识别 Maven 命令）。
    代码静态诊断已检查：无 linter 错误。



这次改了什么
    Auth 改为真正 JWT

        新增 JwtTokenService，生成/校验签名 JWT（access=7200s、refresh=604800s）。
        accessToken / refreshToken 都是标准 JWT（三段式），并带 token type 区分（access / refresh）。
        refresh 接口会校验 refresh token 类型和有效期，再签发一对新 token。
    对接 sys_user / sys_operation_log（MySQL 语义）

        新增 UserRepository（JDBC）从 sys_user + sys_org 查用户。
        新增 OperationLogRepository，logout 时写入 sys_operation_log。
        AuthServiceImpl 已改为数据库查询用户，不再用内存用户表。
        登录后会更新 sys_user.last_login_at。
        密码使用 password_hash 字段校验（当前实现为 SHA-256 比对）。
    补了一版基于数据库的集成测试

        增加测试库配置：src/test/resources/application.properties。
        增加测试初始化 SQL：src/test/resources/schema.sql、src/test/resources/data.sql。
        AuthControllerTest 改为验证：
        登录成功并返回 JWT 形态 token；
        refresh 能返回新 JWT；
        logout 后会落 sys_operation_log（符合“JWT 无状态”的规范）。
    主要文件变更
        pom.xml
        src/main/resources/application.properties
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/AuditBackendApplication.java
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/service/impl/AuthServiceImpl.java
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/config/JwtProperties.java（新增）
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/service/security/JwtTokenService.java（新增）
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/service/security/PasswordCodec.java（新增）
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/repository/UserRepository.java（新增）
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/repository/OperationLogRepository.java（新增）
        src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/repository/model/UserAuthView.java（新增）
        src/test/java/cn/edu/sdua/_db/ytz/company_tender_review/AuthControllerTest.java
        src/test/resources/application.properties（新增）
        src/test/resources/schema.sql（新增）
        src/test/resources/data.sql（新增）

已实现：模块二 机构管理 /api/orgs
    GET /api/orgs：支持 type/status/tree 查询
        默认：status=1、tree=false（平铺列表）
        tree=true：返回树形结构（含 children）
    POST /api/orgs：创建机构（会校验 parentId 存在）
    PUT /api/orgs/{id}：更新机构信息（支持更新 name/code/address/status）
对应新增/更新文件：

    新增 DTO
        src/main/java/.../dto/request/OrgQueryRequest.java
        src/main/java/.../dto/request/OrgCreateRequest.java
        src/main/java/.../dto/request/OrgUpdateRequest.java
        src/main/java/.../dto/response/OrgNode.java
    新增 Controller
        src/main/java/.../controller/OrgController.java
    新增 Repository（JDBC）
        src/main/java/.../repository/OrgRepository.java
        src/main/java/.../repository/model/OrgRow.java
    测试库 schema/data 对齐 sys_org 字段
        src/test/resources/schema.sql（补齐 parent_id/status/created_at/updated_at 等）
        src/test/resources/data.sql（初始化 1001 根节点 + 2001 子节点）

已实现：模块三 用户管理 /api/users
    GET /api/users：用户分页列表（支持 orgId/role/status/keyword/page/size）
        返回用现有 R.okPage(data,total,page,size) 结构承载 Page<UserListItem>。
    POST /api/users：创建用户（密码 BCrypt 入库）
    PUT /api/users/{id}：更新用户信息（含 role/status/avatarUrl）
    PATCH /api/users/{id}/pwd：修改密码（超管可不传 oldPassword 强制重置他人；自己改密必须传 oldPassword 且校验）
    超管权限：以上接口都要求 Authorization: Bearer {accessToken}，并且当前登录用户 role=1，否则返回 code=4004（沿用你现有的 GlobalExceptionHandler 映射）。
关键新增/修改文件
    src/main/java/.../controller/UserController.java
    src/main/java/.../dto/request/UserQueryRequest.java
    src/main/java/.../dto/request/UserCreateRequest.java
    src/main/java/.../dto/request/UserUpdateRequest.java
    src/main/java/.../dto/request/ChangePasswordRequest.java
    src/main/java/.../dto/response/UserListItem.java
    src/main/java/.../repository/model/UserListRow.java
    src/main/java/.../repository/UserRepository.java（扩展分页/创建/更新/改密所需方法）
    src/main/java/.../service/security/PasswordCodec.java（新增 BCrypt + 兼容旧 SHA-256）
    src/test/java/.../UserControllerTest.java
    src/test/resources/schema.sql（严格同写法）
pom.xml（加 spring-security-crypto + Testcontainers 依赖）

我做的修改
    src/main/java/.../service/security/PasswordCodec.java

        移除 SHA-256 相关实现
        matches 现在只接受 BCrypt 格式（$2a$/$2b$/$2y$），否则直接报错：
            "password hash format invalid, bcrypt required"
    src/test/resources/data.sql

        把 admin 的 password_hash 从 SHA-256 改为 BCrypt 哈希（对应密码仍是 123456）：
        '$2b$12$8ZrYQOMn/l5/q8gNMchhEuGFolGmF0H0WNEb8fYMwj.bT5l.ViVYq'
现在的状态
    登录校验：AuthServiceImpl -> passwordCodec.matches(...)，已变为 BCrypt-only
    创建用户 / 改密码：UserController 已经是 passwordCodec.bcrypt(...) 入库，保持一致
    测试初始用户：admin 已是 BCrypt 哈希，和登录流程一致


已实现接口（模块四）
    GET /api/projects 项目分页列表
    POST /api/projects 创建项目
    PATCH /api/projects/{id}/status 项目状态流转（状态机）
    GET /api/projects/{id}/stats 项目统计
主要新增文件
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/controller/ProjectController.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/repository/ProjectRepository.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/request/ProjectQueryRequest.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/request/ProjectCreateRequest.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/request/ProjectStatusRequest.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/response/ProjectListItem.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/response/ProjectDetailResponse.java
    src/main/java/cn/edu/sdua/_db/ytz/company_tender_review/dto/response/ProjectStatsResponse.java
    src/test/java/cn/edu/sdua/_db/ytz/company_tender_review/ProjectControllerTest.java

模块五接口实现
    POST /api/documents/upload
        multipart/form-data
        需要 Authorization: Bearer {accessToken}
        入库 document，并返回 DocumentDetailResponse
    GET /api/documents
        分页列表（DocumentQueryRequest：projectId 必填，其余可选）
        返回 Page<DocumentDetailResponse>（R.okPage）
    GET /api/documents/{id}/extract
        若 doc_extract_cache 命中：直接返回
        未命中：写入缓存并返回 ExtractResultResponse
我新增/修改的关键代码文件
新增（模块五 DTO/Controller/Repository/Service）：
    src/main/java/.../controller/DocumentController.java
    src/main/java/.../dto/request/DocumentUploadRequest.java
    src/main/java/.../dto/request/DocumentQueryRequest.java
    src/main/java/.../dto/response/DocumentDetailResponse.java
    src/main/java/.../dto/response/ExtractResultResponse.java
    src/main/java/.../repository/DocumentRepository.java
    src/main/java/.../repository/DocumentExtractCacheRepository.java
    src/main/java/.../service/DocumentExtractService.java

新增接口：POST /api/bid-announcements
    校验：
        docId 对应的 document.doc_type 必须为 1（招标公告）
        document.project_id 必须与请求 projectId 一致
    写入：
        插入到 doc_bid_announcement
        publicNoticeDays 按 DATEDIFF(deadline_date, publish_date) 语义计算（按日期部分差值）
    返回：
        bidTypeName
        publicNoticeDays
        updatedAt（v2 字段，来自 updated_at）
2) 新增代码文件
    src/main/java/.../controller/BidAnnouncementController.java
    src/main/java/.../dto/request/BidAnnouncementCreateRequest.java
    src/main/java/.../dto/response/BidAnnouncementResponse.java
    src/main/java/.../repository/BidAnnouncementRepository.java
    src/main/java/.../repository/model/BidAnnouncementRow.java
    （并给模块五的 DocumentRepository 增加了校验用的元信息方法）
    src/main/java/.../repository/DocumentRepository.java（新增 findDocMetaById）