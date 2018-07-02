package com.archangel_design.babycentral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BabyschedullerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BabyschedullerApplication.class, args);
	}
}
