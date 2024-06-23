package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.models.Account;
import com.example.repositories.AccountRepository;
import com.example.utils.constants.Authority;

@Service
public class AccountService implements UserDetailsService {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		if(account.getAuthorities()==null) {
			account.setAuthorities(Authority.USER.toString());
		}
		return accountRepository.save(account);
	}
	
	public List<Account> getAll() {
		return accountRepository.findAll();
	}
	
	public Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}
	
	public Optional<Account> findById(Long id) {
		return accountRepository.findById(id);
	}
	
	public void deleteById(Long id) {
		accountRepository.deleteById(id);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Account> optAccount=accountRepository.findByEmail(email);
		
		//account not found
		if(!optAccount.isPresent()) {
			throw new UsernameNotFoundException("Account with email '" + email + "'' not found");
		}
		
		Account account=optAccount.get();
		List<GrantedAuthority> grantedAuthorities=new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(account.getAuthorities()));
		
		return new User(account.getEmail(), account.getPassword(), grantedAuthorities);
	}
}
