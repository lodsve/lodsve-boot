package com.lodsve.boot.rdbms.dynamic;

import com.lodsve.boot.rdbms.annotations.SwitchDataSource;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * aop动态切换数据源.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2017/12/14 下午6:16
 */
public class DynamicDataSourceAspect {
    @Around("@annotation(com.lodsve.boot.rdbms.annotations.SwitchDataSource)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String dataSource = StringUtils.EMPTY;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(SwitchDataSource.class)) {
            SwitchDataSource annotation = method.getAnnotation(SwitchDataSource.class);
            // 取出注解中的数据源名
            dataSource = annotation.value();
        }

        try (DataSourceHolder dsh = DataSourceHolder.getInstance()) {
            dsh.set(dataSource);
            return point.proceed();
        }
    }
}
