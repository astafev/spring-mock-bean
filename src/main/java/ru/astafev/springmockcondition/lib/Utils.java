package ru.astafev.springmockcondition.lib;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.config.BeanDefinition;

@UtilityClass
class Utils {
    @SneakyThrows
    Class<?> getBeanDefinitionClass(BeanDefinition beanDefinition) {
        if (beanDefinition.getBeanClassName() == null) {
            return null;
        }
        return Class.forName(beanDefinition.getBeanClassName());
    }
}
