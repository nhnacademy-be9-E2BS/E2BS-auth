package com.nhnacademy.auth.service;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhnacademy.auth.jwt.provider.JwtTokenProvider;
import com.nhnacademy.auth.jwt.rule.JwtRule;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	private final SecretKey accessSecretKey;
	private final SecretKey refreshSecretKey;

	private static final long ACCESS_EXPIRATION = 1800000; // 30분
	private static final long REFRESH_EXPIRATION = 10800000; // 3시간

	private final PasswordEncoder passwordEncoder;

	/**
	 * AccessToken, RefreshToken 을 생성하고 쿠키와 레디스에 저장해서 응답하는 서비스 로직
	 */
	public ResponseJwtTokenDTO saveToken(RequestJwtTokenDTO request, HttpServletResponse response) {

		String encodedDummyPassword = passwordEncoder.encode("B7gf9Hn4s1c8XKLo6qTJzA!");

		User user = new User(request.getMemberId(),
			encodedDummyPassword,
			List.of()
		);

		/**
		 * Http 응답 쿠키로 클라이언트에 내려주는 구문
		 *
		 * 원래는 auth에서 바로 쿠키를 생성해줬으나 서버 자체가 달라서 다른 서버에서 쿠키 값을 공유할 수 없다
		 * Front로 accessToken 값을 보내주고 Front 브라우저에서 해당 쿠키를 만든다
		 *
		 */
		String accessToken = jwtTokenProvider.provideAccessToken(accessSecretKey,
			ACCESS_EXPIRATION, user); // accessExpiration 시간 10분 저장
		String refreshToken = jwtTokenProvider.provideRefreshToken(refreshSecretKey,
			REFRESH_EXPIRATION, user); // accessExpiration 시간 3시간 저장

		Cookie accessCookie = new Cookie(JwtRule.JWT_ISSUE_HEADER.getValue(), accessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setSecure(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(10800000); // 쿠키 유지 시간은 1시간, Access Token expiration 시간은 10분

		response.addCookie(accessCookie); // 헤더에 쿠키 정보 저장

		// Redis 에 토큰을 저장
		String redisKey = JwtRule.REFRESH_PREFIX.getValue() + ":" + request.getMemberId();
		redisTemplate.opsForValue().set(redisKey, refreshToken, Duration.ofMillis(REFRESH_EXPIRATION));

		return new ResponseJwtTokenDTO("Success");
	}

}
