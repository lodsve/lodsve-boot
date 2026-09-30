---
title: "Review Standards"
readMode: required
priority: medium
category: review
keywords:
  - review
  - checklist
  - gate
  - approval
  - standard
---

# Review Standards

## Entries



<spec-entry category="review" keywords="" date="2026-09-30" sid="S-20260930-0j0v" title="现有质量检查" sourceRef="AGENTS.md" relatedPaths="AGENTS.md">

### 现有质量检查

遵循 AGENTS.md 与父 POM 的 Checkstyle、PMD 和许可证检查；规则位于 tools/。CI 使用 JDK 17 和 Maven Wrapper 执行 clean install。初始化不更换检查工具、不关闭检查；后续变更按受影响模块验证。

证据：`AGENTS.md`。

</spec-entry>
