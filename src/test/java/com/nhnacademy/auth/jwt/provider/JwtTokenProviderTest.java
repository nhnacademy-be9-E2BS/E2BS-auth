package com.nhnacademy.auth.jwt.provider;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;
	private Key secretKey;
	private long expirationMillis;

	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider();
		secretKey = Keys.hmacShaKeyFor("MySuperSecretKeyForJwtTesting1234567890".getBytes());
		expirationMillis = 1000 * 60 * 15; // 15 minutes
	}

	@Test
	@DisplayName("Access Token이 Claim을 가지고 있는지 테스트")
	void provideAccessTokenContainClaimTest() throws Exception {

		// Given
		User user = new User("nhn1", "1234", List.of(new SimpleGrantedAuthority("ROLE_USER")));

		// When
		String token = jwtTokenProvider.provideAccessToken(secretKey, expirationMillis, user);

		// Then
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();

		Assertions.assertThat(claims.getSubject()).isEqualTo("nhn1");
		Assertions.assertThat(claims.get("MemberId")).isEqualTo("nhn1");
		Assertions.assertThat(claims.getExpiration()).isAfter(new Date());
	}

	@Test
	@DisplayName("Refresh Token에 최소한의 정보만 담기는지 테스트")
	void provideRefreshTokenContainSubjectTest() throws Exception {

		// Given
		User user = new User("nhn1", "1234", List.of(new SimpleGrantedAuthority("ROLE_USER")));

		// When
		String refreshToken = jwtTokenProvider.provideRefreshToken(secretKey, expirationMillis, user);

		// Then
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(refreshToken)
			.getBody();

		Assertions.assertThat(claims.getSubject()).isEqualTo("nhn1");
		Assertions.assertThat(claims.get("Role")).isNull();
		Assertions.assertThat(claims.get("Identifier")).isNull();

	}

}