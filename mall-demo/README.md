# 3C 数码商城：公开演示运行包

该模块是原商城项目的求职体验入口。独立构建为一个 Java 进程，复用原购物车实现，以及演示订单的 Token Lua、原订单实体 / Mapper 和库存条件 SQL。商品通过本地适配器读取 MySQL；页面提供三种虚构商品、购物车、订单确认、创建、取消与超时关闭。

这不是原微服务全量部署：没有 Nacos、MQ、Seata、秒杀、真实登录、真实支付或履约。商品详情在此包中采用精简数据库查询，不宣称运行了原商品详情的异步聚合链路。本机原模块仍可独立运行。

## 免费托管的数据生命周期

Docker 容器内运行 Java、MySQL 8.0 和 Redis。MySQL / Redis 只监听回环地址，公开端口只有 Java 的 `PORT`。初始化时生成临时随机数据库密码，Java 数据库用户仅有该演示库的 SELECT / INSERT / UPDATE 权限。

每次应用启动创建全新的虚构数据：9 张必要表、21 条商品与库存记录；不导入个人信息，也不上传本机数据库、缓存或日志。服务休眠 / 重启后购物车、订单和库存重置，页面有明确提示。它适合功能演示，不保存真实交易。Render 免费服务本地文件生命周期见 https://render.com/docs/free。

## 构建与部署

```sh
mvn -B -ntp -f mall-demo/pom.xml package -DskipTests
docker build -t lyd-mall-demo ./mall-demo
docker run --rm -p 4185:4185 -e PORT=4185 lyd-mall-demo
```

Render Web Service 选择原仓库 `liangyingde007/3c-mall-trade-system`、`main` 分支，Root Directory 为 `mall-demo`，Runtime 为 Docker，Dockerfile Path 为 `./Dockerfile`，Free 类型。Health Check Path 为 `/actuator/health`。无需添加原开发环境配置或私有密钥。

Docker 构建上下文采用默认拒绝的 `.dockerignore`；不会读取模块外的原配置。这里只扫描新入口并显式导入必要的演示控制器，原商品后台、会员管理和交易接口不加载。

## 原代码溯源

`tools/sync_sources.py` 是 34 个源码 / 资源的明确白名单；`source-manifest.json` 保存原仓库相对路径和 SHA-256。演示包里 `com.msb.*` 文件由它同步，版权注释保留。公网首页增加了临时数据提示，清楚记录来源和目标哈希。

```sh
python mall-demo/tools/sync_sources.py --check
```

本模块不作为根 POM 的新默认模块，原工程启动行为保持原样。

## 验证状态

2026-10-04：本机独立入口 `http://127.0.0.1:4185` 已通过 37 项购物车与 30 项订单 / 实际 MySQL 事务检查。使用单独的 `lyd_mall_public_demo` 9 表数据和 Redis 13380；原本机库 / Redis 未重置。Docker / Linux / 公网结果另行记录，不能把本机结果当作已上线证明。
