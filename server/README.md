# server 后端服务

`server` 是 PayServer 的后端服务，基于 Spring Boot 构建，提供电商业务 API、JWT 权限控制、支付宝沙箱支付、图片上传和 Spring AI 知识库智能客服能力。

## 技术栈

- Java 21
- Spring Boot 4
- MyBatis
- MySQL
- Redis / Redis Stack
- Spring AI
- JWT
- Alipay SDK
- Lombok

## 功能模块

| 模块 | 说明 |
| --- | --- |
| `auth` | 登录、注册、JWT 签发 |
| `user` | 用户资料、地址管理、管理员用户管理 |
| `product` | 商品、分类、SKU、用户端商品浏览 |
| `cart` | 购物车增删改查 |
| `order` | 下单、订单列表、订单发货、库存锁定/释放 |
| `payment` | 支付宝沙箱支付、支付回调 |
| `admin` | 后台仪表盘统计 |
| `ai` | AI 客服、知识库文档上传、向量检索 |
| `pic` | 本地图片上传与静态资源访问 |

## 目录结构

```text
src/main/java/com/payserver/
├── common/       # 通用响应、异常、配置、工具类
├── module/       # 业务模块
├── security/     # JWT 拦截器、角色注解、用户上下文
└── ServerApplication.java

src/main/resources/
├── application.yml
└── mapper/       # MyBatis XML Mapper

sql/
├── init.sql        # 初始化数据库和基础数据
└── data_expand.sql # 扩展分类与商品数据
```

## 本地启动

### 1. 准备环境

- JDK 21+
- Maven 3.9+
- MySQL 8+
- Redis / Redis Stack

### 2. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
```

如需更多演示商品数据：

```bash
mysql -u root -p payserver_db < sql/data_expand.sql
```

### 3. 配置数据库和 Redis

默认配置位于 `src/main/resources/application.yml`：

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/payserver_db
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 配置 AI 环境变量

知识库上传和 AI 客服依赖 Embedding / Chat 模型，默认使用 OpenAI 兼容接口：

```bash
set DASHSCOPE_API_KEY=your_api_key
```

PowerShell:

```powershell
$env:DASHSCOPE_API_KEY="your_api_key"
```

### 5. 启动服务

```bash
mvn spring-boot:run
```

默认地址：

```text
http://localhost:8081
```

## 常用命令

```bash
# 运行测试
mvn test

# 打包
mvn clean package

# 启动
mvn spring-boot:run
```

## 权限说明

后端使用 JWT 做认证，登录后前端在请求头中携带：

```text
Authorization: Bearer <token>
```

角色分为：

- `USER`：普通用户，可访问购物车、订单、支付、个人资料等接口。
- `ADMIN`：管理员，可访问后台管理接口。

后台接口通过 `@RequireRole("ADMIN")` 做角色保护。

## 关键接口示例

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/v1/auth/login` | 登录 |
| `POST` | `/api/v1/auth/register` | 注册 |
| `GET` | `/api/v1/user/products` | 用户端商品列表 |
| `GET` | `/api/v1/user/products/{id}` | 商品详情 |
| `GET` | `/api/v1/user/categories` | 用户端分类列表 |
| `GET` | `/api/v1/user/cart` | 购物车 |
| `POST` | `/api/v1/user/orders` | 创建订单 |
| `GET` | `/api/v1/admin/products` | 后台商品列表 |
| `GET` | `/api/v1/admin/users` | 后台用户列表 |
| `POST` | `/api/v1/admin/ai/knowledge/upload` | 上传知识库文档 |
| `POST` | `/api/v1/ai/chat` | AI 客服对话 |

## 业务功能

- 使用统一 `Result` 和 `PageResult` 规范 API 返回结构。
- 使用 `GlobalExceptionHandler` 统一处理业务异常和系统异常。
- 通过 `JwtInterceptor` 解析 Token，并结合 `@RequireRole` 做角色鉴权。
- 商品、订单、库存、支付形成完整交易闭环。
- 订单支付前锁定库存，支付成功后确认扣减，超时/取消时释放库存。
- AI 知识库使用文档解析、文本分块、Embedding、Redis Vector Store、相似度检索组成 RAG 流程。
- Mapper 层使用注解 SQL 与 XML SQL 结合，适配简单 CRUD 和复杂库存更新。
