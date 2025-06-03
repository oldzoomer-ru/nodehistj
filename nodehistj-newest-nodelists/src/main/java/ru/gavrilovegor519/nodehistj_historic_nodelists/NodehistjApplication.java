package ru.gavrilovegor519.nodehistj_historic_nodelists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class NodehistjApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodehistjApplication.class, args);
    }
}
