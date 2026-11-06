# 电力设备采购平台

[![Build and Test](https://github.com/LFCCCD/electric-equipment-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/LFCCCD/electric-equipment-platform/actions/workflows/ci.yml)

面向平台运营人员与企业采购人员的电力设备采购平台，包含 Spring Boot 后端、
Vue 3 运营管理后台和 Vue 3 + Vant 移动采购 H5。项目在
[Sonder-MX/sky-take-out](https://github.com/Sonder-MX/sky-take-out) 的 Apache-2.0
许可代码基础上进行业务域重构，保留成熟的后台管理、购物车、订单、报表和鉴权能力，
并将商品域改为设备、规格与设备套餐。

当前版本可以直接演示“运营维护设备与套餐、采购人员选型加购下单、运营确认发货”的
完整前后端流程。

## 功能范围

### 运营管理后台

- 运营人员登录、增删改查、启停用与密码修改。
- 设备分类、设备基础信息、品牌、型号、计价单位、库存和规格维护。
- 设备套餐组合、上下架与套餐明细维护。
- 采购订单查询、确认、拒绝、发货、取消和完成。
- 商户可用状态、运营工作台、销售与订单报表、Excel 导出。
- 设备图片和技术资料上传，支持阿里云 OSS 配置。

### 移动采购端接口

- 采购人员登录、交付地址维护。
- 按分类浏览设备和设备套餐、查看规格与详情。
- 购物车添加、数量调整、删除和清空。
- 提交订单、查询历史订单、订单详情、取消订单、再次下单与确认收货。

## 技术栈

- Java 17
- Spring Boot 3.1.2
- MyBatis、PageHelper
- MySQL 8.0
- Redis 7
- JWT、ThreadLocal
- Vue 3、Vue Router、Axios
- Element Plus、Vant
- Maven
- Docker、Docker Compose

## 项目结构

```text
electric-equipment-platform
├── equipment-common   公共常量、异常、工具与配置属性
├── equipment-pojo     DTO、实体、VO
├── equipment-server   Controller、Service、Mapper、配置与启动类
├── frontend
│   ├── admin-web      Vue 3 + Element Plus 运营管理后台
│   └── mobile-web     Vue 3 + Vant 移动采购 H5
├── demo               MySQL 建表与演示数据
├── docs               产品需求文档
├── Dockerfile
└── docker-compose.yml
```

## 快速启动

### Docker Compose

1. 安装 Docker Desktop。
2. 将 `.env.example` 复制为 `.env`，修改数据库密码和 JWT 密钥。
3. 启动 MySQL、Redis、后端和两个前端：

```bash
docker compose up --build
```

- 运营后台：`http://localhost:8081`
- 移动采购端：`http://localhost:8082`
- 后端健康检查：`http://localhost:8080/actuator/health`
- 接口文档：`http://localhost:8080/swagger-ui.html`

首次启动时，MySQL 会自动执行 `demo/mysql.sql`，创建数据库、表、演示设备与套餐。

演示后台账号：

```text
账号：admin
密码：123456
```

共享环境中必须立即修改默认密码。

演示采购账号：

```text
账号：buyer
密码：123456
```

### 本地开发

需要 JDK 17、Maven 3.9、MySQL 8.0 和 Redis 7。

```bash
mysql -uroot -p < demo/mysql.sql
mvn clean test
mvn -pl equipment-server -am spring-boot:run
```

在另一个终端安装并启动前端：

```bash
npm install
npm run dev:admin
npm run dev:mobile
```

- 运营后台开发地址：`http://localhost:5173`
- 移动采购端开发地址：`http://localhost:5174`

配置通过环境变量注入，常用变量如下：

| 变量 | 说明 | 默认值 |
| --- | --- | --- |
| `DB_URL` | JDBC 地址 | 本机 `electric_equipment_platform` |
| `DB_USERNAME` | 数据库账号 | `root` |
| `DB_PASSWORD` | 数据库密码 | `change-me` |
| `REDIS_HOST` | Redis 地址 | `127.0.0.1` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | `change-me` |
| `JWT_ADMIN_SECRET` | 后台 JWT 密钥 | 开发占位值 |
| `JWT_USER_SECRET` | 采购端 JWT 密钥 | 开发占位值 |

OSS 和微信支付的配置项参考
`equipment-server/src/main/resources/application-env.example.yml`。不要将真实密码、
私钥或 AccessKey 提交到仓库。

## 主要接口

接口统一使用 `/admin` 和 `/user` 前缀区分运营端与采购端。

```text
POST /admin/operator/login
GET  /admin/equipment/page
POST /admin/equipment
GET  /admin/package/page
GET  /admin/order/conditionSearch
PUT  /admin/order/confirm

POST /user/user/demo-login
POST /user/user/login
GET  /user/category/list
GET  /user/equipment/list
GET  /user/equipment/{id}
GET  /user/package/list
GET  /user/package/{id}
POST /user/shoppingCart/add
POST /user/order/submit
```

## 数据与安全说明

- 密码使用 PBKDF2-SHA256 加盐哈希保存，不再使用 MD5。
- JWT 密钥、数据库密码、Redis 密码和 OSS 凭证通过环境变量配置。
- 采购人员只能访问本人的购物车、地址和订单；运营接口统一经过后台 JWT 拦截器。
- `demo/mysql.sql` 提供 MySQL 8.0 基线。SQL Server 兼容属于后续数据库适配工作，
  当前仓库没有将 SQL Server 支持标记为已完成。
- 当前支付实现保留演示用支付成功逻辑。生产接入支付前，必须补充支付回调验签、
  幂等处理、金额校验和退款状态机。

## 测试

```bash
mvn clean test
npm run build
```

服务层单元测试使用 Mockito，不依赖本地 MySQL、Redis 或外部支付服务；前端构建会同时
检查运营后台和移动端两个 Vue 应用。

## 产品文档

- [电力设备采购平台产品需求文档 V1.0](docs/电力设备采购平台产品需求文档_V1.0.docx)

## 许可证与来源

本项目保留上游项目的 Apache License 2.0 和来源说明。二次开发内容仍按 Apache-2.0
分发；引用或展示项目时应同时保留 [LICENSE](LICENSE) 与上游项目链接。
