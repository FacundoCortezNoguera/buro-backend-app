package ar.buro.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BuroBackendAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuroBackendAppApplication.class, args);
	}

}
