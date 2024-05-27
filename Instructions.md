# Partial Component Usage Instructions

## lodsve-boot-starter-encryption

1. Description
    ```text
    Encrypt sensitive information in the project using tools like base64 and jasypt.
    ```

2. Configuration Example
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
3. Configuration Explanation
    1. enabled: Enable the encryption component.
    2. base64 and jasypt represent the two encryption algorithms supported, with base64 not recommended.
    3. prefix、suffix
       ```text
       Mark the ciphertext in the configuration, the part wrapped by prefix and suffix is the ciphertext, for example:
       prefix：TEXT{
       suffix：}
       So, TEXT{tGwoovl9DkJhf45zJ2mRHg==} is the ciphertext, which needs to be extracted for decryption.
       ```
    4. Other configurations:
       ```yaml
       # Encryption key
       # Normally, the password should be passed as a startup variable or using placeholders, and passed as an environment variable, for example:
       # password: $ {LODSVE_PASSWORD}
       password: lodsve
       # Encryption algorithm
       algorithm: PBEWithMD5AndDES
       # Hash iterations to obtain the encryption key
       key-obtention-iterations: 1000
       # Size of the encryption pool to be created
       pool-size: 1
       # Name of the {@link java.security.Provider} implementation to obtain the encryption algorithm.
       providerName: xxx
       # Class name of the {@link java.security.Provider} implementation to obtain the encryption algorithm. Default is {@code null}.
       provider-class-name: xxx
       # Implementation of {@link org.jasypt.salt.SaltGenerator} to be used by the encryption.
       salt-generator-classname: org.jasypt.salt.RandomSaltGenerator
       # Implementation of {@link org.jasypt.iv.IvGenerator} to be used by the encryption. Default is {@code "org.jasypt.iv.NoIvGenerator"}.
       iv-generator-classname: org.jasypt.iv.NoIvGenerator
       # Specify the form of encoding for String output. {@code "base64"} or {@code "hexadecimal"}. Default is {@code "base64"}.
       string-output-type: base64
       ```
4. Using the Encryption Tool (for Windows)
    1. Download the tool package`jasypt-1.9.3-dist.zip`
    2. Navigate to the bin directory
    3. Use encrypt.bat for encryption
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
    4. Use decrypt.bat for decryption
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
