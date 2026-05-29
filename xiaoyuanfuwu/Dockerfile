# =============================================
# 校园服务系统 - Docker 构建文件
# 采用多阶段构建，前端静态资源打包进后端
# =============================================

# -------------------- 阶段1: 构建前端 --------------------
FROM node:20-alpine AS frontend-builder

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build

# -------------------- 阶段2: 构建后端 --------------------
FROM eclipse-temurin:17-jdk-alpine AS backend-builder

WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/src ./src
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# -------------------- 阶段3: 运行环境 --------------------
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="campus-service"
LABEL description="校园服务系统"

WORKDIR /app

# 安装必要工具
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制后端 JAR
COPY --from=backend-builder /app/backend/target/campus-service-1.0.0.jar app.jar

# 复制前端静态资源到 Spring Boot 的 static 目录
COPY --from=frontend-builder /app/frontend/dist /app/static

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
