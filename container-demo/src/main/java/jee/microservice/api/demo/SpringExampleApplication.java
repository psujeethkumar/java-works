package com.javatechie.spring.openshift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringExampleApplication {

	@GetMapping("/")
	public String welcome() {
		return "Welcome to java techie";
	}

	@GetMapping("/containerId")
	public String returnContainerId() {
		return "Welcome to java techie";
	}

	@GetMapping("/greet")
	public String greetConsumer() {
		return "Welcome ! I'm a microservice running in beaturiful world of Openshift platform";
	}
	
	


	public static void main(String[] args) {
		SpringApplication.run(SpringExampleApplication.class, args);
	}
}
