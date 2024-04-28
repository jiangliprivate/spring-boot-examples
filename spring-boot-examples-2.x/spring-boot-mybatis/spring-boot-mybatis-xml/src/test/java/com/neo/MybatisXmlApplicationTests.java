package com.neo;

import com.neo.mapper.FooMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisXmlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MybatisXmlApplicationTests {
	static final Logger logger = LoggerFactory.getLogger(MybatisXmlApplicationTests.class);
	@Autowired
	FooMapper fooMapper;

	@Test
	public void contextLoads() {
		System.out.println("hello world");
		LocalDateTime now = fooMapper.now();

		logger.info("NOW={}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
	}

}
