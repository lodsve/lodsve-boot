# How to run it

该示例面向 Spring Boot 4.1.2 和 JDK 21。Druid 数据源与 SQL 过滤器仍可使用；Druid 监控页面和 Web 统计过滤器依赖旧版 `javax.servlet`，在 Boot 4 下暂未启用。

## Encrypt
1. download jasypt package

    [https://mvnrepository.com/artifact/org.jasypt/jasypt/1.9.3]()
2. run command via your terminal

   ```
   java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=[your DB url] password=[PASSWORD] algorithm=PBEWithMD5AndDES
   java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=[your DB username] password=[PASSWORD] algorithm=PBEWithMD5AndDES
   java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=[your DB password] password=[PASSWORD] algorithm=PBEWithMD5AndDES
   ```
3. update application.yml
4. add spring boot application VM options

   ```
   -Djasypt.encryptor.password=[PASSWORD]
   ```
