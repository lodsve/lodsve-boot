package com.lodsve.boot.example.config;

import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.component.validator.exception.ExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Component
public class ValidatorErrorHandler extends ExceptionHandler {
    @Override
    public String getMessage(List<ErrorMessage> messages) {
        List<String> messageList = new ArrayList<>(messages.size());
        for (ErrorMessage em : messages) {
            messageList.add(em.getMessage());
        }

        return StringUtils.join(messageList, "####");
    }
}
