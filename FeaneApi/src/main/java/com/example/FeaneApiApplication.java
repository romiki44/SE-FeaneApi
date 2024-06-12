package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.config.RsaKeyProperties;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

//springdoc-openapi-starter-webmvc-ui
//http://localhost:8080/swagger-ui/index.html
//http://localhost:8080/v3/api-docs/

@SpringBootApplication
@SecurityScheme(name="feane-sec-api", scheme = "bearer", type = SecuritySchemeType.HTTP, in=SecuritySchemeIn.HEADER)
@EnableConfigurationProperties(RsaKeyProperties.class)  //toto iba ak rsaKey je @Autowired! (vygenerovane pem subory cez opnssl)
public class FeaneApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeaneApiApplication.class, args);
	}

}
