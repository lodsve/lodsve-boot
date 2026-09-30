---
title: lodsve-boot — 执行测试
type: recipe
explicitId: rcp-20260930-test-workflow
created: 2026-09-30T15:41:58.860Z
keywords:
  - workflow
  - test-workflow
  - auto-generated
sourceRef: AGENTS.md
lifecycleStatus: active
relatedPaths:
  - AGENTS.md
---

## Goal

执行测试。

## Prerequisites

使用 JDK 17，保证依赖和生成源码可用。

## Steps

在项目根目录执行：

```sh
./mvnw test
./mvnw -pl lodsve-boot-project/lodsve-boot-actuator -am test
```

## Expected Outcome

查看 Surefire 报告，确认 DependenciesEndpointTest 被执行。

## Common Pitfalls

没有确认到现成的 watch 或覆盖率命令，不编造对应命令；不得使用跳过测试的选项代替修复。

## Related

- `AGENTS.md`
- [[architecture-constraints]]
