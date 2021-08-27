package ru.astafev.springmockcondition.lib;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Value;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CglibMockFactory implements MockFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public CglibMockFactory() {
        System.out.println("asdf");
    }

    @Override
    public void registerNewBeanToMock(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createBean(Class<T> type) {
        Enhancer e = new Enhancer();
        e.setCallbackType(AlwayNullInterceptor.class);
        e.setSuperclass(type);
        type = e.createClass();
        Enhancer.registerCallbacks(type, new Callback[]{
                new AlwayNullInterceptor(),
        });

        // Objenesis to avoid constructor being called
        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator<T> instantiator = objenesis.getInstantiatorOf(type);
        return instantiator.newInstance();
    }

    public static class AlwayNullInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
            return null;
        }
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
