package com.nhnacademy.auth.common.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nhnacademy.auth.common.properties.JwtProperties;

import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {

	private final JwtProperties jwtProperties;

	public JwtConfig(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	@Bean
	public SecretKey accessSecretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes());
	}

	@Bean
	public SecretKey refreshSecretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes());
	}

	@Bean
	public long accessExpiration() {
		return jwtProperties.getAccessExpiration();
	}

	@Bean
	public long refreshExpiration() {
		return jwtProperties.getRefreshExpiration();
	}

}
