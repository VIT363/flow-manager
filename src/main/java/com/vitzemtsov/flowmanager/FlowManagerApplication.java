package com.vitzemtsov.flowmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
public class FlowManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowManagerApplication.class, args);
	}

}
