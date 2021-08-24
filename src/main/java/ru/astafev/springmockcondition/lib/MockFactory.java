package ru.astafev.springmockcondition.lib;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MockFactory {

    public <T> T createBean(BeanDefinition beanDefinition) {
        // TODO
        return null;
    }
}
