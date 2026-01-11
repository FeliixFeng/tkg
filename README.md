# tkg-backend

## 项目介绍
面向纺织产业的多模态知识图谱平台后端，提供用户管理、知识导入、搜索、展示及专家评审等功能。

## 软件架构
- **后端技术栈**：
  - Java 11
  - Spring Boot
  - MyBatis
  - Maven
- **日志管理**：
  - SLF4J + Logback

## 安装教程

1. 克隆项目：
   ```bash
   git clone https://github.com/FeliixFeng/tkg.git
   ```
2. 进入项目目录：
   ```bash
   cd tkg-backend
   ```
3. 启动后端服务：
   ```bash
   mvn spring-boot:run
   ```

## 使用说明

1. 配置数据库连接：
   - 修改 `application.yml` 文件中的数据库配置。
2. 启动服务后，访问 API 文档：
   - 默认地址为 `http://localhost:8080/swagger-ui.html`。
3. 测试功能：
   - 使用 Postman 或其他工具测试用户管理、知识图谱相关接口。

## 参与贡献

1. Fork 本仓库
2. 新建 Feature 分支
3. 提交代码
4. 新建 Pull Request

## 许可证
本项目采用 [MIT 许可证](LICENSE)。
