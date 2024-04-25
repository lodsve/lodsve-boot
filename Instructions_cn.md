# 部分组件使用说明

## lodsve-boot-starter-encryption

1. 说明
    ```text
    利用base64、jasypt等加解密工具，对项目中一些敏感信息进行加密。
    ```

2. 配置示例
    ```yaml
    lodsve:
      encryption:
        enabled: true
        base64:
          prefix: BASE64(
          suffix: )
        jasypt:
          prefix: ENC(
          suffix: )
          password: lodsve
          algorithm: PBEWithMD5AndDES
          key-obtention-iterations: 1000
          poolSize: 1
          providerName: xxx
          providerClassName: xxx
          saltGeneratorClassname: org.jasypt.salt.RandomSaltGenerator
          ivGeneratorClassname: org.jasypt.iv.NoIvGenerator
          stringOutputType: base64
    ```
3. 配置解释
    1. enabled: 启用加解密组件
    2. base64和jasypt，表示支持的两种加解密算法，其中base64不推荐使用
    3. prefix、suffix
       ```text
       标识配置中，哪些是密文，使用prefix和suffix包裹的字符串为密文，例如：
       prefix：TEXT{
       suffix：}
       则，TEXT{tGwoovl9DkJhf45zJ2mRHg==}为密文，需取出其中的字符串进行解密
       ```
    4. 其他配置：
       ```yaml
       # 加密因子
       # 通常来说，这里的password，需要放在启动变量中传入，或者使用占位符，然后用环境变量传入，例如:
       # password: ${WEILAN_PASSWORD}
       password: lodsve
       # 解密算法
       algorithm: PBEWithMD5AndDES
       # 获取签名密钥的哈希迭代次数
       key-obtention-iterations: 1000
       # 要创建的加密器池的大小
       pool-size: 1
       # 加密程序将用于获取加密算法的{@link java.security.Provider}实现的名称。
       providerName: xxx
       # 加密程序将使用{@link java.security.Provider}实现的类名称来获取加密算法。 默认值为{@code null}
       provider-class-name: xxx
       # 加密器要使用的{@link org.jasypt.salt.SaltGenerator}实现。 默认值为{@code "org.jasypt.salt.RandomSaltGenerator"}
       salt-generator-classname: org.jasypt.salt.RandomSaltGenerator
       # 加密器要使用的{@link org.jasypt.iv.IvGenerator}实现。 默认值为{@code "org.jasypt.iv.NoIvGenerator"}.
       ivG-generator-classname: org.jasypt.iv.NoIvGenerator
       # 指定将String输出编码的形式.{@code "base64"} or {@code "hexadecimal"}.默认值为{@code "base64"}
       string-output-type: base64
       ```
4. 加密工具的使用（用Windows举例）
    1. 下载工具包`jasypt-1.9.3-dist.zip`
    2. 进入bin目录
    3. 使用encrypt.bat进行加密
       ```shell
        .\encrypt.bat input=test password=lodsve

        ----ENVIRONMENT-----------------

        Runtime: Azul Systems, Inc. OpenJDK 64-Bit Server VM 11.0.9.1+1-LTS



        ----ARGUMENTS-------------------

        input: test
        password: lodsve



        ----OUTPUT----------------------

        fKdBT8qImG9ZIuYO4reebA==
       ```
    4. 使用decrypt.bat进行解密
       ```shell
        .\decrypt.bat input=fKdBT8qImG9ZIuYO4reebA== password=lodsve

        ----ENVIRONMENT-----------------

        Runtime: Azul Systems, Inc. OpenJDK 64-Bit Server VM 11.0.9.1+1-LTS



        ----ARGUMENTS-------------------

        input: fKdBT8qImG9ZIuYO4reebA==
        password: lodsve



        ----OUTPUT----------------------

        test
       ```
