package com.thomas.personal.fpl.funfpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FunfplApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunfplApplication.class, args);
	}

}
