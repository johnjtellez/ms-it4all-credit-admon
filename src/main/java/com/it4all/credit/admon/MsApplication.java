package com.it4all.credit.admon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = { "com.it4all.credit.admon.data.model" })
@EnableJpaRepositories(basePackages = { "com.it4all.credit.admon.data.model.repository" })
@ComponentScan (basePackages = { "com.it4all.credit.admon.controller",
		"com.it4all.credit.admon.service",
		"com.it4all.credit.admon.config",
		"com.it4all.credit.admon.data.mapper",
		"com.it4all.credit.admon.jwt"})
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableAutoConfiguration
public class MsApplication{

	public static void main(String[] args) {
		SpringApplication.run(MsApplication.class, args);
	}

}
