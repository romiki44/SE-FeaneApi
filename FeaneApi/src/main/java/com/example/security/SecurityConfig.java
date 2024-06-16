package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//import com.example.config.RsaKeyProperties;
import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
public class SecurityConfig {
	
	private static final String[] WHITELIST= {
			"/",
			"/token",
			"/hello",
			"/db-console/**",		
			"/swagger-ui/**",
			"/v3/api-docs/**"
	};

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    /*@Bean
    InMemoryUserDetailsManager users() {
		return new InMemoryUserDetailsManager(
				User.withUsername("kirk")
				.password("{noop}pwd123")
				.authorities("read")
				.build());
	}*/
    
    //ak mam vvgenerovane subory private.pem+public.pem cez openssl
    //plus musim mat v application class @EnableConfigurationProperties(RsaKeyProperties.class)
    //plus v aplication properties rsa.private-key= classpath:certs/private.pem, rsa.public-key= classpath:certs/public.pem
    //potom to chodi takto:
    /*@Autowired
	RsaKeyProperties rsaKeys;
    
    public SecurityConfig(RsaKeyProperties rsaKeys) {
    	this.rsaKeys=rsaKeys;
    }      
    
    @Bean
    JwtDecoder jwtDecoder() {
    	return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }
    
    @Bean
    JwtEncoder jwtEncoder() {
    	JWK jwk=new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
    	JWKSource<SecurityContext> jwks=new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk));
    	return new NimbusJwtEncoder(jwks);
    }*/
    
    private RSAKey rsaKey;
    
    //druha moznost, vygenerovat private a public rsa key v kode!
    //pozor, zmeni sa aj kod v jwtEncoder a JwtEncoder...
    @Bean
    JWKSource<SecurityContext> jwkSource() {
    	rsaKey=Jwks.generateRSA();
    	JWKSet jwkSet=new JWKSet(rsaKey);
    	return (jwkSelector, securityContext)->jwkSelector.select(jwkSet);
    }
    
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
    	return new NimbusJwtEncoder(jwks);
    }
    
    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
    	return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
    
    @Bean
    AuthenticationManager authManager(UserDetailsService userDetailsService) {
    	var authProvider=new DaoAuthenticationProvider();
    	authProvider.setPasswordEncoder(passwordEncoder());
    	authProvider.setUserDetailsService(userDetailsService);
    	return new ProviderManager(authProvider);
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests(autz->autz
				.requestMatchers(WHITELIST).permitAll()
				.requestMatchers("/test").authenticated()				
				)
		//.oauth2ResourceServer(oauth->oauth.jwt(jwt->jwt.decoder(JwtDecoder()));
		.oauth2ResourceServer(oauth->oauth.jwt(Customizer.withDefaults()))
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.csrf(c->c.disable())
		.headers(h->h.frameOptions(f->f.disable()));
					
		return http.build();
	}
}
