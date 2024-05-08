package com.neo;

import com.neo.cache.RedisDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j// 使用lombok,起到了便捷的作用
public class RedisApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(RedisApplication.class, args);
		log.info(applicationContext.getBean(RedisDao.class).toString());
		log.info(applicationContext.getBean("stringRedisTemplate").toString());
	}
}
