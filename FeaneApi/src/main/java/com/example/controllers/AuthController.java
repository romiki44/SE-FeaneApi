package com.example.controllers;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.models.Account;
import com.example.payload.auth.AccountDto;
import com.example.payload.auth.AccountViewDto;
import com.example.payload.auth.PasswordDto;
import com.example.payload.auth.ProfileDto;
import com.example.payload.auth.TokenDto;
import com.example.payload.auth.UserLoginDto;
import com.example.service.AccountService;
import com.example.service.TokenService;
import com.example.utils.constants.AccountError;
import com.example.utils.constants.AccountSuccess;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/auth")
@Tag(name="Auth Controller", description = "Controller for account managment")
@Slf4j
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenService tokenService;	
	@Autowired
	private AccountService accountService;
		
	//takto cez constructor injection, alebo cez @Autowired
	//public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, AccountService accountService) {
	//	this.authenticationManager = authenticationManager;
	//	this.tokenService = tokenService;
	//	this.accountService=accountService;
	//}

	@PostMapping("/login")	
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Login user and get token")
	public ResponseEntity<TokenDto> login(@Valid @RequestBody UserLoginDto userLogin) throws AuthenticationException {
		try {
			Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));	
			TokenDto token=new TokenDto(tokenService.generateToken(authentication));
			log.debug("Token for user " + userLogin.getEmail() + " created successfully.");
			return ResponseEntity.ok(token);
		} 
		catch(Exception ex) {
			log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + ex.getMessage());
			return new ResponseEntity<TokenDto>(new TokenDto(null), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiResponse(responseCode = "400", description = "Please enter a valid email and password")
	@ApiResponse(responseCode = "201", description = "User account added")
	@Operation(summary = "Register new user")
	public ResponseEntity<String> createUser(@Valid @RequestBody AccountDto accountDto) {
		try {
			Account  account=new Account();
			account.setEmail(accountDto.getEmail());
			account.setPassword(accountDto.getPassword());
			//account.setRole("ROLE_USER");
			accountService.save(account);
			return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
		}
		catch (Exception e) {
			log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}					
	}
	
	@GetMapping(value = "/profile", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "List of users accounts")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@Operation(summary = "View users profile")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<ProfileDto> Profile(Authentication authentication) {
		String email=authentication.getName();
		Optional<Account> optAccount=accountService.findByEmail(email);
		if(optAccount.isPresent()) {
			Account account=optAccount.get();
			ProfileDto profileDto=new ProfileDto(account.getId(),email, account.getAuthorities());			
			return ResponseEntity.ok(profileDto);
		}		
		
		return new ResponseEntity<ProfileDto>(new ProfileDto(), HttpStatus.NOT_FOUND);
	}
	
	@PutMapping(value = "/profile/update-password", produces = "application/json", consumes = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Users password updated")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@Operation(summary = "Updating users paassword")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<AccountViewDto> UpdatePassword(@Valid @RequestBody PasswordDto passwordDto, Authentication authentication) {
		String email=authentication.getName();
		Optional<Account> optAccount=accountService.findByEmail(email);
		if(optAccount.isPresent()) {
			Account account=optAccount.get();
			account.setPassword(passwordDto.getPassword());
			accountService.save(account);
			AccountViewDto accountViewDto=new AccountViewDto(account.getId(), account.getEmail(), account.getAuthorities());
			return ResponseEntity.ok(accountViewDto);
		}		
		
		return new ResponseEntity<AccountViewDto>(new AccountViewDto(), HttpStatus.NOT_FOUND);
	}
	
	@DeleteMapping(value = "/profile/remove")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Removing user")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@Operation(summary = "Deleting users self-profile")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<String> RemoveUser(Authentication authentication) {
		String email=authentication.getName();
		Optional<Account> optAccount=accountService.findByEmail(email);
		if(optAccount.isPresent()) {
			//user maze sam seba!
			Account account=optAccount.get();
			accountService.deleteById(account.getId());
			return ResponseEntity.ok("Users profile removed succesfully");
		}		
				
		return new ResponseEntity<String>("Users profile not found", HttpStatus.NOT_FOUND);
	}
}
