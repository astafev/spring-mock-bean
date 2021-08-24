package ru.astafev.springmockcondition;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "test1.mock=true")
class SpringMockConditionApplicationTests {

	@Test
	void contextLoads() {
	}

}
