package ru.astafev.springmockcondition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.astafev.springmockcondition.test1.DependantClass;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "test1.mock=true")
class SpringMockConditionApplicationTests {

	@Autowired
	private DependantClass dependantClass;

	@Test
	void contextLoads() {
		assertNotNull(dependantClass);
	}

}
