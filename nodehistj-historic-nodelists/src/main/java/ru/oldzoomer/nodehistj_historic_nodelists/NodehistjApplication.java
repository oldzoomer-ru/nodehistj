package ru.oldzoomer.nodehistj_historic_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class NodehistjApplication {

	public static void main(String[] args) {
		SpringApplication.run(NodehistjApplication.class, args);
	}
}
