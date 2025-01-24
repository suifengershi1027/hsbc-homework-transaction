# 基于 OpenJDK 21 基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制项目的 pom.xml 文件到工作目录
COPY pom.xml .

# 下载项目依赖（这一步可以利用 Docker 缓存，加快构建速度）
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

# 复制项目源代码到工作目录
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests

# 暴露项目运行的端口，根据实际情况修改
EXPOSE 8080

# 定义容器启动时执行的命令
CMD ["java", "-jar", "target/hsbc-homework-transaction-0.0.1-SNAPSHOT.jar"]