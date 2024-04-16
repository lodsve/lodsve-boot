# How to use

## 关于路径

1. spring boot actuator默认路径是`/actuator`
2. 如果想修改，则按照以下配置

    ```yaml
    management:
      endpoints:
        web:
          base-path: /actuator
    ```

## 关于endpoint

1. spring boot自身提供了很多类似端点来获取系统内部信息，例如：

    ```text
    beans
    caches
    health
    info
    conditions
    configprops
    env
    loggers
    heapdump
    threaddump
    scheduledtasks
    mapping
    ```

2. 这些端点完全暴露出来，存在安全风险
3. 所以spring boot默认只开放`health`和`info`两个，如果要配置其他端点，如下配置：
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include:
             - beans
             - caches
             # - ...(都在上表中)
     ```

## 关于`lodsve-boot`提供的端点

1. versions: 查看系统关键组件版本号，一般指spring boot、springframework、lodsve-boot
2. dependencies：查看整个代码所有的依赖组件及其版本号
3. enums: 查看系统中定义的所有枚举（实现特定接口`com.lodsve.boot.bean.Codeable`）
4. 如果要用，则将这些key按照spring boot的方式配置上去即可
