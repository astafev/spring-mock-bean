package ru.astafev.springmockcondition.test1;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DependantClass {
    @Autowired
    Interface1 interface1;

    @PostConstruct
    public void checkThatTheDependencyIsInited() {
        interface1.execute();
    }
    public String getHello() {
        return interface1.getArgument("Hello");
    }
}
