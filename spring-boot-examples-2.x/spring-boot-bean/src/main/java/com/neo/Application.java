package com.neo;

import com.neo.bean.*;
import com.neo.controller.HelloWorldController;
import com.neo.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
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
		System.out.println("end Hello, World!");
	}
}
