# TKG-Backend

## 项目简介

面向纺织产业的多模态知识图谱平台后端服务，提供完整的用户管理、知识图谱构建、实体审核、文件上传等功能。

### 核心功能

✅ **用户管理系统**
- 用户注册/登录（JWT 认证）
- 密码加密存储（SHA-256 + 盐值）
- 用户信息管理
- 密码修改

✅ **知识图谱管理**
- 实体增删改查
- 树形结构展示（性能优化，支持深层嵌套）
- 分页查询
- 多条件筛选

✅ **审核流程**
- 实体审核通过/拒绝
- 审核状态管理
- 用户提交追踪

✅ **文件上传**
- 阿里云 OSS 集成
- 文件类型校验
- 大小限制（10MB）

---

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.3.4 | 核心框架 |
| MyBatis | 3.0.3 | ORM 框架 |
| MySQL | 8.0.30 | 数据库 |
| JWT | 0.11.5 | 认证方案 |
| Knife4j | 4.4.0 | API 文档 |
| Lombok | - | 代码简化 |
| Validation | - | 参数校验 |

### 安全特性

- 🔐 密码加密存储（SHA-256 + 随机盐值）
- 🔑 JWT Token 认证
- 🛡️ 敏感信息脱敏
- ✅ 参数自动验证
- 🚨 全局异常处理
- 🌐 CORS 跨域支持

### 性能优化

- ⚡ N+1 查询优化（树形结构查询提升 10-100 倍）
- 🔄 OSS 客户端单例模式
- 💾 数据库连接池
- 📦 事务管理

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/FeliixFeng/tkg.git
   cd tkg
   ```

2. **配置数据库**
   
   修改 `src/main/resources/application-local.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://your-host:3306/tkg
       username: your-username
       password: your-password
   ```

3. **配置环境变量**（可选）
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/tkg
   export DB_USER=root
   export DB_PWD=your_password
   export JWT_SECRET=your-secret-key-at-least-32-chars
   export OSS_AK=your-oss-access-key
   export OSS_SK=your-oss-secret-key
   ```

4. **启动项目**
   ```bash
   mvn spring-boot:run
   ```

5. **访问 API 文档**
   
   打开浏览器访问：`http://localhost:8080/doc.html`

---

## API 接口

### 用户模块（6个接口）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 用户注册 |
| POST | `/api/user/login` | 用户登录 |
| GET | `/api/user/get_name/{id}` | 查询用户名 |
| GET | `/api/user/get_user/{username}` | 查询用户信息 |
| PUT | `/api/user/update` | 修改用户信息 |
| PUT | `/api/user/modify_password` | 修改密码 |

### 实体模块（11个接口）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/entity/tree` | 获取树形结构 |
| GET | `/api/entity/{id}` | ID查询实体 |
| GET | `/api/entity/get_id/{name}` | 名称查询ID |
| GET | `/api/entity/all/{name}` | 名称查询实体 |
| GET | `/api/entity/pages` | 分页查询 |
| GET | `/api/entity/get_info/{userId}` | 用户ID查询 |
| POST | `/api/entity/add` | 添加实体 |
| PUT | `/api/entity` | 修改实体 |
| DELETE | `/api/entity/{name}` | 删除实体 |
| PUT | `/api/entity/check_ok/{id}` | 审核通过 |
| PUT | `/api/entity/check_no` | 审核拒绝 |

### 文件模块（1个接口）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/upload` | 文件上传 |

---

## 项目结构

