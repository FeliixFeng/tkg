# 纺织知识图谱前端

纺织行业知识库管理系统前端项目，基于 Vue + Element Plus 构建。

## 项目介绍

面向纺织行业的知识库管理系统，提供知识图谱展示、知识检索、专家评审、知识导入等功能。

## 功能特性

- **知识图谱** - 以树形结构展示纺织行业知识体系
- **知识检索** - 支持按名称分页搜索知识实体
- **专家评审** - 专家用户可审核知识内容
- **知识导入** - 普通用户可导入新知识
- **用户管理** - 用户登录注册、权限管理

## 技术栈

- Vue 3
- Vuex（状态管理）
- Vue Router（路由管理）
- Element Plus（UI组件库）
- Axios（HTTP请求）

## 目录结构

```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   │   ├── LoginRegister.vue      # 登录注册组件
│   │   ├── KnowledgeGraph.vue     # 知识图谱组件
│   │   ├── KnowledgeSearch.vue    # 知识检索组件
│   │   ├── KnowledgeImport.vue    # 知识导入组件
│   │   ├── ExpertReview.vue       # 专家评审组件
│   │   ├── MyKnowledge.vue        # 我的知识组件
│   │   ├── UserInfoModal.vue      # 用户信息弹窗
│   │   └── Main.vue               # 主页面布局
│   ├── http.js           # Axios配置
│   ├── store/            # Vuex状态管理
│   ├── router/           # 路由配置
│   ├── main.js           # 入口文件
│   └── App.vue           # 根组件
├── public/               # 公共资源
├── package.json
└── vue.config.js         # Vue配置
```

## API接口

前端调用后端接口地址：`http://localhost:8080/api`

| 接口 | 方法 | 说明 |
|------|------|------|
| /user/login | POST | 用户登录 |
| /user/register | POST | 用户注册 |
| /entity/tree | GET | 获取知识图谱树 |
| /entity/{id} | GET | 获取实体详情 |
| /entity/pages | GET | 分页查询实体 |
| /user/get_name/{id} | GET | 获取用户名 |

## 快速开始

### 安装依赖
```
npm install
```

### 开发模式
```
npm run serve
```
访问地址：http://localhost:8081

### 生产构建
```
npm run build
```

## 用户类型

- **普通用户 (userType=0)** - 可查看知识图谱、检索知识、导入知识
- **专家用户 (userType=1)** - 额外拥有专家评审权限

## 配置说明

修改 `src/http.js` 中的 `baseURL` 即可切换后端地址：

```javascript
const instance = axios.create({
  baseURL: 'http://localhost:8080', // 本地后端
  // baseURL: 'http://your-server:8080/api', // 生产环境
});
```

## 相关链接

- 后端项目：[TKG Backend](https://github.com/your-repo/tkg)
- 在线文档：http://localhost:8080/doc.html
