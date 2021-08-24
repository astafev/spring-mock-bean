package ru.astafev.springmockcondition.lib;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DoTheFuckingMagicPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor,
        ApplicationContextAware {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Setter
    private ApplicationContext applicationContext;

    private final Map<String, BeanDefinition> beans = new LinkedHashMap<>();

    @Autowired
    private MockFactory mockFactory;

    public DoTheFuckingMagicPostProcessor() {
    }

    @Autowired
    public DoTheFuckingMagicPostProcessor(MockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Arrays.stream(registry.getBeanDefinitionNames())
                .forEach(beanName -> {
                            var beanDefinition = registry.getBeanDefinition(beanName);
                            if (toMock(beanDefinition)) {
                                registry.removeBeanDefinition(beanName);
                                // TODO register it back here?
                                // TODO check how spring creates factory when needed (it should know the requried interface)
                                beans.put(beanName, beanDefinition);
                            }
                        }
                );
    }

    private boolean toMock(BeanDefinition beanDefinition) {
        return Optional.ofNullable(beanDefinition)
                // TODO maybe use ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().getAnnotations().get(MockOnProperty.class).getValue("value")
                .map(BeanDefinition::getBeanClassName)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).map(clazz -> clazz.getAnnotation(MockOnProperty.class))
                .map(annotation -> {
                    var value = applicationContext.getEnvironment().getProperty(annotation.value());
                    return annotation.trueIf().equals(value);
                }).orElse(false);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // spring doesn't use multiple threads for container initialization, doesn't it?
        beans.forEach((key, value) -> {
            Object o = mockFactory.createBean(value);
            beanFactory.registerSingleton(key, o);
        });
        beans.clear();
    }
}
