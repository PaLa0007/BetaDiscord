package com.beta_discord_api.betadiscordapi;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan
public class BetaDiscordApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(BetaDiscordApiApplication.class, args);
	}
}


