package com.rite.products.convertrite.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(JwtSecurityConfig.class);
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	String issuerUri;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("entering configure Method####");
		//long startTime = System.currentTimeMillis();
		http.cors().and().csrf(csrf -> csrf.ignoringAntMatchers("/api/convertritecore/**")).authorizeRequests().antMatchers("/fallback").permitAll()
				.antMatchers(HttpMethod.POST, "/callback").permitAll()
				.antMatchers(HttpMethod.POST, "/synccloudinterfacedata").permitAll()
				.antMatchers(HttpMethod.POST, "/clouddataprocessingrequest").permitAll().antMatchers("/actuator/*")
				.permitAll();
				
				//.anyRequest().authenticated();
		
				
		
				
		/*
		 * http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
		 * .jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri))));
		 */
		 
		// http.cors().and().csrf().disable().authorizeRequests(authz ->
		// authz.anyRequest().authenticated()).oauth2ResourceServer(oauth2ResourceServer
		// ->oauth2ResourceServer.jwt(jwt
		// ->jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri))));
		log.debug("end of configure ###");
		// long endTime=System.currentTimeMillis();
		// long diff=endTime-startTime;
		// log.debug("TimeDiff##########"+diff);
	}

	@Override
	public void configure(WebSecurity web) {
		// long startTime=System.currentTimeMillis();
		// The new firewall is forced to overwrite the original
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
		// long endTime=System.currentTimeMillis();
		// long diff=endTime-startTime;
		// log.debug("TimeDiff ##"+diff);
	}

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}

}
