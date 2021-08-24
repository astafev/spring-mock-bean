package ru.astafev.springmockcondition.test1;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import ru.astafev.springmockcondition.lib.MockOnProperty;

@MockOnProperty("test1.mock")
public class DoNotInstantiate implements Interface1 {
    public DoNotInstantiate() {
        throw new AssertionError();
    }

    @Override
    public Object execute() {
        throw new UnsupportedOperationException();
    }
}
