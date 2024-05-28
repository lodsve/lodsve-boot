package com.lodsve.boot.example.config;

import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.component.validator.handler.AbstractValidateHandler;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Component
public class EqualsHandler extends AbstractValidateHandler<Equals> {
    @Override
    protected ErrorMessage handle(Equals annotation, Object value) {
        return getMessage(Equals.class, getClass(), "等于异常", null != value && value.equals(annotation.expect()));
    }
}
