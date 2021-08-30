package ru.astafev.springmockcondition.lib;

import java.util.Arrays;

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
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;

@Component
public class MockUnwantedBeansPostProcessor implements
        BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final MockFactory mockFactory;

    private EvaluatorHelper conditionEvaluator;


    public MockUnwantedBeansPostProcessor() {
        this(new CglibMockFactory());
    }

    @Autowired
    public MockUnwantedBeansPostProcessor(MockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        conditionEvaluator = EvaluatorHelper.initConditionEvaluator(registry,
                applicationContext.getEnvironment());


        Arrays.stream(registry.getBeanDefinitionNames())
                .forEach(beanName -> processBean(registry, beanName));
    }

    private void processBean(BeanDefinitionRegistry registry, String beanName) {
        var beanDefinition = registry.getBeanDefinition(beanName);
        if (shouldBeMocked(beanDefinition)) {
            /*
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

    private void stupidBuild(BeanDefinitionRegistry registry, String beanName, Class<Object> beanClass) {
        registry.removeBeanDefinition(beanName);
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(
                                beanClass,
                                () -> mockFactory.createBean(beanName)
                        )
                        .getBeanDefinition());
    }

    private boolean shouldBeMocked(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof ScannedGenericBeanDefinition) {
            var metadata = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata();
            return conditionEvaluator.shouldSkip(metadata);
//            metadata.getAnnotation
        }
        /*return Optional.ofNullable(beanDefinition)
                // TODO maybe use ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().getAnnotations().get(MockOnProperty.class).getValue("value")
                .map(Utils::getBeanDefinitionClass)
                .map(clazz -> clazz.getAnnotation(MockOnProperty.class))
                .map(annotation -> {
                    var value = applicationContext.getEnvironment().getProperty(annotation.value());

                    return annotation.trueIf().equals(value);
                }).orElse(false);*/
        return false;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
