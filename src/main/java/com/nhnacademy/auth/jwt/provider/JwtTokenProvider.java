package com.nhnacademy.auth.jwt.provider;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT 토큰 발급
 */
@Component
public class JwtTokenProvider {

	/**
	 * Access Token 발급하는 메서드
	 */
	public String provideAccessToken(final Key AccessSECRET, final long AccessEXPIRATION, User user) {
		long now = System.currentTimeMillis();

		return Jwts.builder()
			.setHeader(createHeader())
			.setClaims(createClaims(user))
			.setSubject(String.valueOf(user.getUsername()))
			.setExpiration(new Date(now + AccessEXPIRATION))
			.signWith(AccessSECRET, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * Refresh Token 발급하는 메서드
	 */
	public String provideRefreshToken(final Key RefreshSECRET, final long RefreshEXPIRATION, User user) {
		long now = System.currentTimeMillis();

		return Jwts.builder()
			.setHeader(createHeader())
			.setSubject(user.getUsername())
			.setExpiration(new Date(now + RefreshEXPIRATION))
			.signWith(RefreshSECRET, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * Header, Payload, Signature 중 JWT 토큰의 Header를 만드는 메서드
	 */
	private Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("typ", "JWT"); // 토큰의 타입이 JWT라는 것을 명시
		header.put("alg", "HS256"); // 토큰을 서명할 때 사용하는 알고리즘 (HMAC-SHA256)

		return header;
	}

	/**
	 * Header, Payload, Signature 중 JWT 토큰의 Payload를 만드는 메서드
	 * Claim에는 토큰을 발급받은 주체에 대한 데이터를 담고 있다
	 * 사용자의 정보나 권한 같은 데이터
	 */
	private Map<String, Object> createClaims(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("MemberId", user.getUsername());

		return claims;
	}

}
