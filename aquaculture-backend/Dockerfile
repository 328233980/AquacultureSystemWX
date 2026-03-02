# ============================================
# 水产养殖管理系统 - Docker 构建文件
# 适用于腾讯云容器服务部署
# 数据库：MySQL 8.0+
# ============================================

# ==================== 构建阶段 ====================
FROM maven:3.8.6-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件（使用腾讯云镜像加速）
COPY settings.xml /root/.m2/settings.xml

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==================== 运行阶段 ====================
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建非 root 用户运行应用
RUN groupadd -r aquaculture && useradd -r -g aquaculture aquaculture

# 从构建阶段复制 jar 文件
COPY --from=builder /build/target/aquaculture-backend-1.0.0.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads && chown -R aquaculture:aquaculture /app

# 切换到非 root 用户
USER aquaculture

# 暴露端口
EXPOSE 8080

# 设置时区为中国标准时间
ENV TZ=Asia/Shanghai

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 数据库配置（通过环境变量传入）
# DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
