package ru.astafev.springmockcondition.lib;

import java.util.Arrays;
import java.util.Optional;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MockUnwantedBeansPostProcessor implements
        BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final MockFactory mockFactory;


    public MockUnwantedBeansPostProcessor() {
        this(new CglibMockFactory());
    }
    @Autowired
    public MockUnwantedBeansPostProcessor(MockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Arrays.stream(registry.getBeanDefinitionNames())
                .forEach(beanName -> {
                            var beanDefinition = registry.getBeanDefinition(beanName);
                            if (shouldBeMocked(beanDefinition)) {
                                /**
                                 TODO instead of removing/adding a new bean definition.
                                 Just set factory method and factory name and generate factory dynamically (just...)
                                 {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#instantiateUsingFactoryMethod} */
//                                beanDefinition.setFactoryBeanName("asdf");
//                                beanDefinition.setFactoryMethodName("asdf");
                                @SuppressWarnings("unchecked")
                                Class<Object> beanClass = (Class<Object>) Utils.getBeanDefinitionClass(beanDefinition);

                                // TODO
                                stupidBuild(registry, beanName, beanClass);
                                mockFactory.registerNewBeanToMock(beanName, beanDefinition);
                            }
                        }
                );
    }

    private void stupidBuild(BeanDefinitionRegistry registry, String beanName, Class<Object> beanClass) {
        registry.removeBeanDefinition(beanName);
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(beanClass,
                        () -> {
                            return mockFactory.createBean(beanClass);
                        }).getBeanDefinition());
    }

    private boolean shouldBeMocked(BeanDefinition beanDefinition) {
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

    }
}
