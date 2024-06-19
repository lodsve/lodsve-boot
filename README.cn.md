# lodsve-boot

> 基于Spring-Boot和一些开源组件的开发套件！

```
 _               _                ______             _
| |             | |               | ___ \           | |
| |     ___   __| |_____   _____  | |_/ / ___   ___ | |_
| |    / _ \ / _` / __\ \ / / _ \ | ___ \/ _ \ / _ \| __|
| |___| (_) | (_| \__ \\ V /  __/ | |_/ / (_) | (_) | |_
\_____/\___/ \__,_|___/ \_/ \___| \____/ \___/ \___/ \__|
```

[英文版本](README.md)

[![Deploy Snapshot Weekly](https://github.com/lodsve/lodsve-boot/actions/workflows/deploy-snapshot-weekly.yml/badge.svg?branch=master)](https://github.com/lodsve/lodsve-boot/actions/workflows/deploy-snapshot-weekly.yml)
[![Github Action Maven Verify](https://github.com/lodsve/lodsve-boot/actions/workflows/maven-verify.yml/badge.svg?branch=master)](https://github.com/lodsve/lodsve-boot/actions/workflows/maven-verify.yml)

[![LICENSE](https://img.shields.io/github/license/lodsve/lodsve-boot)](https://github.com/lodsve/lodsve-boot/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.lodsve.boot/lodsve-boot.svg)](https://search.maven.org/artifact/com.lodsve.boot/lodsve-boot)
[![GitHub stars](https://img.shields.io/github/stars/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/network)
[![GitHub issues](https://img.shields.io/github/issues/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/pulls)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot?ref=badge_shield)

## 简单的介绍

1. `Lodsve`基于Spring-Boot和一些开源组件的开发包！
2. 包含了以下的模块：

   参考 [提供的能力](capability_cn.md)
3. **注意**：由于优化了获取版本号的方式，在首次导入到idea中，或者执行过`mvn clean`命令后，都需要在根目录执行一下以下命令：
   ```
   # Linux or MacOS
   ./mvnw com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
   # Windows
   ./mvnw.cmd com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
   ```

## 如何使用

1. 使用release版本

        <parent>
            <groupId>com.lodsve.boot</groupId>
            <artifactId>lodsve-boot-dependencies</artifactId>
            <version>x.x.x</version>
        </parent>
2. 部分插件的使用方式，可参考[组件介绍](Instructions_cn.md)
3. 如果您只是想尝试新功能，请尝试预发布版本。 如果您有任何问题，请在 issue 中与我联系。

        <parent>
            <groupId>com.lodsve.boot</groupId>
            <artifactId>lodsve-boot-dependencies</artifactId>
            <version>x.x.x-SNAPSHOT</version>
        </parent>
        <repositories>
            <repository>
                <id>maven-center-snapshot</id>
                <name>Maven Center Snapshot</name>
                <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
                <snapshots>
                    <enabled>true</enabled>
                </snapshots>
            </repository>
        </repositories>

## 关于发版

1. 我每周都会通过Github Action自动发布一个快照版本！

   构建版本号，如 `x.x.x-YYYYMMDD-SNAPSHOT`。
2. 不定时发布Release版本。

   构建版本号，如 `x.x.x`。

## 检出源码

`git clone git@github.com:lodsve/lodsve-boot.git`

## 导入到您的IDE中

在项目根目录运行命令 `mvn idea:idea` 或者 `mvn eclipse:eclipse` 。
> **注意:** 根据上述先决条件，确保已在IDE中正确配置了 `JDK 8`，`Maven 3.3.X` 和 `Lombok插件` 。

1. 配置Git

        git config --global user.name "your name"
        git config --global user.email "your email"
        git config --global core.autocrlf false
        git config --global core.safecrlf true

2. 为了更标准的git提交信息

       # commit message template
       git config commit.template ./git/templates/commit-message-template
       # git hook to check commit message
       # windows
       fsutil hardlink create .git\hooks\commit-msg git\hooks\check-commit-msg
       # linux
       cd ./.git/hooks
       ln -s ../../git/hooks/check-commit-msg commit-msg

3. 配置您的IDE
    - Eclipse：打开 Settings-General-Workspace，修改 `New text file line delimiter` 为 `Unix`。
    - Eclipse：打开 Settings-General-Workspace，修改 `Text file encoding` 为 `UTF-8`。
    - IDE：打开 Setting-Editor-Code Style，修改 `line delimiter` 为 `Unix and OS X(\n)`。
    - IDE：打开 Setting-Editor-File encoding，修改所有的 `Encoding` 为 `UTF-8` 和 `with NO BOM`。
4. 必须要安装的IDE插件(Eclipse 和 Intellij IDEA):
    - Alibaba Java Coding Guidelines
    - Lombok plugin

## 文档

查看master分支的 [JavaDoc][].

## 联系我

1. 邮箱: sunhao.java@gmail.com
2. QQ: [867885140][]
3. 博客: [Blog][] [OSChina][]

## License

`Lodsve Boot` 是在 [Apache License][] 的2.0版下发布的。

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot?ref=badge_large)

## Thanks

`Lodsve Boot` 是基于 [JetBrains IDEA][] 创建的。

![JetBrains logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg)

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

[Blog]: https://www.crazy-coder.cn

[OSChina]: https://my.oschina.net/sunhaojava

[867885140]: http://wpa.qq.com/msgrd?v=3&uin=867885140&site=qq&menu=yes

[JetBrains IDEA]: https://www.jetbrains.com/?from=lodsve-boot

[JavaDoc]: https://javadoc.lodsve.com
