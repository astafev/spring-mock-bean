package ru.astafev.springmockcondition.test1;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

public class DependantClass {
    @Autowired
    Interface1 interface1;

    @PostConstruct
    public void checkThatTheDependencyIsInited() {
        interface1.execute();
    }
}
