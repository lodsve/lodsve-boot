---
title: "Architecture Constraints"
readMode: required
priority: high
category: arch
keywords:
  - architecture
  - module
  - layer
  - boundary
  - dependency
  - structure
---

# Architecture Constraints

## Module Structure

## Layer Boundaries

## Dependency Rules

## Technology Constraints

## Entries



<spec-entry category="arch" keywords="" date="2026-09-30" sid="S-20260930-n7ds" title="模块和依赖边界" sourceRef="lodsve-boot-project/pom.xml" relatedPaths="lodsve-boot-project/pom.xml">

### 模块和依赖边界

根构建默认聚合 lodsve-boot-project；示例由 include-examples profile 开启。核心能力、组件实现、自动配置和 Starter 分目录；Starter 聚合自动配置与对应组件依赖。依赖版本集中到 dependencies/parent 层管理，变更不得引入反向示例依赖。

证据：`lodsve-boot-project/pom.xml`。

</spec-entry>
