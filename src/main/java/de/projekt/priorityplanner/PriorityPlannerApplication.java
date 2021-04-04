package de.projekt.priorityplanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@SpringBootApplication
public class PriorityPlannerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PriorityPlannerApplication.class, args);
	}

}
