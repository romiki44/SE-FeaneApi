package com.example.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
	@Email
	@Schema(description = "Email address", example = "tom.jones@mail.com",  requiredMode = RequiredMode.REQUIRED)
	private String email;
	
	@Size(min=5, max=20)
	@Schema(description = "Password", example = "pwd123", requiredMode = RequiredMode.REQUIRED, minLength = 5, maxLength = 20)
	private String password;	
}



