# lodsve-boot

> Lodsve develop kits base on Spring-Boot and some opensource components!

```
 _               _                ______             _
| |             | |               | ___ \           | |
| |     ___   __| |_____   _____  | |_/ / ___   ___ | |_
| |    / _ \ / _` / __\ \ / / _ \ | ___ \/ _ \ / _ \| __|
| |___| (_) | (_| \__ \\ V /  __/ | |_/ / (_) | (_) | |_
\_____/\___/ \__,_|___/ \_/ \___| \____/ \___/ \___/ \__|
```

[中文版本](README.cn.md)

[![Deploy Snapshot Weekly](https://github.com/lodsve/lodsve-boot/actions/workflows/deploy-snapshot-weekly.yml/badge.svg?branch=master)](https://github.com/lodsve/lodsve-boot/actions/workflows/deploy-snapshot-weekly.yml)
[![Github Action Maven Verify](https://github.com/lodsve/lodsve-boot/actions/workflows/maven-verify.yml/badge.svg?branch=master)](https://github.com/lodsve/lodsve-boot/actions/workflows/maven-verify.yml)

[![LICENSE](https://img.shields.io/github/license/lodsve/lodsve-boot)](https://github.com/lodsve/lodsve-boot/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.lodsve.boot/lodsve-boot.svg)](https://search.maven.org/artifact/com.lodsve.boot/lodsve-boot)
[![GitHub stars](https://img.shields.io/github/stars/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/network)
[![GitHub issues](https://img.shields.io/github/issues/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/lodsve/lodsve-boot.svg)](https://github.com/lodsve/lodsve-boot/pulls)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot?ref=badge_shield)

## Simple Introduction

1. Lodsve develop kits base on Spring-Boot and some opensource components!
2. It consists of the following modules:

   See [capability](capability.md)
3. **Note**: Due to the optimized way of retrieving the version number, after importing into IntelliJ IDEA for the first time or executing the `mvn clean` command, it is necessary to run the following command in the root directory:
   ```
   # Linux or MacOS
   ./mvnw com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
   # Windows
   ./mvnw.cmd com.lodsve.maven.plugins:lodsve-javatemplate-maven-plugin:1.0.3:generate-sources
   ```

## How To Use

1. release version

        <parent>
            <groupId>com.lodsve.boot</groupId>
            <artifactId>lodsve-boot-dependencies</artifactId>
            <version>x.x.x</version>
        </parent>
2. You can refer to the [Component Introduction](Instructions.md) for the usage of some plugins.
3. If you just to try new feature, please try the pre-release version. If you have any questions, please contact me in
   the issue.

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

## About release

1. I will automatically publish a snapshot version every week through Github Action!

   Artifact version number like `x.x.x-YYYYMMDD-SNAPSHOT`.
2. Release a release version from time to time.

   Artifact version number like `x.x.x`.

## Check out sources

`git clone git@github.com:lodsve/lodsve-boot.git`

## Import sources into your IDE

Run command `mvn idea:idea` or `mvn eclipse:eclipse` in the root folder.
> **Note:** Per the prerequisites above, ensure that you have JDK 8 and Maven 3.3.X configured properly in your IDE.

1. Config your Git

        git config --global user.name "your name"
        git config --global user.email "your email"
        git config --global core.autocrlf false
        git config --global core.safecrlf true

2. For a more standard git commit message

       # commit message template
       git config commit.template ./git/templates/commit-message-template
       # git hook to check commit message
       # windows
       fsutil hardlink create .git\hooks\commit-msg git\hooks\check-commit-msg
       # linux
       cd ./.git/hooks/
       ln -s ../../git/hooks/check-commit-msg commit-msg

3. Config your IDE
    - Eclipse: Open Settings-General-Workspace, modify `New text file line delimiter` as `Unix`
    - Eclipse: Open Settings-General-Workspace, modify `Text file encoding` as `UTF-8`
    - IDE: Open Setting-Editor-Code Style, modify `line delimiter` as `Unix and OS X(\n)`
    - IDE: Open Setting-Editor-File encoding, modify all `Encoding` as `UTF-8` and `with NO BOM`
4. Required IDE Plugins(Both Eclipse and Intellij IDEA):
    - Alibaba Java Coding Guidelines
    - Lombok plugin

## Documentation

See the master branch [JavaDoc][].

## Contact me

1. Email: sunhao.java@gmail.com
2. QQ: [867885140][]
3. Blog: [Blog][] [OSChina][]

## License

The `Lodsve Boot` is released under version 2.0 of the [Apache License][].

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Flodsve%2Flodsve-boot?ref=badge_large)

## Thanks

The `Lodsve Boot` was created using awesome [JetBrains IDEA][].

![LOGO](.github/JetBrains.png "JetBrains")

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

[Blog]: https://www.crazy-coder.cn

[OSChina]: https://my.oschina.net/sunhaojava

[867885140]: http://wpa.qq.com/msgrd?v=3&uin=867885140&site=qq&menu=yes

[JetBrains IDEA]: https://www.jetbrains.com/?from=lodsve-framework

[JavaDoc]: https://javadoc.lodsve.com
