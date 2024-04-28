package com.neo.hello;

import com.neo.bean.*;
import com.neo.controller.HelloController;
import com.neo.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 * <p>
 * SpringBoot启动类
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-09-28 14:49
 */
@ComponentScan({"com.neo.bean", "com.neo.controller"})
@SpringBootApplication
//1.SpringBootApplication中的ComponentScan自动扫描同级和之下的目录,不扫描父目录
//2.ComponentScan一旦指定,默认规则失效
public class Application {
    public static int i = 0;

    public static void main(String[] args) {
        System.out.println("start Hello, World! i = " + i++);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        SpringUtil.setApplicationContext(applicationContext);
        try {
            System.out.println(applicationContext.getBean(ControllerExample.class));
            System.out.println(applicationContext.getBean(ServiceExample.class));
            System.out.println(applicationContext.getBean(RepositoryExample.class));
            System.out.println(applicationContext.getBean(ComponentExample.class));
            System.out.println(applicationContext.getBean(CustomComponentExample.class));
            System.out.println(applicationContext.getBean(HelloController.class));
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("end Hello, World!");
    }


}
