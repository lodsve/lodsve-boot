---
title: "Test Conventions"
readMode: required
priority: high
category: test
keywords:
  - test
  - coverage
  - mock
  - fixture
  - assertion
  - framework
---

# Test Conventions

## Framework

## Directory Structure

## Naming Conventions

## Patterns

## Entries



<spec-entry category="test" keywords="" date="2026-09-30" sid="S-20260930-w6s3" title="JUnit 4 测试约定" sourceRef="lodsve-boot-project/lodsve-boot-actuator/src/test/java/com/lodsve/boot/actuator/dependencies/DependenciesEndpointTest.java" relatedPaths="lodsve-boot-project/lodsve-boot-actuator/src/test/java/com/lodsve/boot/actuator/dependencies/DependenciesEndpointTest.java">

### JUnit 4 测试约定

现有 DependenciesEndpointTest 使用 org.junit.Test 和 Assert。新增对应测试放 src/test/java，保持包路径和 *Test 命名。运行 ./mvnw test 或 ./mvnw -pl lodsve-boot-project/lodsve-boot-actuator -am test，并核查 Surefire 实际测试数量；不把构建成功等同于有测试执行。未检测到覆盖率门槛。

证据：`lodsve-boot-project/lodsve-boot-actuator/src/test/java/com/lodsve/boot/actuator/dependencies/DependenciesEndpointTest.java`。

</spec-entry>
