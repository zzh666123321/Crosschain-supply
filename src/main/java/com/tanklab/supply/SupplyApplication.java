package com.tanklab.supply;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@ComponentScan("com.tanklab.supply.config")
@MapperScan("com.tanklab.supply.mapper")
public class SupplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplyApplication.class, args);
	}

}
