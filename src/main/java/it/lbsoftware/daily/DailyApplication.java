package it.lbsoftware.daily;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DailyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyApplication.class, args);
	}

}
