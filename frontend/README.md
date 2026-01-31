# 纺织知识图谱前端

纺织行业知识库管理系统前端项目，基于 Vue 3 + Element Plus 构建。

## 项目介绍

面向纺织行业的知识库管理系统，提供知识图谱可视化、知识检索、专家评审、知识导入等功能。

## 功能特性

- **知识图谱** - D3.js 力导向图展示纺织行业知识体系
- **知识检索** - 支持按名称分页搜索知识实体
- **专家评审** - 专家用户可审核知识内容
- **知识导入** - 普通用户可导入新知识
- **用户管理** - JWT认证、登录注册、权限管理

## 技术栈

- Vue 3 (Composition API)
- Vuex 4（状态管理）
- Vue Router 4（路由管理）
- Element Plus（UI组件库）
- Axios（HTTP请求）
- D3.js（知识图谱可视化）

## 项目结构

```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   │   ├── KnowledgeGraph.jpg       # 背景图
│   │   └── defaultavatar.jpg        # 默认头像
│   ├── components/      # 业务组件
│   │   ├── KnowledgeGraph.vue       # 知识图谱可视化
│   │   ├── KnowledgeSearch.vue      # 知识检索
│   │   ├── KnowledgeImport.vue      # 知识导入
│   │   ├── ExpertReview.vue         # 专家评审
│   │   ├── MyKnowledge.vue          # 我的知识
│   │   └── UserInfoModal.vue        # 用户信息弹窗
│   ├── views/           # 页面视图
│   │   ├── Login.vue                # 登录/注册页
│   │   └── Dashboard.vue            # 主控制台
│   ├── store/           # Vuex状态管理
│   │   └── modules/
│   │       └── auth.js              # 认证模块
│   ├── router/          # 路由配置
│   │   └── index.js                 # 路由守卫
│   ├── http.js          # Axios配置（含拦截器）
│   ├── main.js          # 入口文件
│   └── App.vue          # 根组件
├── public/              # 公共资源
├── package.json
└── vue.config.js        # Vue配置
```

## 认证流程

- 基于 JWT Token 认证
- 登录/注册成功后后端返回 Token
- Token 存储在 sessionStorage
- Axios 请求拦截器自动附加 Token
- 路由守卫检查认证状态

## 用户权限

| 用户类型 | 权限 |
|---------|------|
| 普通用户 (userType=0) | 知识图谱、知识检索、知识导入、我的知识 |
| 专家用户 (userType=1) | 上述所有 + 专家评审 |

## 快速开始

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run serve
```
访问地址：http://localhost:1024

### 生产构建
```bash
npm run build
```

## API接口

后端接口地址：`http://localhost:8080`

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/login | POST | 用户登录 |
| /api/user/register | POST | 用户注册 |
| /api/user/get_user/{username} | GET | 查询用户 |
| /api/entity/tree | GET | 获取知识图谱树 |
| /api/entity/{id} | GET | 获取实体详情 |
| /api/entity/pages | GET | 分页查询实体 |

## 配置说明

### 后端地址配置
修改 `src/http.js` 中的 `baseURL`：

```javascript
const instance = axios.create({
  baseURL: 'http://localhost:8080', // 本地后端
  timeout: 10000,
});
```

### 环境变量
开发环境使用 `.env.development`，生产环境使用 `.env.production`

## 开发指南

### 添加新页面
1. 在 `src/views/` 创建页面组件
2. 在 `src/router/index.js` 添加路由
3. 在 `Dashboard.vue` 添加菜单项

### 状态管理
使用 Vuex `auth` 模块管理登录状态：
```javascript
// 登录
this.$store.dispatch('auth/login', credentials)

// 获取当前用户
this.$store.state.auth.user

// 退出
this.$store.dispatch('auth/logout')
```

## 相关链接

- 后端项目：[TKG Backend](https://github.com/FeliixFeng/tkg)
- API文档：http://localhost:8080/doc.html

## 技术亮点

- JWT认证 + 路由守卫
- D3.js 力导向图可视化
- Element Plus 深色主题适配
- 响应式布局设计
