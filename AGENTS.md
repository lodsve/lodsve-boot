# Repository Guidelines

## 项目结构与模块组织

本项目当前基于 JDK 21 和 Spring Boot 2.6.3，使用 Maven 多模块构建。已发布的历史版本可能使用更低版本的 JDK。

- `lodsve-boot-project/`：核心库、Actuator、自动配置、`lodsve-boot-components/` 功能实现和 `lodsve-boot-starters/` 依赖入口。
- `lodsve-boot-dependencies/` 与 `lodsve-boot-parent/` 位于上述目录中，分别管理依赖与父 POM 配置。
- `lodsve-boot-examples/`：各组件示例，默认不参与根项目构建。
- 各模块使用 `src/main/java`、`src/main/resources` 和 `src/test/java`；资源包括 Spring 配置、元数据和国际化文件。
- `tools/` 保存检查配置，`.github/` 保存 CI 和贡献模板。

## 构建、测试与本地开发

在仓库根目录使用 Maven Wrapper；Windows 使用 `mvnw.cmd`。

- `./mvnw clean install`：清理、构建、运行测试并安装到本地 Maven 仓库，与 CI 的主要构建步骤一致。
- `./mvnw test`：运行默认模块的测试。
- `./mvnw -pl lodsve-boot-project/lodsve-boot-actuator -am test`：测试 Actuator 及其所需模块。
- `./mvnw -Pinclude-examples install`：同时构建示例模块。

首次导入 IDE 或执行 `clean` 后，按 README 执行源码生成：

```sh
./mvnw com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
```

本地运行可从示例中的 `ExampleApplication` 启动；先配置对应资源文件和所需外部服务。

## 编码风格与命名

遵循 `.editorconfig`：UTF-8、LF、空格缩进，默认 4 空格，YAML 为 2 空格；一般行宽为 120。类名使用 `PascalCase`，方法和字段使用 `camelCase`，包名沿用 `com.lodsve.boot`。自动配置和配置属性保持 `*AutoConfiguration`、`*Properties` 命名。

参考相邻模块的导入和注释风格，保留 Apache 许可证头。README 要求 Alibaba Java Coding Guidelines IDE 插件；父 POM 配置了 Checkstyle，规则位于 `tools/check-style.xml`。

## 测试指南

现有 `DependenciesEndpointTest` 使用 JUnit 4 的 `org.junit.Test` 和 `Assert`。测试放在对应模块的 `src/test/java`，包路径与被测代码一致，类名以 `Test` 结尾。行为变更应补充正常路径和相关异常路径测试。未发现明确的覆盖率百分比要求；提交前运行受影响模块测试，并确认 Maven 实际执行了测试。

## 提交与 Pull Request

Git 历史和提交模板采用 `type(scope): 描述`，例如 `fix(filesystem): 修复文件下载行为`；scope 可省略。使用简短中文描述，主题行不超过 50 字符且不加句号。可通过 `git config commit.template ./git/templates/commit-message-template` 启用模板。

从 `develop` 创建分支并向其提交 PR；不存在时使用 `master`。按照 `.github/PULL_REQUEST_TEMPLATE.md` 填写关联 Issue、变更说明、测试用例及结果，有可见效果时附截图。保持变更范围集中，并说明兼容性影响。
