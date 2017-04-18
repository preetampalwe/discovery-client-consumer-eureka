package com.mycompany;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class DiscoveryClientConsumerEurekaApplication {

	private static Logger logger = LoggerFactory.getLogger(DiscoveryClientConsumerEurekaApplication.class);

	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		logger.debug("\n\n\nAll Environment Variables - " + System.getenv());
		logger.debug("\n\n\nAll System Properties - " + System.getProperties());

		SpringApplication.run(DiscoveryClientConsumerEurekaApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RequestMapping("/hello")
	public String hello(@RequestParam(value = "salutation", defaultValue = "Hi") String salutation,
			@RequestParam(value = "name", defaultValue = "Rob") String name) {
		logger.debug("Received -- " + salutation + " and " + name);
		URI uri = UriComponentsBuilder.fromUriString("http://discovery-client-eureka/greeting")
				.queryParam("salutation", salutation).queryParam("name", name).build().toUri();

		Greeting greeting = this.restTemplate.getForObject(uri, Greeting.class);
		return greeting.getMessage();
	}

	public static class Greeting {
		private String message;

		@JsonCreator
		public Greeting(@JsonProperty("message") String message) {
			this.message = message;
		}

		public String getMessage() {
			return this.message;
		}
	}
}
