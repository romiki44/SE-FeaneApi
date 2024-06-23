package com.example.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name="Home Controller", description = "Home controller")
public class HomeController {
	
	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Return Hello world message")
	@Operation(summary = "Testing simple api")
	public String demo() {
		return "Hello, world!";
	}
	
	@GetMapping("/test")
	@SecurityRequirement(name = "feane-sec-api")
	//@Tag(name = "Test", description = "Test API url")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Return Hellooo")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@Operation(summary = "Testing secure api")
	public String test() {
		return "Helloooo and welcome from secure test-api!!!";
	}
}
