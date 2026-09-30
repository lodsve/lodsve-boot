# Project: lodsve-boot

## What This Is

基于 Spring Boot 的 Java 开发套件，为应用提供可组合的组件、Starter 与自动配置。

## Core Value

以一致的组件接入方式减少 Java 应用的基础设施集成工作。

## Requirements

### Validated

以下能力由现有源码/配置确认，不代表本次完成运行时测试或用户价值验证。

- [x] 提供核心库、Actuator、自动配置、组件、Starter、依赖管理与父 POM 等模块。 证据：`lodsve-boot-project/pom.xml`。
- [x] 通过 spring.factories 注册 Redis、MyBatis、消息、文件系统等自动配置。 证据：`lodsve-boot-project/lodsve-boot-autoconfigure/src/main/resources/META-INF/spring.factories`。
- [x] 以 Starter 聚合自动配置和组件依赖，Redis Starter 为代表。 证据：`lodsve-boot-project/lodsve-boot-starters/lodsve-boot-starter-redis/pom.xml`。
- [x] 提供组件示例，通过 include-examples profile 纳入构建。 证据：`pom.xml`。

### Active

本次范围为既有项目接入 Maestro；尚未指定新增产品需求。

### Out of Scope

- 本次不升级技术栈、不改变公开接口、不调整现有源码目录。

## Context

项目属于 lodsve 工作区；各子项目独立维护工作流与知识。

## Constraints

- 保持现有行为和向后兼容。
- 基于各项目现有构建工具和代码约定工作。

## Tech Stack

Java 17；Spring Boot 2.6.3；Maven Wrapper 3.8.7；Maven 多模块；当前 revision 为 1.0.3。数据库和中间件由使用的组件及应用配置决定，不是单一固定数据库。

## Key Decisions

| # | Decision | Choice | Source (user / code / default) | Confidence (high / medium / LOW) |
|---|----------|--------|--------------------------------|---------------------------------|
| 1 | 初始化范围 | 5 个子项目独立初始化，各用根目录 .workflow/ | user | high |
| 2 | 项目类型 | 既有项目接入 | code | high |
| 3 | 技术栈与目录 | 沿用当前构建配置与源码布局 | code | high |
| 4 | 代码索引 | 初始化时建立 Maestro 代码知识图谱 | user | high |
| 5 | 工作流偏好 | 研究、复盘、工作流文档提交、执行后代码库文档同步开启；仅已有 Git 仓库提交 | user | high |
| 6 | 本次目标 | 仅接入工作流；未指定后续产品目标，不预设路线图 | default | LOW |
| 7 | 规范与词表 | 根据源码填充规范；自动发现无术语候选，词表保持空列表 | code | high |

## Stakeholders

- 项目维护者与 Lodsve 使用者。

## Initialization Notes

- W001：4 个并行研究代理各重试一次仍返回 502/503，汇总代理也失败，未形成代理研究报告。保留研究开关，后续阶段可重试；本次规范来自直接源码扫描，不宣称完成了并行研究。
- 当前 Maestro 缺少通用 XML 提取器；本次代码索引排除了 XML。POM/模板元数据仍需直接读取。过滤规则已保存到 .maestroignore，后续默认索引会沿用该边界。
- 索引重建命令：`maestro kg index --include-tests`；过滤规则保存在项目根目录 `.maestroignore`。
- 已执行 `maestro spec init`、`maestro domain init`、`maestro domain discover`；无自动发现的术语候选。
- 本次验证配置、知识条目与索引完整性；没有执行项目构建或业务测试。

---
*Last updated: 2026-09-30 after initialization*
