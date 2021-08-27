package ru.astafev.springmockcondition.test2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.astafev.springmockcondition.lib.MockOnProperty;

/**
 * Objenesis can create a class even without NoArgsConstructor
 */
@MockOnProperty("test1.mock")
@Component("noDefaultConstructor")
public class NoDefaultConstructoClass {
    public NoDefaultConstructoClass(@Value("test2.mock") String value) {
        throw new IllegalArgumentException(value);
    }
}
