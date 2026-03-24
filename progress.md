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