package com.example.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
	info=@Info(
		title = "Feane API",
		version = "Version 1.0",
		contact = @Contact(
			name="StudyEasy",
			email = "admin@mail.com",
			url = "https://studyeasy.org"
		),
		license = @License(
			name = "Apache 2.0",
			url = "https://www.apache.org/license/LICENSE-2.0"
		),
		termsOfService = "https://studyeasy.org",
		description = "Spring BOOT RestFul API Demo"
		)
)


public class SwaggerConfig {

}
