# 使用官方的 Java 21 JDK 镜像作为基础镜像
FROM eclipse-temurin:21-jdk

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中
COPY target/TransactionManagement-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露默认的 Spring Boot 应用端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]