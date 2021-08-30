package ru.astafev.springmockcondition.lib;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationSelector;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import ru.astafev.springmockcondition.lib.annotations.MockOnConditional;

public record EvaluatorHelper(Object conditionEvaluator, Method shouldSkipMethod) {

    public boolean shouldSkip(AnnotationMetadata metadata) {
        try {
            MergedAnnotation<MockOnConditional> annotation = metadata.getAnnotations().get(MockOnConditional.class);
            if (!annotation.isPresent())  return false;

            MergedAnnotation<ConditionalOnProperty>[] onProperties =
                    annotation.getAnnotationArray("onProperty", ConditionalOnProperty.class);

            // TODO
            AnnotatedTypeMetadata metadata1 = new AnnotatedTypeMetadata() {
                @Override
                public MergedAnnotations getAnnotations() {
                    return new MergedAnnotations() {
                        @Override
                        public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
                            return false;
                        }

                        @Override
                        public boolean isPresent(String annotationType) {
                            return false;
                        }

                        @Override
                        public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
                            return false;
                        }

                        @Override
                        public boolean isDirectlyPresent(String annotationType) {
                            return false;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, Predicate<? super MergedAnnotation<A>> predicate) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(String annotationType) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
                            return null;
                        }

                        @Override
                        public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
                            return null;
                        }

                        @Override
                        public Stream<MergedAnnotation<Annotation>> stream() {
                            return null;
                        }

                        @Override
                        public Iterator<MergedAnnotation<Annotation>> iterator() {
                            return null;
                        }
                    };
                }
            };
            return (Boolean) shouldSkipMethod.invoke(conditionEvaluator, (AnnotatedTypeMetadata) metadata);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static EvaluatorHelper initConditionEvaluator(BeanDefinitionRegistry registry, Environment env) {
        try {
            Class<?> conditionEvaluatorClazz;
            conditionEvaluatorClazz = Class.forName("org.springframework.context.annotation.ConditionEvaluator");
            Constructor<?> constructor = conditionEvaluatorClazz.getConstructor(BeanDefinitionRegistry.class,
                    Environment.class, ResourceLoader.class);
            constructor.setAccessible(true);
            Object conditionEvaluator = constructor.newInstance(registry, env,
                    null);
            Method shouldSkipMethod = conditionEvaluatorClazz.getMethod("shouldSkip", AnnotatedTypeMetadata.class);
            shouldSkipMethod.setAccessible(true);

            return new EvaluatorHelper(conditionEvaluator, shouldSkipMethod);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
