# PayServer 电商支付与智能客服系统

PayServer 是一个全栈电商项目，包含用户端商城、管理员后台和 Spring Boot 后端服务。项目围绕真实电商业务链路设计，覆盖商品浏览、分类管理、购物车、订单、支付宝沙箱支付、后台运营、用户管理、数据看板，以及基于 Spring AI 的知识库智能客服。

## 项目亮点

- 前后端分离架构：用户端、管理员端、后端服务独立工程，便于开发、部署和职责拆分。
- 完整电商主链路：商品分类、SKU、购物车、订单、库存锁定、支付回调、订单发货。
- 管理后台能力：仪表盘统计、分类管理、商品管理、订单管理、用户管理、知识库管理。
- AI 客服能力：支持上传知识库文档，使用 Redis Vector Store 做向量检索，结合大模型生成客服回复。
- 权限体系：JWT 登录认证，区分 `USER` / `ADMIN` 角色，后台接口通过角色注解保护。
- 工程化实践：统一响应结构、全局异常处理、MyBatis Mapper 分层、前端路由守卫、Pinia 状态管理。

## 工程结构

```text
payServer/
├── client-user/      # 用户端商城，面向普通消费者
├── client-admin/     # 管理员后台，面向运营/管理员
├── server/           # Spring Boot 后端服务
├── picserver/        # 本地图片上传目录
└── README.md         # 项目总览文档
```

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 用户端 | Vue 3, Vue Router, Pinia, Axios, Vite |
| 管理端 | Vue 3, Element Plus, ECharts, Vue Router, Pinia, Axios, Vite |
| 后端 | Java 21, Spring Boot 4, MyBatis, MySQL, Redis, JWT |
| AI 能力 | Spring AI, OpenAI 兼容接口, Redis Vector Store, Tika / Markdown 文档解析 |
| 支付 | 支付宝沙箱 SDK |

## 核心功能

### 用户端

- 首页广告轮播、商品分类树、商品搜索、商品列表分页
- 商品详情、SKU 选择、库存展示
- 登录注册、JWT 会话保持
- 购物车增删改查、结算选择
- 订单创建、订单列表、支付宝沙箱支付
- AI 智能客服入口

### 管理端

- 登录鉴权，仅管理员可进入后台
- 数据看板：用户、商品、订单、销售数据统计
- 分类管理：新增、编辑、删除商品分类
- 商品管理：商品 CRUD、图片上传、默认 SKU 创建
- 订单管理：订单筛选、发货操作
- 用户管理：用户列表、筛选、新增、编辑、禁用、删除
- 知识库管理：上传文档、向量化状态查看、删除文档

### 后端

- 统一 REST API 与 `Result` 响应封装
- JWT 拦截器与角色权限注解
- 商品、分类、SKU、购物车、订单、支付、用户、AI 模块
- 订单库存锁定、释放与确认扣减
- 支付宝沙箱支付与异步回调处理
- 文档解析、分块、向量入库、RAG 检索增强客服回复

## 本地运行

### 1. 准备环境

- JDK 21+
- Maven 3.9+
- Node.js 20+
- MySQL 8+
- Redis / Redis Stack

### 2. 初始化数据库

```bash
mysql -u root -p < server/sql/init.sql
```

如需导入扩展商品数据：

```bash
mysql -u root -p payserver_db < server/sql/data_expand.sql
```

### 3. 配置环境变量

AI 和支付宝相关配置可按需启用。至少需要在使用知识库/AI 客服前配置：

```bash
set DASHSCOPE_API_KEY=your_api_key
```

Windows PowerShell:

```powershell
$env:DASHSCOPE_API_KEY="your_api_key"
```

### 4. 启动后端

```bash
cd server
mvn spring-boot:run
```

默认端口：`http://localhost:8081`

### 5. 启动用户端

```bash
cd client-user
npm install
npm run dev
```

默认地址：`http://localhost:5173`

### 6. 启动管理端

```bash
cd client-admin
npm install
npm run dev
```

默认地址：`http://localhost:5174`

## 默认账号

数据库初始化脚本内置以下账号，默认密码均为 `123456`：

| 角色 | 用户名 |
| --- | --- |
| 管理员 | `admin` |
| 普通用户 | `user01` |
| 普通用户 | `user02` |


## 子工程文档

- [用户端 README](client-user/README.md)
- [管理端 README](client-admin/README.md)
- [后端 README](server/README.md)
