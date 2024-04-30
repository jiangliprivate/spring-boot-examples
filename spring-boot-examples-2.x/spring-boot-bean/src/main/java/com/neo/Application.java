package com.neo;

import com.neo.bean.*;
import com.neo.controller.HelloWorldController;
import com.neo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j// 使用lombok,起到了便捷的作用
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = 	SpringApplication.run(Application.class, args);
		SpringUtil.setApplicationContext(applicationContext);
		System.out.println(applicationContext.getBean(ControllerExample.class));
		System.out.println(applicationContext.getBean(ServiceExample.class));
		System.out.println(applicationContext.getBean(RepositoryExample.class));
		System.out.println(applicationContext.getBean(ComponentExample.class));
		System.out.println(applicationContext.getBean(CustomComponentExample.class));
		System.out.println(applicationContext.getBean(HelloWorldController.class));
		log.info(applicationContext.getBean(ControllerExample.class).toString());
		log.info(applicationContext.getBean(ServiceExample.class).toString());
		log.info(applicationContext.getBean(RepositoryExample.class).toString());
		log.info(applicationContext.getBean(ComponentExample.class).toString());
		log.info(applicationContext.getBean(CustomComponentExample.class).toString());
		log.info(applicationContext.getBean(HelloWorldController.class).toString());
		System.out.println("end Hello, World!");
	}
}
