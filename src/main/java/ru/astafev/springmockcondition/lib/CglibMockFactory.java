package ru.astafev.springmockcondition.lib;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CglibMockFactory implements MockFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void registerNewBeanToMock(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

    @Override
    public Object createBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        Class<?> type = Utils.getBeanDefinitionClass(beanDefinition);
        Enhancer e = new Enhancer();
        e.setCallbackType(AlwayNullInterceptor.class);
        e.setSuperclass(type);

        Class<?> patchedType = e.createClass();
        Enhancer.registerCallbacks(patchedType, new Callback[]{
                new AlwayNullInterceptor(),
        });

        // Objenesis to avoid constructor being called
        Objenesis objenesis = new ObjenesisStd();
        var instantiator = objenesis.getInstantiatorOf(patchedType);
        return instantiator.newInstance();
    }

    public static class AlwayNullInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
            return null;
        }
    }
}
