---
title: "Coding Conventions"
readMode: required
priority: high
category: coding
keywords:
  - style
  - naming
  - import
  - pattern
  - convention
  - formatting
---

# Coding Conventions

## Formatting

## Naming

## Imports

## Patterns

## Entries



<spec-entry category="coding" keywords="" date="2026-09-30" sid="S-20260930-h575" title="Java 格式与命名" sourceRef=".editorconfig" relatedPaths=".editorconfig">

### Java 格式与命名

以 .editorconfig 为准：UTF-8、LF、默认 4 空格，YAML 2 空格，一般行宽 120；类名 PascalCase、字段和方法 camelCase、常量 UPPER_SNAKE_CASE，包名沿用 com.lodsve.boot。保留现有 Apache 许可证头，导入分组参考相邻源码，不批量整理通配符导入。

证据：`.editorconfig`。

</spec-entry>

<spec-entry category="coding" keywords="" date="2026-09-30" sid="S-20260930-h6xr" title="自动配置模式" sourceRef="lodsve-boot-project/lodsve-boot-autoconfigure/src/main/java/com/lodsve/boot/autoconfigure/redis/RedisAutoConfiguration.java" relatedPaths="lodsve-boot-project/lodsve-boot-autoconfigure/src/main/java/com/lodsve/boot/autoconfigure/redis/RedisAutoConfiguration.java">

### 自动配置模式

新增同类配置参照 RedisAutoConfiguration、EventAutoConfiguration、ValidatorAutoConfiguration：以 @Configuration 声明配置，使用 @ConditionalOnClass 等条件限制激活，配置属性通过 *Properties 和 @EnableConfigurationProperties 绑定；需要注册时同步 META-INF/spring.factories。沿用具体模块现有条件和命名，不统一改写历史配置。

证据：`lodsve-boot-project/lodsve-boot-autoconfigure/src/main/java/com/lodsve/boot/autoconfigure/redis/RedisAutoConfiguration.java`。

</spec-entry>
