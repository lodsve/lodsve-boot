# MongoDB 示例

此示例演示 Spring Data MongoDB 的 Repository CRUD，以及默认数据源和 `archive` 具名数据源之间的切换。

先启动本地 MongoDB，或设置连接 URI 环境变量，然后从 `lodsve-boot` 根目录运行：

```shell
MONGODB_URI=mongodb://localhost:27017/lodsve \
MONGODB_ARCHIVE_URI=mongodb://localhost:27017/lodsve_archive \
mvn -pl lodsve-boot-examples/lodsve-boot-example-mongodb -am spring-boot:run
```

示例服务默认监听 `8080`，上下文路径为 `/mongo`。默认数据源接口：

| 操作 | 请求 |
|---|---|
| 保存 | `POST /mongo/demo/save` |
| 按 ID 查询 | `GET /mongo/demo/{id}` |
| 查询全部 | `GET /mongo/demo` |

`archive` 接口由 `DemoService` 中的 `@SwitchMongoDataSource("archive")` 方法路由：

| 操作 | 请求 |
|---|---|
| 保存 | `POST /mongo/demo/archive/save` |
| 按 ID 查询 | `GET /mongo/demo/archive/{id}` |
| 查询全部 | `GET /mongo/demo/archive/list` |

保存请求体示例：

```json
{
  "name": "Alice",
  "age": 30
}
```

MongoDB 会为未指定的 `String` ID 生成 ObjectId。可在响应中读取 `id`，再用于查询。
