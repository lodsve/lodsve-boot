---
title: lodsve-boot — 构建项目
type: recipe
explicitId: rcp-20260930-build-workflow
created: 2026-09-30T15:41:58.722Z
keywords:
  - workflow
  - build-workflow
  - auto-generated
sourceRef: README.cn.md
lifecycleStatus: active
relatedPaths:
  - README.cn.md
---

## Goal

构建项目。

## Prerequisites

需要 JDK 17 和可用的 Maven 依赖仓库。

## Steps

在项目根目录执行：

```sh
./mvnw com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
./mvnw clean install
```

## Expected Outcome

模块构建并安装到本地 Maven 仓库；产物位于各模块 target/。

## Common Pitfalls

首次导入或 clean 后需要生成版本相关源码；需要示例时使用 ./mvnw -Pinclude-examples install。构建命令沿用 README/CI，本次初始化未执行。

## Related

- `README.cn.md`
- [[architecture-constraints]]
