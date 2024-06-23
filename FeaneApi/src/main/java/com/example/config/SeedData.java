package com.example.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.models.Account;
import com.example.service.AccountService;
import com.example.utils.constants.Authority;

@Component
public class SeedData implements CommandLineRunner {
	@Autowired
	private AccountService accountService;
	
	@Override
	public void run(String... args) throws Exception {			
		Account account01=new Account();
		account01.setEmail("tom.jones@mail.com");			
		account01.setPassword("pwd123");
		account01.setAuthorities(Authority.USER.toString());
		
		Account account02=new Account();
		account02.setEmail("johny.cash@mail.com");
		account02.setPassword("pwd123");
		account02.setAuthorities(Authority.USER.toString());
		
		Account account03=new Account();
		account03.setEmail("james.kirk@mail.com");
		account03.setPassword("pwd123");
		account03.setAuthorities(Authority.ADMIN.toString() + " " + Authority.USER.toString());			
		
		//https://dbeaver.io/ dbtools napr. aj pre H2
		//som si sam vymyslel...pozor, musi byt equals() nie == inac to nefunguje!
		List<Account> accounts= accountService.getAll();	
		if(!accounts.stream().anyMatch(a->a.getEmail().equals(account01.getEmail()))) {
			System.out.println("Added user with email: " + account01.getEmail());
			accountService.save(account01);
		}
		if(!accounts.stream().anyMatch(a->a.getEmail().equals(account02.getEmail()))) {
			System.out.println("Added user with email: " + account02.getEmail());
			accountService.save(account02);
		}
		if(!accounts.stream().anyMatch(a->a.getEmail().equals(account03.getEmail()))) {
			System.out.println("Added user with email: " + account03.getEmail());
			accountService.save(account03);
		}
	}

}
