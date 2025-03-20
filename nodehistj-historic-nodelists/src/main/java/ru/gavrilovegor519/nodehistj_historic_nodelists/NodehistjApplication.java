package ru.gavrilovegor519.nodehistj_historic_nodelists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.gavrilovegor519.nodehistj_historic_nodelists.util.ClearRedisCache;

@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class NodehistjApplication {
	@Autowired
	private ClearRedisCache clearRedisCache;

	public static void main(String[] args) {
		SpringApplication.run(NodehistjApplication.class, args);
	}

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		clearRedisCache.clearAllCache();
	}
}
