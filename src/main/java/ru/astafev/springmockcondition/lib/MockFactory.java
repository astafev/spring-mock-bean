package ru.astafev.springmockcondition.lib;

import org.springframework.beans.factory.config.BeanDefinition;

public interface MockFactory {
    void registerNewBeanToMock(String name, BeanDefinition beanDefinition);

    <T> T createBean(Class<T> type);
}
