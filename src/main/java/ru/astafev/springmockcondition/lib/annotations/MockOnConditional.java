package ru.astafev.springmockcondition.lib.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cglib.proxy.MethodInterceptor;
import ru.astafev.springmockcondition.lib.CglibMockFactory;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockOnConditional {
    Class<? extends MethodInterceptor> methodInterceptor() default CglibMockFactory.AlwayNullInterceptor.class;


    ConditionalOnBean[] onBean() default {};
    ConditionalOnClass[] onClass() default {};
    ConditionalOnProperty[] onProperty() default {};
    // TODO etc
}
