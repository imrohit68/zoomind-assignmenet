package com.example.ZoomindAssignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
@OpenAPIDefinition(
		info = @Info(
				title = "Test Case Management API",
				version = "1.0",
				description = "This API allows users to manage test cases, including creating, retrieving, updating, and deleting test cases."
		)
)
public class ZoomindAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZoomindAssignmentApplication.class, args);
	}
}
