package ru.astafev.springmockcondition.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Value;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CglibMockFactory implements BeanFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public Object createBean(BeanDefinition beanDefinition) {
        return Enhancer.create(Utils.getBeanDefinitionClass(beanDefinition),
                (FixedValue) () -> null);
    }

    public void newBeanToMock(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    public <T> T createBean(Class<T> type) {
        return (T) Enhancer.create(type,
                (FixedValue) () -> null);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return createBean(Object.class);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        if (!beanDefinitionMap.containsKey(name))
            return null;
        return createBean(requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        // we don't care about the args
        return getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return createBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        // we don't care about the args
        return getBean(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return new SingletonBeanProvider<>(getBean(requiredType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return new SingletonBeanProvider<T>(getBean((Class<T>) requiredType.getType()));
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return true;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return false;
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return null;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Value
    private static class SingletonBeanProvider<T> implements ObjectProvider<T> {
        T object;

        @Override
        public T getObject(Object... args) throws BeansException {
            return object;
        }

        @Override
        public T getIfAvailable() throws BeansException {
            return object;
        }

        @Override
        public T getIfUnique() throws BeansException {
            return object;
        }

        @Override
        public T getObject() throws BeansException {
            return object;
        }
    }
}
