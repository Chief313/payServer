# client-admin 管理员后台

`client-admin` 是 PayServer 的后台管理系统，面向管理员和运营人员，提供数据看板、商品管理、订单管理、用户管理和 AI 知识库管理能力。

## 技术栈

- Vue 3
- Element Plus
- ECharts
- Vue Router
- Pinia
- Axios
- Vite

## 功能模块

- 登录鉴权：仅 `ADMIN` 角色可进入后台
- 首页概览：展示用户数、商品数、订单数、销售额等统计数据
- 分类管理：新增、编辑、删除商品分类，支持父子分类
- 商品管理：商品分页、分类筛选、新增、编辑、删除、封面上传、默认 SKU
- 订单管理：按订单状态筛选，支持已支付订单发货
- 用户管理：用户列表、关键词搜索、角色/状态筛选、新增、编辑、禁用、删除
- 知识库管理：上传 txt/doc/docx/pdf/md 文档，查看向量化状态，删除文档

## 目录结构

```text
src/
├── api/          # 后台接口封装
├── components/   # 通用组件
├── layout/       # 后台主布局
├── router/       # 路由配置与登录守卫
├── stores/       # Pinia 状态，如管理员登录信息
└── views/        # 后台页面
```

## 本地启动

后端服务默认运行在 `http://localhost:8081`，Vite 代理已配置到该地址。

```bash
npm install
npm run dev
```

默认访问地址：

```text
http://localhost:5174
```

## 构建

```bash
npm run build
```

## 默认管理员账号

数据库初始化后可使用：

```text
用户名：admin
密码：123456
```

## 页面路由

| 路由 | 页面 |
| --- | --- |
| `/login` | 管理员登录 |
| `/home` | 首页概览 |
| `/categories` | 分类管理 |
| `/products` | 商品管理 |
| `/orders` | 订单管理 |
| `/users` | 用户管理 |
| `/knowledge` | 知识库管理 |

## 业务功能

- 后台使用 Element Plus 构建表格、弹窗、表单、分页等典型管理系统交互。
- 路由守卫结合本地 Token 判断登录态，避免未登录访问后台页面。
- 用户管理、商品管理、订单管理覆盖常见后台 CRUD 场景。
- 知识库管理展示了从文件上传到后端向量化处理的 AI 管理闭环。
- 仪表盘使用 ECharts 展示运营数据，体现数据可视化能力。
