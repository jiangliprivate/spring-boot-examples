package com.neo;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.neo.mapper")
// 指定 mapper 接口所在的包，以及 mapper 接口使用的注解。
@MapperScan(basePackages = "com.neo.mapper"/*, annotationClass = Mapper.class*/)//使用了annotationClass字段扫描就会更严格
public class MybatisXmlApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisXmlApplication.class, args);
	}
}
