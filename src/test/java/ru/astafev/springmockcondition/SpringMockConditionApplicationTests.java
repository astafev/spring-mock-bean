package ru.astafev.springmockcondition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.astafev.springmockcondition.test1.DependantClass;
import ru.astafev.springmockcondition.test2.NoDefaultConstructoClass;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(properties = {
		"test1.mock=true",
		"test2.mock=true",
})
public class SpringMockConditionApplicationTests {

	@Autowired
	private DependantClass dependantClass;

	@Autowired
	private NoDefaultConstructoClass noDefaultConstructorClass;

	@Test
	void contextLoads() {
		assertNotNull(dependantClass);
		assertNotNull(noDefaultConstructorClass);
	}

	@Test
	void methodGetsReplaced() {
		assertNull(dependantClass.getHello());
	}

}
