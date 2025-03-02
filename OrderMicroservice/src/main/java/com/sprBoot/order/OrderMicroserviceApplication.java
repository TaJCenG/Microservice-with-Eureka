package com.sprBoot.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.netflix.discovery.EurekaClient;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class OrderMicroserviceApplication {
	@Autowired
	private EurekaClient eurekaClient;
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderMicroserviceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(OrderMicroserviceApplication.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Forcing Eureka registry fetch...");
	    eurekaClient.getApplications();  // Autowire EurekaClient
	}
}
