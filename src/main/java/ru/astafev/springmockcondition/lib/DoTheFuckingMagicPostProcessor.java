package ru.astafev.springmockcondition.lib;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DoTheFuckingMagicPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor,
        BeanFactoryAware,
        ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final Map<String, BeanDefinition> beans = new LinkedHashMap<>();

    private final CglibMockFactory mockFactory;


    public DoTheFuckingMagicPostProcessor() {
        mockFactory = new CglibMockFactory();
    }

    @Autowired
    public DoTheFuckingMagicPostProcessor(CglibMockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Arrays.stream(registry.getBeanDefinitionNames())
                .forEach(beanName -> {
                            var beanDefinition = registry.getBeanDefinition(beanName);
                            if (toMock(beanDefinition)) {
                                registry.removeBeanDefinition(beanName);
                                @SuppressWarnings("unchcked")
                                Class<Object> beanClass = (Class<Object>) Utils.getBeanDefinitionClass(beanDefinition);
                                registry.registerBeanDefinition(beanName,
                                        BeanDefinitionBuilder.genericBeanDefinition(beanClass,
                                                () -> {
                                                    return mockFactory.createBean(beanClass);
                                                }).getBeanDefinition());
                                mockFactory.newBeanToMock(beanName, beanDefinition);


//                                registry.registerBeanDefinition(beanName, BeanDefinitionBuilder.);
                                // TODO register it back here?
                                // TODO check how spring creates factory when needed (it should know the required interface)
                                beans.put(beanName, beanDefinition);
                            }
                        }
                );
    }

    private boolean toMock(BeanDefinition beanDefinition) {
        return Optional.ofNullable(beanDefinition)
                // TODO maybe use ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().getAnnotations().get(MockOnProperty.class).getValue("value")
                .map(Utils::getBeanDefinitionClass)
                .map(clazz -> clazz.getAnnotation(MockOnProperty.class))
                .map(annotation -> {
                    var value = applicationContext.getEnvironment().getProperty(annotation.value());

                    return annotation.trueIf().equals(value);
                }).orElse(false);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.setParentBeanFactory(this.mockFactory);
       /* // spring doesn't use multiple threads for container initialization, doesn't it?
        beans.forEach((key, value) -> {
            Object o = mockFactory.createBean(value);
            beanFactory.registerSingleton(key, o);
        });
        beans.clear();*/
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        ((AbstractBeanFactory) beanFactory).setParentBeanFactory(this.mockFactory);
    }
}
