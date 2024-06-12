package com.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
public class AccountController {
	
	@GetMapping("/hello")
	public String demo() {
		return "Hello, world!";
	}
	
	@GetMapping("/test")
	@SecurityRequirement(name = "feane-sec-api")
	@Tag(name = "Test", description = "Test API url")
	public String test() {
		return "This is only simple test api with security enabled...";
	}
}
