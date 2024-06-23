package com.example.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.models.Account;
import com.example.payload.auth.AccountViewDto;
import com.example.payload.auth.AuthoritiesDto;
import com.example.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name="Users Controller", description = "Controller for users managment")
public class UsersController {
	@Autowired
	private AccountService accountService;
	
	@GetMapping(value = "", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "List of users accounts")
	@ApiResponse(responseCode = "401", description = "Token is required")
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@Operation(summary = "Get all users")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<List<AccountViewDto>> Users() {
		List<AccountViewDto> accountViews=accountService.getAll()
				.stream()
				.map(v->new AccountViewDto(v.getId(), v.getEmail(), v.getAuthorities()))
				.toList();
		
		return ResponseEntity.ok(accountViews);
	}
	
	@PutMapping(value = "/{user_id}/update-auth", produces = "application/json", consumes = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Users authorities updated")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@Operation(summary = "Updating users authorities")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<AccountViewDto> UpdateAuth(@PathVariable Long user_id, @Valid @RequestBody AuthoritiesDto authoritiesDto) {		
		Optional<Account> optAccount=accountService.findById(user_id);
		if(optAccount.isPresent()) {
			Account account=optAccount.get();
			account.setAuthorities(authoritiesDto.getAuthorities());
			accountService.save(account);
			AccountViewDto accountViewDto=new AccountViewDto(account.getId(), account.getEmail(), account.getAuthorities());
			return ResponseEntity.ok(accountViewDto);
		}		
				
		return new ResponseEntity<AccountViewDto>(new AccountViewDto(), HttpStatus.NOT_FOUND);
	}
	
	@DeleteMapping(value = "/{user_id}/remove")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Removing user")
	@ApiResponse(responseCode = "401", description = "Token is required")	
	@ApiResponse(responseCode = "403", description = "Insuficient privilliges")
	@ApiResponse(responseCode = "404", description = "User not found")
	@Operation(summary = "Deleting any user")
	@SecurityRequirement(name = "feane-sec-api")
	public ResponseEntity<String> RemoveUser(@PathVariable Long user_id) {		
		Optional<Account> optAccount=accountService.findById(user_id);
		if(optAccount.isPresent()) {
			//moze sice vymazat sam seba, ale to nebudem teraz asi riesit....
			accountService.deleteById(user_id);
			return ResponseEntity.ok("User removed succesfully");
		}		
				
		return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
	}
}
