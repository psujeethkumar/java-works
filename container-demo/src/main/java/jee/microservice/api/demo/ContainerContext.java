package jee.microservice.api.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ContainerContext {

	String containerId = "empty";

	public ContainerContext() {
		try {
			InetAddress id = InetAddress.getLocalHost();
			containerId = id.getHostName();
		} catch (UnknownHostException e) {
			containerId = e.getMessage();
		}
	}

	@GetMapping("/container")
	public String represent() {
		return "Hello! I'm container with name :  " + containerId;
	}


	@GetMapping("/{input}")
	public String introduce(@PathVariable String input) {
		return "UPDATE : <H3> Hello ! " + input + " I'm a container service living in the world of OCP. I have a name called " + containerId + "</H3>";
	}

	@GetMapping("/load")
	public String load() {
		;
		double load = 0.8;
		final long duration = 60000;

		long startTime = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() - startTime < duration) {
				if (System.currentTimeMillis() % 100 == 0) {
					Thread.sleep((long) Math.floor((1 - load) * 100));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return " Generated load & now check on my brother pods ";
	}
	

	public static void main(String[] args) {
		SpringApplication.run(ContainerContext.class, args);
	}

}
