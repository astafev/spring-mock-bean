package ru.astafev.springmockcondition.lib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockOnProperty {
    String value();
    String trueIf() default "true";

    Class<? extends MethodInterceptor> methodInterceptor() default CglibMockFactory.AlwayNullInterceptor.class;
    // TODO is it possible to have Boot's Conditions inside?
}
