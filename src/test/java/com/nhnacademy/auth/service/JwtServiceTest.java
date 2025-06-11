package com.nhnacademy.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
	private PasswordEncoder passwordEncoder;

	@Mock
	private SecretKey accessSecretKey;

	@Mock
	private SecretKey refreshSecretKey;

	@Mock
	private HttpServletResponse httpServletResponse;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	@DisplayName("JWT Access/Refresh 토큰 생성 및 Redis 저장 테스트")
	void testSaveToken() {

		// Given
		RequestJwtTokenDTO request = new RequestJwtTokenDTO("user");
		String dummyAccessToken = "dummyAccessToken";
		String dummyRefreshToken = "dummyRefreshToken";

		when(jwtTokenProvider.provideAccessToken(eq(accessSecretKey), anyLong(), any()))
			.thenReturn(dummyAccessToken);
		when(jwtTokenProvider.provideRefreshToken(eq(accessSecretKey), anyLong(), any()))
			.thenReturn(dummyRefreshToken);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

		// When
		ResponseJwtTokenDTO result = jwtService.saveToken(request, httpServletResponse);

		// Then
		verify(jwtTokenProvider, times(1)).provideAccessToken(eq(accessSecretKey), anyLong(), any());
		verify(jwtTokenProvider, times(1)).provideRefreshToken(eq(accessSecretKey), anyLong(), any());

		verify(httpServletResponse, times(1)).addCookie(cookieCaptor.capture());
		Cookie accessCookie = cookieCaptor.getValue();

		assertThat(accessCookie.getName()).isEqualTo(JwtRule.JWT_ISSUE_HEADER.getValue());
		assertThat(accessCookie.getValue()).isEqualTo(dummyAccessToken);
		assertThat(accessCookie.getMaxAge()).isEqualTo(10800000);
		assertThat(accessCookie.getPath()).isEqualTo("/");
		assertThat(accessCookie.isHttpOnly()).isTrue();
		assertThat(accessCookie.getSecure()).isTrue();

		verify(valueOperations, times(1)).set(
			eq(JwtRule.REFRESH_PREFIX.getValue() + ":" + request.getMemberId()),
			eq(dummyRefreshToken),
			eq(Duration.ofMillis(10800000))
		);

		assertThat(result.getMessage()).isEqualTo("Success");

	}
}