```
tkg/
├── src/main/java/com/wtu/
│   ├── config/              # 配置类
│   │   ├── OssMvcConfiguration.java
│   │   ├── WebConfig.java
│   │   └── StartupRunner.java
│   ├── controller/          # 控制器
│   │   ├── UserController.java
│   │   ├── EntityController.java
│   │   └── CommonController.java
│   ├── service/             # 服务层
│   │   ├── IUserService.java
│   │   ├── IEntityService.java
│   │   └── impl/
│   ├── mapper/              # 数据访问层
│   │   ├── UserMapper.java
│   │   └── EntityMapper.java
│   ├── entity/              # 实体类
│   │   ├── User.java
│   │   ├── Entity.java
│   │   └── Node.java
│   ├── DTO/                 # 数据传输对象
│   │   ├── UserLoginDTO.java
│   │   ├── UserRegisterDTO.java
│   │   ├── PageQueryDTO.java
│   │   └── ...
│   ├── vo/                  # 视图对象
│   │   └── UserLoginVO.java
│   ├── utils/               # 工具类
│   │   ├── JwtUtils.java
│   │   ├── AliOssUtil.java
│   │   └── PasswordEncoder.java
│   ├── constant/            # 常量类
│   │   ├── StatusConstant.java
│   │   └── MessageConstant.java
│   ├── exception/           # 异常处理
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   └── interceptor/         # 拦截器
│       └── JwtAuthenticationInterceptor.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-local.yml
│   └── mapper/              # MyBatis XML
│       ├── UserMapper.xml
│       └── EntityMapper.xml
└── frontend/                # 前端项目
    └── ...
```

---

## 使用示例

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "userType": 0,
    "phone": "13800138000"
  }'
```

### 2. 用户登录

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456"
  }'
```

### 3. 查询知识图谱树

```bash
curl -X GET http://localhost:8080/api/entity/tree \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 数据库设计

### 核心表

**user 表** - 用户信息
- id, username, password（加密）, phone, user_type, avatar

**entity 表** - 知识实体
- id, name, parent_id, description, status, image_url, user_id, created_at, updated_at

---

## 配置说明

### application.yml

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/tkg}
    username: ${DB_USER:root}
    password: ${DB_PWD:password}

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 3600000  # 1小时

wtu:
  alioss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: ${OSS_AK:your-key}
    access-key-secret: ${OSS_SK:your-secret}
    bucket-name: tkg
```

---

## 测试

### 运行测试

```bash
mvn test
```

### API 测试

访问 Knife4j 文档进行在线测试：
```
http://localhost:8080/doc.html
```

---

## 部署

### Docker 部署（推荐）

```bash
# 构建镜像
docker build -t tkg-backend .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_URL=jdbc:mysql://your-db:3306/tkg \
  -e DB_USER=root \
  -e DB_PWD=password \
  tkg-backend
```

### 传统部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/tkg-backend-0.0.1-SNAPSHOT.jar
```

---

## 性能指标

| 指标 | 数值 |
|------|------|
| 接口数量 | 18 个 |
| 树形查询性能 | < 150ms（35个节点） |
| 平均响应时间 | < 100ms |
| 并发支持 | 100+ |

---

## 开发团队

- **后端开发**: [@FeliixFeng](https://github.com/FeliixFeng)

---

## 更新日志

### v0.0.1-SNAPSHOT (2026-01-18)

✨ **新功能**
- 完整的用户认证系统
- 知识图谱 CRUD 操作
- 树形结构展示
- 实体审核流程
- 文件上传功能

🔐 **安全增强**
- 密码加密存储
- JWT 认证
- 敏感信息脱敏
- 参数自动验证

⚡ **性能优化**
- N+1 查询优化
- OSS 单例模式
- 数据库连接池

---

## 常见问题

**Q: 如何修改端口？**

A: 在 `application.yml` 中添加：
```yaml
server:
  port: 你的端口号
```

**Q: 忘记密码怎么办？**

A: 联系管理员重置，或直接修改数据库中的密码字段（需使用加密后的值）

**Q: API 文档在哪里？**

A: 启动后访问 `http://localhost:8080/doc.html`

---

## 贡献指南

1. Fork 本仓库
2. 创建 Feature 分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

本项目采用 [MIT 许可证](LICENSE)

---

## 联系方式

- 项目主页: https://github.com/FeliixFeng/tkg
- Issues: https://github.com/FeliixFeng/tkg/issues

---

**⭐ 如果这个项目对你有帮助，请给一个 Star！**
