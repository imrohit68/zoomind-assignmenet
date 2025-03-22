package com.example.ZoomindAssignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
@OpenAPIDefinition(
		info = @Info(
				title = "🔍 Test Case Management API",
				version = "1.0",
				description = "A RESTful API designed to efficiently manage software test cases. Supports CRUD operations, pagination, filtering, and caching for optimal performance.",
				contact = @Contact(
						name = "Rohit Singh",
						email = "sirohit328@gmail.com"
				)
		)
)
public class ZoomindAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZoomindAssignmentApplication.class, args);
	}
}
