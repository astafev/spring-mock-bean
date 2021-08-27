package ru.astafev.springmockcondition.test1;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.astafev.springmockcondition.lib.MockOnProperty;

@MockOnProperty("test1.mock")
@Component("doNotInstantiate")
public class DoNotInstantiate implements Interface1 {
    public DoNotInstantiate() {
        throw new AssertionError();
    }

    @Override
    public Object execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getArgument(T o) {
        return o;
    }
}
