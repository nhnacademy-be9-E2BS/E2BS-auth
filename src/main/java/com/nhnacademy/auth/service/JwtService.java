package com.nhnacademy.auth.service;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.nhnacademy.auth.jwt.JwtRule;
import com.nhnacademy.auth.jwt.provider.JwtTokenProvider;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	private final SecretKey accessSecretKey;
	private final SecretKey refreshSecretKey;

	private final long accessExpiration = 600000;
	private final long refreshExpiration = 3600000;

	/**
	 * AccessToken, RefreshToken 을 생성하고 쿠키와 레디스에 저장해서 응답하는 서비스 로직
	 */
	public ResponseJwtTokenDTO saveToken(RequestJwtTokenDTO request, HttpServletResponse response) {

		User user = new User(request.getMemberId(),
			"notUsePassword",
			List.of(new SimpleGrantedAuthority(request.getMemberRole()))
		);

		String accessToken = jwtTokenProvider.provideAccessToken(accessSecretKey, accessExpiration, user);
		String refreshToken = jwtTokenProvider.provideRefreshToken(refreshSecretKey, refreshExpiration, user);

		/**
		 * Http 응답 쿠키로 클라이언트에 내려주는 구문
		 */
		ResponseCookie accessCookie = ResponseCookie.from(JwtRule.ACCESS_PREFIX.getValue(), accessToken)
			.httpOnly(true)    // 자바스크립트가 접근할 수 없게 하여 XSS 공격 방어한다
			.secure(true)        // HTTPS 에서만 전송되도록 한다
			.sameSite("None")    // 크로스 도메인 요청에서도 쿠키를 보내도록 설정한다
			.path("/")            // 쿠키가 유효한 경로를 지정한다 ("/"는 전체 경로에서 접근 가능)
			.maxAge(Duration.ofMillis(accessExpiration)) // 유효시간 10분
			.build();
		// 응답 헤더에 쿠키를 추가
		// HTTP 응답 헤더에 Set-Cookie 가 포함되고 브라우저는 응답을 받는 즉시 해당 쿠키를 저장한다
		response.addHeader(JwtRule.JWT_ISSUE_HEADER.getValue(), accessCookie.toString());

		// Redis 에 토큰을 저장
		String redisKey = JwtRule.REFRESH_PREFIX.getValue() + ":" + request.getMemberId();
		redisTemplate.opsForValue().set(redisKey, refreshToken, Duration.ofMillis(refreshExpiration));

		return new ResponseJwtTokenDTO(accessToken, refreshToken);
	}

}
