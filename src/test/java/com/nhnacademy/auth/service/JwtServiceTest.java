package com.nhnacademy.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nhnacademy.auth.jwt.provider.JwtTokenProvider;
import com.nhnacademy.auth.jwt.rule.JwtRule;
import com.nhnacademy.auth.model.dto.request.RequestJwtTokenDTO;
import com.nhnacademy.auth.model.dto.response.ResponseJwtTokenDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private SecretKey accessSecretKey;

	@Mock
	private SecretKey refreshSecretKey;

	@Mock
	private HttpServletResponse response;

	@Captor
	private ArgumentCaptor<Cookie> cookieCaptor;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	@DisplayName("JWT Access/Refresh 토큰 생성 및 Redis 저장 테스트")
	void saveTokenTest() {

		// Given
		RequestJwtTokenDTO request = new RequestJwtTokenDTO("testUser");
		String accessToken = "access.jwt.token";
		String refreshToken = "refresh.jwt.token";

		when(jwtTokenProvider.provideAccessToken(any(), anyLong(), any())).thenReturn(accessToken);
		when(jwtTokenProvider.provideRefreshToken(any(), anyLong(), any())).thenReturn(refreshToken);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		// When
		ResponseJwtTokenDTO result = jwtService.saveToken(request, response);

		// Then
		assertThat(result).isNotNull();

		verify(jwtTokenProvider).provideAccessToken(any(), anyLong(), any());
		verify(jwtTokenProvider).provideRefreshToken(any(), anyLong(), any());

		verify(redisTemplate.opsForValue(), times(1))
			.set(eq(JwtRule.REFRESH_PREFIX.getValue() + ":" + request.getMemberId()), eq(refreshToken),
				eq(Duration.ofMillis(10800000)));

		verify(response, times(1)).addCookie(cookieCaptor.capture());

		Cookie cookie = cookieCaptor.getValue();
		assertThat(cookie.getName()).isEqualTo(JwtRule.JWT_ISSUE_HEADER.getValue());
		assertThat(cookie.getValue()).isEqualTo(accessToken);

	}
}