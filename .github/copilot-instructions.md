<!-- Copilot instructions for agents working on this repo -->
# 项目速览与代理指引

目的：为 AI 编程代理提供可立刻上手的项目背景、关键文件、运行/调试命令和项目特有约定。

- **大体架构**: 后端为 Spring Boot (Java 17, Maven)；前端为微信小程序（目录 `aquaculture-miniapp/`）。
  - 后端代码：`aquaculture-backend/src/main/java/`（包名 `com.aquaculture`）。
  - 后端配置：`aquaculture-backend/src/main/resources/application.yml`（端口、数据源、JWT、test-mode）。
  - 小程序：`aquaculture-miniapp/`（入口 `app.js`，通用请求封装 `utils/request.js`）。

- **数据与持久层**:
  - 使用 SQLite，本地 DB 路径由 `application.yml` 指定：`jdbc:sqlite:data/aquaculture.db`（相对后端工作目录）。
  - MyBatis mapper XML 放在 `src/main/resources/mapper/`（`mybatis.mapper-locations` 配置），实体在 `com.aquaculture.entity`。
  - 初始 schema 在 `target/classes/db/schema.sql`（可用于重建 DB）。

- **鉴权与测试模式**:
  - 后端使用 JWT（`application.yml` 下 `jwt.secret`、`jwt.expiration`）。
  - 小程序请求头使用 `Authorization: Bearer <token>`，实现见 `aquaculture-miniapp/utils/request.js`。
  - 有测试模式与默认 token（`test.default-token` / `888888`），在 `application.yml` 中。

- **重要依赖 / 插件**:
  - MyBatis (`mybatis-spring-boot-starter`), SQLite JDBC, Hutool, Lombok。
  - Maven 编译器插件在 `pom.xml` 指定了 `executable` 为 `D:\java\jdk-17\bin\javac.exe` —— 在不同开发机上可能需要移除或改为使用 `JAVA_HOME`。

- **常用开发命令（可直接运行）**:
  - 启动后端（开发）：
    ```bash
    cd aquaculture-backend
    mvn spring-boot:run
    ```
  - 打包并运行 jar：
    ```bash
    cd aquaculture-backend
    mvn -DskipTests package
    java -jar target/aquaculture-backend-1.0.0.jar
    ```
  - 小程序本地开发：使用微信开发者工具打开 `aquaculture-miniapp` 目录；前端默认调用 `http://localhost:8088`（见 `app.js` 的 `baseUrl`）。

- **项目约定与行为模式（从代码可观察到的）**:
  - 接口约定：后端响应 JSON 结构包含 `code` 与 `message`，前端 `utils/request.js` 会检查 `data.code === 200` 视为成功。
  - 文件上传：小程序使用 `wx.uploadFile`，后端 `file.upload-path` 指定为 `static/uploads/`。
  - 日志级别：`application.yml` 将 `com.aquaculture` 设为 `DEBUG`，因此改动日志级别会影响大量输出。

- **常见维护/修改位置（给代理的具体指令）**:
  - 修改 API 地址或本地运行端口：`aquaculture-miniapp/app.js`（`baseUrl`）和 `aquaculture-backend/src/main/resources/application.yml`（`server.port`）。
  - 添加/修改数据库表结构：编辑 `data/` 下的 SQLite 文件或更新 `target/classes/db/schema.sql`，并确保 `application.yml` 的 `url` 指向正确文件。
  - 添加 MyBatis 映射：在 `src/main/resources/mapper/` 添加 `*.xml` 并在 `src/main/java/com/.../mapper` 添加接口。
  - 调试认证问题：检查 `utils/request.js` 的 `Authorization` header 与后端 JWT 配置（secret、expiration）。

- **注意事项 / 限制（可避免常见错误）**:
  - `pom.xml` 中硬编码的 `javac` 路径会导致在不同机器上构建失败；若出现编译异常，先检查或移除该 `executable` 配置。
  - SQLite 的路径是相对的——在容器化或服务部署时请确保工作目录正确或使用绝对路径。
  - 小程序的 token 流程：开发模式下 token 默认存在（`888888`）；真实集成时需替换为真实登录逻辑。

- 如果你希望我合并已有文档（若存在）或把本文件转换为英文版，请告诉我需要的变更。请审阅并指出哪些部分需要补充或更详细的示例。
