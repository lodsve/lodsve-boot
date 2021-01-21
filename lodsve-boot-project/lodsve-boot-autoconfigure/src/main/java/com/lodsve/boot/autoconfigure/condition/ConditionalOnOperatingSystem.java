package com.lodsve.boot.autoconfigure.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 根据操作系统的类型判断.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnOperatingSystemCondition.class)
public @interface ConditionalOnOperatingSystem {
    /**
     * 操作系统类型
     *
     * @return 操作系统类型
     */
    OperatingSystem value();

    /**
     * 操作系统枚举.
     */
    enum OperatingSystem {
        /**
         * windows
         */
        Windows(new String[]{"windows"}),
        /**
         * linux
         */
        Linux(new String[]{"linux"}),
        /**
         * mac
         */
        Mac(new String[]{"mac", "os"});

        private final String[] keyword;

        OperatingSystem(String[] keyword) {
            this.keyword = keyword;
        }

        public String[] getKeyword() {
            return keyword;
        }
    }
}
