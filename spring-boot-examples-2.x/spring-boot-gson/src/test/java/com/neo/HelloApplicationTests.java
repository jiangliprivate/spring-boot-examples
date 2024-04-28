package com.neo;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/*@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println("Hello Spring Boot 2.0!");
	}

}*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HelloApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloApplicationTests {

	static final Logger logger = LoggerFactory.getLogger(HelloApplicationTests.class);

	@Autowired
	private Gson gson;  // 注入 Gson

	@Test
	public void test() {

		Map<String, Object> map = new LinkedHashMap<>();

		map.put("title", "spring 中文网");
		map.put("url", "https://springdoc.cn/");
		map.put("creatAt", LocalDateTime.now());

		String json = this.gson.toJson(map);

		logger.info(json);
	}
}
