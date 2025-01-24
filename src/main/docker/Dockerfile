# 基于 OpenJDK 21 基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

COPY hsbc-homework-transaction-0.0.1-SNAPSHOT.jar /app/hsbc-homework-transaction-0.0.1.jar

# 暴露项目运行的端口，根据实际情况修改
EXPOSE 8080

# 定义容器启动时执行的命令
CMD ["java", "-jar", "hsbc-homework-transaction-0.0.1.jar"]