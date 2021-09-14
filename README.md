# lodsve-boot

Lodsve develop kits base on Spring-Boot and some opensource components!

```
 _               _                ______             _
| |             | |               | ___ \           | |
| |     ___   __| |_____   _____  | |_/ / ___   ___ | |_
| |    / _ \ / _` / __\ \ / / _ \ | ___ \/ _ \ / _ \| __|
| |___| (_) | (_| \__ \\ V /  __/ | |_/ / (_) | (_) | |_
\_____/\___/ \__,_|___/ \_/ \___| \____/ \___/ \___/ \__|
```

## How To Use
1. release version

        <parent>
            <groupId>com.lodsve.boot</groupId>
            <artifactId>lodsve-boot-parent</artifactId>
            <version>x.x.x.RELEASE</version>
        </parent>
        <dependency>
            <groupId>com.lodsve</groupId>
            <artifactId>lodsve-framework</artifactId>
            <version>${lodsve.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
2. If you just to try new feature, please try the pre-release version. If you have any questions, please contact me in the issue.

        <parent>
            <groupId>com.lodsve.boot</groupId>
            <artifactId>lodsve-boot-parent</artifactId>
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

    Artifact version number like `x.x.x-SNAPSHOT`.
2. Release a release version from time to time.

    Artifact version number like `x.x.x.RELEASE`.

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
2. Config your IDE
    - Eclipse: Open Settings-General-Workspace, modify `New text file line delimiter` as `Unix`
    - Eclipse: Open Settings-General-Workspace, modify `Text file encoding` as `UTF-8`
    - IDE: Open Setting-Editor-Code Style, modify `line delimiter` as `Unix and OS X(\n)`
    - IDE: Open Setting-Editor-File encoding, modify all `Encoding` as `UTF-8` and `with NO BOM`

## Contact me

1. Email: sunhao.java@gmail.com
2. QQ: [867885140][]
3. Blog: [Blog][] [OSChina][]

## License

The `Lodsve Boot` is released under version 2.0 of the [Apache License][].

## Thanks

The `Lodsve Boot` was created using awesome [JetBrains IDEA][].

![LOGO](.github/JetBrains.png "JetBrains")

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

[Blog]: https://www.crazy-coder.cn

[OSChina]: https://my.oschina.net/sunhaojava

[867885140]: http://wpa.qq.com/msgrd?v=3&uin=867885140&site=qq&menu=yes

[JetBrains IDEA]: https://www.jetbrains.com/?from=lodsve-framework
