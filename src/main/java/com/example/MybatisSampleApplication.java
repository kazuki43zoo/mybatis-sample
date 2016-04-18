package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource("classpath:/applicationContext.xml")
public class MybatisSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisSampleApplication.class, args);
	}
}
