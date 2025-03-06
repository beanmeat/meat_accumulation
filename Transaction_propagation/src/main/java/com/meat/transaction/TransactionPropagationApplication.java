package com.meat.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.meat.transaction.mapper")
public class TransactionPropagationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionPropagationApplication.class, args);
	}

}
