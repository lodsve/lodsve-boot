# How to run it

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
