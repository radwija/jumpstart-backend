package com.radwija.jumpstartbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JumpstartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JumpstartBackendApplication.class, args);
	}

}
